package com.cbank.atx.service;

import com.cbank.atx.domain.atx.Atx;
import com.cbank.atx.domain.atx.LocaleAtx;
import com.cbank.atx.domain.atx.Param;
import com.cbank.atx.domain.request.ParamValue;
import com.cbank.atx.domain.request.RequetsAtx;
import com.cbank.atx.domain.settings.Settings;
import com.cbank.atx.domain.user.UserAssignment;
import com.cbank.atx.enums.DataSource;
import com.cbank.atx.enums.RequestStatus;
import com.cbank.atx.exception.BusinessException;
import com.cbank.atx.exception.ResourceNotFoundException;
import com.cbank.atx.repository.AtxRepository;
import com.cbank.atx.repository.RequetsAtxRepository;
import com.cbank.atx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequetsAtxService {

    private final RequetsAtxRepository
            requetsAtxRepository;
    private final UserRepository userRepository;
    private final AtxRepository atxRepository;
    private final SettingsService settingsService;
    private final NotificationService
            notificationService;

    // ─────────────────────────────────────────
    // CRÉER une demande
    // → Status initial = PENDING
    // → Date de création = maintenant
    // ─────────────────────────────────────────
    public RequetsAtx create(
            RequetsAtx requetsAtx) {
        requetsAtx.setStatus(
                RequestStatus.PENDING);
        requetsAtx.setRequestedAt(new Date());
        return requetsAtxRepository
                .save(requetsAtx);
    }

    // ─────────────────────────────────────────
    // LIRE toutes les demandes
    // ─────────────────────────────────────────
    public List<RequetsAtx> getAll() {
        return requetsAtxRepository.findAll();
    }

    // ─────────────────────────────────────────
    // LIRE une demande par ID
    // → Si non trouvée → 404
    // ─────────────────────────────────────────
    public RequetsAtx getById(String id) {
        return requetsAtxRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Demande non trouvée : "
                                        + id));
    }

    // ─────────────────────────────────────────
    // LIRE les demandes par statut
    // ─────────────────────────────────────────
    public List<RequetsAtx> getByStatus(
            RequestStatus status) {
        return requetsAtxRepository
                .findByStatus(status);
    }

    // ─────────────────────────────────────────
    // LIRE les demandes d'un agent
    // ─────────────────────────────────────────
    public List<RequetsAtx> getByAgent(
            String userId) {
        return requetsAtxRepository
                .findByAssignedTo(userId);
    }

    // ─────────────────────────────────────────
    // ASSIGNER une demande à un agent
    // → Vérifie que l'agent existe et est actif
    // → Status passe PENDING → PROCESSING
    // → Notifie l'agent par email
    // ─────────────────────────────────────────
    public RequetsAtx assign(
            String requestId,
            String userId) {

        // Charge la demande
        RequetsAtx request = getById(requestId);

        // Vérifie que l'agent existe et est actif
        // → Si non trouvé ou inactif → 404
        var agent = userRepository
                .findById(userId)
                .filter(u -> u.getActive())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Agent non trouvé "
                                        + "ou inactif : " + userId));

        // Crée l'assignation
        UserAssignment assignment =
                new UserAssignment();
        assignment.setUserId(userId);
        assignment.setStatus(
                RequestStatus.PROCESSING);
        assignment.setAssignedAt(new Date());
        request.getAssignments().add(assignment);
        request.setAssignedTo(userId);
        request.setStatus(
                RequestStatus.PROCESSING);

        // Sauvegarde dans MongoDB
        RequetsAtx saved =
                requetsAtxRepository.save(request);

        // ✅ Notifie l'agent par email
        notificationService
                .notifyRequestAssignment(
                        agent.getEmail(),
                        requestId
                );

        return saved;
    }

    // ─────────────────────────────────────────
    // LIVRER une demande
    // → Status doit être PROCESSING
    // → Status passe PROCESSING → DELIVERED
    // → Notifie le client par email
    // ─────────────────────────────────────────
    public RequetsAtx deliver(String requestId) {

        RequetsAtx request = getById(requestId);

        // Vérifie le statut
        // → Si pas PROCESSING → erreur métier
        if (request.getStatus()
                != RequestStatus.PROCESSING) {
            throw new BusinessException(
                    "La demande n'est pas "
                            + "en cours de traitement !");
        }

        request.setStatus(
                RequestStatus.DELIVERED);
        request.setDeliveredAt(new Date());

        // Ferme l'assignation en cours
        request.getAssignments().stream()
                .filter(a ->
                        a.getEndedAt() == null)
                .findFirst()
                .ifPresent(a ->
                        a.setEndedAt(new Date()));

        // Sauvegarde dans MongoDB
        RequetsAtx saved =
                requetsAtxRepository.save(request);

        // ✅ Notifie le client par email
        notificationService
                .notifyRequestDelivered(
                        request.getCustomer(),
                        requestId
                );

        return saved;
    }

    // ─────────────────────────────────────────
    // CLÔTURER une demande
    // → Status doit être DELIVERED
    // → Status passe DELIVERED → ENDED
    // ─────────────────────────────────────────
    public RequetsAtx close(String requestId) {

        RequetsAtx request = getById(requestId);

        // Vérifie le statut
        // → Si pas DELIVERED → erreur métier
        if (request.getStatus()
                != RequestStatus.DELIVERED) {
            throw new BusinessException(
                    "La demande n'a pas "
                            + "encore été livrée !");
        }

        request.setStatus(RequestStatus.ENDED);
        return requetsAtxRepository.save(request);
    }

    // ─────────────────────────────────────────
    // SUPPRIMER une demande
    // ─────────────────────────────────────────
    public void delete(String id) {
        getById(id);
        requetsAtxRepository.deleteById(id);
    }

    // ─────────────────────────────────────────
    // REMPLIR les paramètres d'une demande
    // → Charge tous les params de l'attestation
    // → Pour chaque param :
    //   MANUAL → valeur saisie par le BO
    //   DB     → valeur récupérée via Node.js
    // ─────────────────────────────────────────
    public RequetsAtx fillParams(
            String requestId,
            String lang,
            Map<String, String> manualValues) {

        RequetsAtx request = getById(requestId);

        Atx atx = atxRepository
                .findById(request.getAtxId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attestation non trouvée !"));

        LocaleAtx locale =
                atx.getLocales().get(lang);
        if (locale == null) {
            locale = atx.getLocales().get("fr");
        }

        request.getParamsValues().clear();

        for (Param param : locale.getParams()) {
            ParamValue paramValue =
                    new ParamValue();
            paramValue.setParamId(
                    param.getName());

            if (param.getDataSource()
                    == DataSource.MANUAL) {
                String value = manualValues
                        .getOrDefault(
                                param.getName(), "");
                paramValue.setValue(value);
            } else if (param.getDataSource()
                    == DataSource.DB) {
                String value = getValueFromNodeJS(
                        param.getDataSourceValue(),
                        request.getAccountNumber()
                );
                paramValue.setValue(value);
            }

            request.getParamsValues()
                    .add(paramValue);
        }

        return requetsAtxRepository.save(request);
    }

    // ─────────────────────────────────────────
    // EXÉCUTER la requête SQL pour UN param
    // → Appelé quand le BO clique "Exécuter"
    // → Appelle Node.js pour récupérer la valeur
    // ─────────────────────────────────────────
    public Map<String, String> executeParam(
            String requestId,
            String paramName) {

        RequetsAtx request = getById(requestId);

        Atx atx = atxRepository
                .findById(request.getAtxId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attestation non trouvée !"));

        Param param = atx.getLocales()
                .values().stream()
                .flatMap(l ->
                        l.getParams().stream())
                .filter(p -> p.getName()
                        .equals(paramName))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Paramètre non trouvé : "
                                        + paramName));

        String value = getValueFromNodeJS(
                param.getDataSourceValue(),
                request.getAccountNumber()
        );

        saveParamValue(request, paramName, value);

        Map<String, String> result =
                new HashMap<>();
        result.put("paramName", paramName);
        result.put("value", value);
        return result;
    }

    // ─────────────────────────────────────────
    // SAUVEGARDER une valeur manuelle
    // → Appelé quand le BO saisit manuellement
    // ─────────────────────────────────────────
    public RequetsAtx saveParam(
            String requestId,
            String paramName,
            String value) {

        RequetsAtx request = getById(requestId);
        saveParamValue(request, paramName, value);
        return requetsAtxRepository.save(request);
    }

    // ─────────────────────────────────────────
    // MÉTHODE UTILITAIRE — Sauvegarder param
    // → Met à jour si existe déjà
    // → Crée si nouveau param
    // ─────────────────────────────────────────
    private void saveParamValue(
            RequetsAtx request,
            String paramName,
            String value) {

        ParamValue existing = request
                .getParamsValues().stream()
                .filter(pv -> pv.getParamId()
                        .equals(paramName))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setValue(value);
        } else {
            ParamValue paramValue =
                    new ParamValue();
            paramValue.setParamId(paramName);
            paramValue.setValue(value);
            request.getParamsValues()
                    .add(paramValue);
        }

        requetsAtxRepository.save(request);
    }

    // ─────────────────────────────────────────
    // APPELER Node.js pour requête SQL
    // → Envoie la requête et le numéro de compte
    // → Retourne la valeur récupérée
    // → Si Node.js non disponible →
    //   retourne "VALEUR_NON_DISPONIBLE"
    // ─────────────────────────────────────────
    private String getValueFromNodeJS(
            String query,
            String accountNumber) {

        try {
            RestTemplate restTemplate =
                    new RestTemplate();
            Settings settings =
                    settingsService.get();

            Map<String, String> body =
                    new HashMap<>();
            body.put("query", query);
            body.put("param", accountNumber);
            body.put("sgbd",
                    settings.getBank()
                            .getDbmsServer());
            body.put("server",
                    settings.getBank()
                            .getDbmsServer());
            body.put("dbName",
                    settings.getBank()
                            .getDbmsName());
            body.put("username",
                    settings.getBank()
                            .getDbmsUsername());
            body.put("password",
                    settings.getBank()
                            .getDbmsPassword());

            Map response = restTemplate
                    .postForObject(
                            "http://localhost:3008/query",
                            body,
                            Map.class
                    );

            if (response != null
                    && response
                    .containsKey("value")) {
                return (String) response
                        .get("value");
            }

            return "";

        } catch (Exception e) {
            System.out.println(
                    "⚠️ Node.js non disponible : "
                            + e.getMessage());
            return "VALEUR_NON_DISPONIBLE";
        }
    }
}

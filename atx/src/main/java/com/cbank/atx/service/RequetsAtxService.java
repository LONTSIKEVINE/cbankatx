package com.cbank.atx.service;

import com.cbank.atx.domain.atx.Atx;
import com.cbank.atx.domain.atx.LocaleAtx;
import com.cbank.atx.domain.atx.Param;
import com.cbank.atx.domain.request.ParamValue;
import com.cbank.atx.domain.request.RequetsAtx;
import com.cbank.atx.domain.user.UserAssignment;
import com.cbank.atx.enums.DataSource;
import com.cbank.atx.enums.RequestStatus;
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
import com.cbank.atx.domain.settings.Settings;

@Service
@RequiredArgsConstructor
public class RequetsAtxService {

    private final RequetsAtxRepository requetsAtxRepository;
    private final UserRepository userRepository;
    private final AtxRepository atxRepository;
    private final SettingsService settingsService;

    // ─────────────────────────────────────────
    // CRÉER une demande
    // ─────────────────────────────────────────
    public RequetsAtx create(RequetsAtx requetsAtx) {
        requetsAtx.setStatus(RequestStatus.PENDING);
        requetsAtx.setRequestedAt(new Date());
        return requetsAtxRepository.save(requetsAtx);
    }

    // ─────────────────────────────────────────
    // LIRE toutes les demandes
    // ─────────────────────────────────────────
    public List<RequetsAtx> getAll() {
        return requetsAtxRepository.findAll();
    }

    // ─────────────────────────────────────────
    // LIRE une demande par ID
    // ─────────────────────────────────────────
    public RequetsAtx getById(String id) {
        return requetsAtxRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Demande non trouvée : " + id));
    }

    // ─────────────────────────────────────────
    // LIRE les demandes par statut
    // ─────────────────────────────────────────
    public List<RequetsAtx> getByStatus(
            RequestStatus status) {
        return requetsAtxRepository.findByStatus(status);
    }

    // ─────────────────────────────────────────
    // LIRE les demandes d'un agent
    // ─────────────────────────────────────────
    public List<RequetsAtx> getByAgent(String userId) {
        return requetsAtxRepository
                .findByAssignedTo(userId);
    }

    // ─────────────────────────────────────────
    // ASSIGNER une demande à un agent
    // ─────────────────────────────────────────
    public RequetsAtx assign(
            String requestId,
            String userId) {

        RequetsAtx request = getById(requestId);

        userRepository.findById(userId)
                .filter(u -> u.getActive())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Agent non trouvé ou inactif : "
                                        + userId));

        UserAssignment assignment = new UserAssignment();
        assignment.setUserId(userId);
        assignment.setStatus(RequestStatus.PROCESSING);
        assignment.setAssignedAt(new Date());
        request.getAssignments().add(assignment);
        request.setAssignedTo(userId);
        request.setStatus(RequestStatus.PROCESSING);

        return requetsAtxRepository.save(request);
    }

    // ─────────────────────────────────────────
    // LIVRER une demande
    // ─────────────────────────────────────────
    public RequetsAtx deliver(String requestId) {

        RequetsAtx request = getById(requestId);

        if (request.getStatus()
                != RequestStatus.PROCESSING) {
            throw new RuntimeException(
                    "La demande n'est pas "
                            + "en cours de traitement !");
        }

        request.setStatus(RequestStatus.DELIVERED);
        request.setDeliveredAt(new Date());

        request.getAssignments().stream()
                .filter(a -> a.getEndedAt() == null)
                .findFirst()
                .ifPresent(a ->
                        a.setEndedAt(new Date()));

        return requetsAtxRepository.save(request);
    }

    // ─────────────────────────────────────────
    // CLÔTURER une demande
    // ─────────────────────────────────────────
    public RequetsAtx close(String requestId) {

        RequetsAtx request = getById(requestId);

        if (request.getStatus()
                != RequestStatus.DELIVERED) {
            throw new RuntimeException(
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
    // ─────────────────────────────────────────
    public RequetsAtx fillParams(
            String requestId,
            String lang,
            Map<String, String> manualValues) {

        // Étape 1 : charger la demande
        RequetsAtx request = getById(requestId);

        // Étape 2 : charger l'attestation
        Atx atx = atxRepository
                .findById(request.getAtxId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Attestation non trouvée !"));

        // Étape 3 : récupérer la locale
        LocaleAtx locale = atx.getLocales().get(lang);
        if (locale == null) {
            locale = atx.getLocales().get("fr");
        }

        // Étape 4 : vider les anciennes valeurs
        request.getParamsValues().clear();

        // Étape 5 : traiter chaque param
        for (Param param : locale.getParams()) {

            ParamValue paramValue = new ParamValue();
            paramValue.setParamId(param.getName());

            if (param.getDataSource() == DataSource.MANUAL) {
                // Param MANUAL → valeur saisie par le BO
                String value = manualValues
                        .getOrDefault(
                                param.getName(), "");
                paramValue.setValue(value);

            } else if (param.getDataSource()
                    == DataSource.DB) {
                // Param DB → appeler Node.js
                String value = getValueFromNodeJS(
                        param.getDataSourceValue(),
                        request.getAccountNumber()
                );
                paramValue.setValue(value);
            }

            request.getParamsValues().add(paramValue);
        }

        // Étape 6 : sauvegarder dans MongoDB
        return requetsAtxRepository.save(request);
    }

    // ─────────────────────────────────────────
    // APPELER Node.js pour exécuter une requête SQL
    // ─────────────────────────────────────────
    // ─────────────────────────────────────────
// APPELER Node.js pour exécuter une requête SQL
// ─────────────────────────────────────────
    private String getValueFromNodeJS(
            String query,
            String accountNumber) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            // Récupérer les infos de connexion
            // depuis Settings MongoDB
            Settings settings = settingsService.get();

            // Construire le body de la requête
            Map<String, String> body = new HashMap<>();
            body.put("query", query);
            body.put("param", accountNumber);
            body.put("sgbd",
                    settings.getBank().getDbmsServer());
            body.put("server",
                    settings.getBank().getDbmsServer());
            body.put("dbName",
                    settings.getBank().getDbmsName());
            body.put("username",
                    settings.getBank().getDbmsUsername());
            body.put("password",
                    settings.getBank().getDbmsPassword());

            // Appeler Node.js sur le port 3008
            Map response = restTemplate.postForObject(
                    "http://localhost:3008/query",
                    body,
                    Map.class
            );

            if (response != null
                    && response.containsKey("value")) {
                return (String) response.get("value");
            }

            return "";

        } catch (Exception e) {
            System.out.println(
                    "Node.js non disponible : "
                            + e.getMessage());
            return "VALEUR_NON_DISPONIBLE";
        }
    }

    // ─────────────────────────────────────────
// EXÉCUTER la requête SQL pour UN param
// → appelé quand le BO clique "Exécuter"
// ─────────────────────────────────────────
    public Map<String, String> executeParam(
            String requestId,
            String paramName) {

        // Charger la demande
        RequetsAtx request = getById(requestId);

        // Charger l'attestation
        Atx atx = atxRepository
                .findById(request.getAtxId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Attestation non trouvée !"));

        // Trouver le param par son nom
        Param param = atx.getLocales()
                .values()
                .stream()
                .flatMap(l -> l.getParams().stream())
                .filter(p -> p.getName()
                        .equals(paramName))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Paramètre non trouvé : "
                                        + paramName));

        // Exécuter la requête SQL via Node.js
        String value = getValueFromNodeJS(
                param.getDataSourceValue(),
                request.getAccountNumber()
        );

        // Sauvegarder la valeur dans MongoDB
        saveParamValue(request, paramName, value);

        // Retourner la valeur à React
        Map<String, String> result = new HashMap<>();
        result.put("paramName", paramName);
        result.put("value", value);
        return result;
    }

    // ─────────────────────────────────────────
// SAUVEGARDER une valeur manuelle
// → appelé quand le BO saisit manuellement
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
// ─────────────────────────────────────────
    private void saveParamValue(
            RequetsAtx request,
            String paramName,
            String value) {

        // Chercher si le param existe déjà
        ParamValue existing = request
                .getParamsValues()
                .stream()
                .filter(pv -> pv.getParamId()
                        .equals(paramName))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            // Mettre à jour la valeur existante
            existing.setValue(value);
        } else {
            // Créer une nouvelle entrée
            ParamValue paramValue = new ParamValue();
            paramValue.setParamId(paramName);
            paramValue.setValue(value);
            request.getParamsValues().add(paramValue);
        }

        requetsAtxRepository.save(request);
    }

    }
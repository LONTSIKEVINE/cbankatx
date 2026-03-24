package com.cbank.atx.service;

import com.cbank.atx.domain.atx.Atx;
import com.cbank.atx.domain.atx.LocaleAtx;
import com.cbank.atx.domain.atx.Param;
import com.cbank.atx.domain.request.ParamValue;
import com.cbank.atx.domain.request.RequetsAtx;
import com.cbank.atx.exception.ResourceNotFoundException;
import com.cbank.atx.repository.AtxRepository;
import com.cbank.atx.repository.RequetsAtxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocxService {

    private final RequetsAtxRepository
            requetsAtxRepository;
    private final AtxRepository atxRepository;

    // ─────────────────────────────────────────
    // GÉNÉRER le document Word
    // → Charge la demande et l'attestation
    // → Construit les données du template
    // → Appelle Node.js /generate
    // → Retourne le fichier en bytes
    // ─────────────────────────────────────────
    public byte[] generateDocument(
            String requestId,
            String lang) throws Exception {

        // Étape 1 : charger la demande
        RequetsAtx request = requetsAtxRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Demande non trouvée : "
                                        + requestId));

        // Étape 2 : charger l'attestation
        Atx atx = atxRepository
                .findById(request.getAtxId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attestation non trouvée !"));

        // Étape 3 : récupérer la locale
        // → Cherche la langue demandée
        // → Si non trouvée → fallback français
        LocaleAtx locale = atx.getLocales()
                .get(lang);
        if (locale == null) {
            locale = atx.getLocales().get("fr");
        }

        // Étape 4 : construire les données
        // pour Docxtemplater
        Map<String, Object> data =
                buildTemplateData(
                        request, locale);

        // Log pour vérifier les données
        System.out.println(
                "📋 Données template : " + data);

        // Étape 5 : appeler Node.js
        return callNodeJsGenerate(
                locale.getTemplateUrl(),
                data
        );
    }

    // ─────────────────────────────────────────
    // CONSTRUIRE les données du template
    // → Mappe chaque paramId → valeur
    // → Utilise le paramId directement
    //   comme clé dans la Map
    // ─────────────────────────────────────────
    private Map<String, Object> buildTemplateData(
            RequetsAtx request,
            LocaleAtx locale) {

        Map<String, Object> data = new HashMap<>();

        // Ajoute les données système
        data.put("REQUEST_ID",
                request.getId() != null
                        ? request.getId() : "");
        data.put("CUSTOMER",
                request.getCustomer() != null
                        ? request.getCustomer() : "");
        data.put("ACCOUNT_NUMBER",
                request.getAccountNumber() != null
                        ? request.getAccountNumber() : "");

        // Pour chaque param de l'attestation
        for (Param param : locale.getParams()) {

            // Cherche la valeur dans paramsValues
            String value = request
                    .getParamsValues()
                    .stream()
                    .filter(pv -> pv.getParamId()
                            .equals(param.getName()))
                    .map(ParamValue::getValue)
                    .findFirst()
                    .orElse("");

            // Nettoie le nom de la variable
            // Enlève les {{ }} si présents
            // Ex: "{{NOM_CLIENT}}" → "NOM_CLIENT"
            // Ex: "NOM_CLIENT"    → "NOM_CLIENT"
            String varName = param.getName()
                    .replace("{{", "")
                    .replace("}}", "")
                    .replace("{", "")
                    .replace("}", "")
                    .trim();

            // Ajoute dans les données
            // avec valeur vide si null
            data.put(varName,
                    value != null ? value : "");

            // Log pour vérifier le mapping
            System.out.println(
                    "🔑 Variable : " + varName
                            + " = " + value);
        }

        return data;
    }

    // ─────────────────────────────────────────
    // APPELER Node.js /generate
    // → Envoie le nom du template
    //   et les données
    // → Reçoit le document en base64
    // → Décode et retourne en bytes
    // ─────────────────────────────────────────
    private byte[] callNodeJsGenerate(
            String templateUrl,
            Map<String, Object> data)
            throws Exception {

        try {
            RestTemplate restTemplate =
                    new RestTemplate();

            // Extrait juste le nom du fichier
            // Ex: "/templates/solde-fr.docx"
            //   → "solde-fr.docx"
            String templateName = templateUrl;
            if (templateName == null
                    || templateName.isEmpty()) {
                templateName = "solde-fr.docx";
            } else {
                templateName = templateName
                        .substring(
                                templateName
                                        .lastIndexOf("/") + 1);
            }

            // Construit le body
            Map<String, Object> body =
                    new HashMap<>();
            body.put("templateName", templateName);
            body.put("data", data);

            System.out.println(
                    "📤 Appel Node.js /generate"
                            + " avec template : "
                            + templateName);

            // Appelle Node.js
            Map response = restTemplate
                    .postForObject(
                            "http://localhost:3008/generate",
                            body,
                            Map.class
                    );

            if (response != null
                    && response
                    .containsKey("document")) {

                String base64 = (String) response
                        .get("document");

                System.out.println(
                        "✅ Document reçu de Node.js !");

                return Base64.getDecoder()
                        .decode(base64);
            }

            throw new RuntimeException(
                    "Réponse invalide de Node.js !");

        } catch (Exception e) {
            System.out.println(
                    "⚠️ Erreur Node.js : "
                            + e.getMessage());
            throw new RuntimeException(
                    "Erreur génération document : "
                            + e.getMessage());
        }
    }
}
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
        LocaleAtx locale = atx.getLocales()
                .get(lang);
        if (locale == null) {
            locale = atx.getLocales().get("fr");
        }

        // Étape 4 : construire les données
        // pour Docxtemplater
        // → Map de toutes les variables
        //   du template avec leurs valeurs
        Map<String, Object> data =
                buildTemplateData(
                        request, locale);

        // Étape 5 : appeler Node.js
        // pour générer le document
        return callNodeJsGenerate(
                locale.getTemplateUrl(),
                data
        );
    }

    // ─────────────────────────────────────────
    // CONSTRUIRE les données du template
    // → Pour chaque param de l'attestation
    //   cherche sa valeur dans paramsValues
    // → Construit la Map data pour
    //   Docxtemplater
    // ─────────────────────────────────────────
    private Map<String, Object> buildTemplateData(
            RequetsAtx request,
            LocaleAtx locale) {

        Map<String, Object> data = new HashMap<>();

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

            // Extrait le nom de la variable
            // sans les {{ }}
            // Ex: "{{NOM_CLIENT}}" → "NOM_CLIENT"
            String varName = param.getName()
                    .replace("{{", "")
                    .replace("}}", "")
                    .trim();

            data.put(varName, value);
        }

        // Ajoute des données système
        data.put("REQUEST_ID",
                request.getId());
        data.put("CUSTOMER",
                request.getCustomer());
        data.put("ACCOUNT_NUMBER",
                request.getAccountNumber());

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

            // Nom du fichier template
            String templateName = templateUrl;
            if (templateName == null
                    || templateName.isEmpty()) {
                templateName = "solde-fr.docx";
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

                // Décode le base64
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
                    "⚠️ Node.js non disponible : "
                            + e.getMessage());
            throw new RuntimeException(
                    "Erreur génération document : "
                            + e.getMessage());
        }
    }
}
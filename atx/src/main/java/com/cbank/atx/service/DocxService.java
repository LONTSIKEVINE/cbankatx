
package com.cbank.atx.service;

import com.cbank.atx.domain.atx.Atx;
import com.cbank.atx.domain.atx.LocaleAtx;
import com.cbank.atx.domain.atx.Param;
import com.cbank.atx.domain.request.ParamValue;
import com.cbank.atx.domain.request.RequetsAtx;
import com.cbank.atx.repository.AtxRepository;
import com.cbank.atx.repository.RequetsAtxRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import java.io.*;
        import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocxService {

    private final RequetsAtxRepository
            requetsAtxRepository;
    private final AtxRepository atxRepository;

    // ─────────────────────────────────────────
    // GÉNÉRER le document Docx
    // pour une demande donnée
    // ─────────────────────────────────────────
    public byte[] generateDocument(
            String requestId,
            String lang) throws Exception {

        // Étape 1 : charger la demande
        RequetsAtx request = requetsAtxRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Demande non trouvée : "
                                        + requestId));

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

        // Étape 4 : construire la Map des variables
        // {{NOM_CLIENT}} → "Jean Dupont"
        Map<String, String> variables =
                buildVariablesMap(
                        request,
                        locale
                );

        // Étape 5 : charger et remplir le template
        return fillTemplate(
                locale.getTemplateUrl(),
                variables
        );
    }

    // ─────────────────────────────────────────
    // CONSTRUIRE la Map des variables
    // ─────────────────────────────────────────
    private Map<String, String> buildVariablesMap(
            RequetsAtx request,
            LocaleAtx locale) {

        Map<String, String> variables =
                new HashMap<>();

        // Pour chaque param de l'attestation
        for (Param param : locale.getParams()) {

            // Chercher la valeur dans paramsValues
            String value = request.getParamsValues()
                    .stream()
                    .filter(pv -> pv.getParamId()
                            .equals(param.getName()))
                    .map(ParamValue::getValue)
                    .findFirst()
                    .orElse("");

            // Ajouter dans la Map :
            // "{{NOM_CLIENT}}" → "Jean Dupont"
            variables.put(
                    param.getRawTemplateVariable(),
                    value
            );
        }

        return variables;
    }

    // ─────────────────────────────────────────
    // REMPLIR le template avec les variables
    // ─────────────────────────────────────────
    private byte[] fillTemplate(
            String templateUrl,
            Map<String, String> variables)
            throws Exception {

        // Charger le template depuis le dossier
        // resources/templates/
        InputStream templateStream =
                getClass().getResourceAsStream(
                        "/templates/"
                                + templateUrl.replace(
                                "/templates/", "")
                );

        // Si le template n'existe pas →
        // créer un document simple
        if (templateStream == null) {
            return createSimpleDocument(variables);
        }

        // Ouvrir le document Word
        XWPFDocument document =
                new XWPFDocument(templateStream);

        // Remplacer les variables dans chaque
        // paragraphe du document
        for (XWPFParagraph paragraph
                : document.getParagraphs()) {
            replaceParagraph(paragraph, variables);
        }

        // Convertir en bytes
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();

        return outputStream.toByteArray();
    }

    // ─────────────────────────────────────────
    // REMPLACER les variables dans un paragraphe
    // ─────────────────────────────────────────
    private void replaceParagraph(
            XWPFParagraph paragraph,
            Map<String, String> variables) {

        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                // Remplacer chaque variable
                for (Map.Entry<String, String>
                        entry : variables.entrySet()) {
                    text = text.replace(
                            entry.getKey(),
                            entry.getValue()
                    );
                }
                run.setText(text, 0);
            }
        }
    }

    // ─────────────────────────────────────────
    // CRÉER un document simple si pas de template
    // ─────────────────────────────────────────
    private byte[] createSimpleDocument(
            Map<String, String> variables)
            throws Exception {

        XWPFDocument document = new XWPFDocument();

        // Titre
        XWPFParagraph title =
                document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("ATTESTATION BANCAIRE");
        titleRun.setBold(true);
        titleRun.setFontSize(16);

        // Contenu — une ligne par variable
        for (Map.Entry<String, String>
                entry : variables.entrySet()) {
            XWPFParagraph para =
                    document.createParagraph();
            XWPFRun run = para.createRun();
            run.setText(
                    entry.getKey()
                            .replace("{{", "")
                            .replace("}}", "")
                            + " : "
                            + entry.getValue()
            );
        }

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();

        return outputStream.toByteArray();
    }
}

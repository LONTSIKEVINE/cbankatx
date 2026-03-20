package com.cbank.atx.controller;
import com.cbank.atx.service.DocxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocxService docxService;

    // ─────────────────────────────────────────
    // GET /api/documents/generate/{requestId}
    // → Génère le document Docx
    //   pour une demande donnée
    // → Retourne le fichier .docx
    //   téléchargeable
    // ─────────────────────────────────────────
    @GetMapping("/generate/{requestId}")
    public ResponseEntity<byte[]> generate(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "fr")
            String lang) {

        try {
            // Générer le document
            byte[] document =
                    docxService.generateDocument(
                            requestId, lang);

            // Configurer les headers
            // pour le téléchargement
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData(
                    "attachment",
                    "attestation_"
                            + requestId + ".docx"
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(document);

        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}
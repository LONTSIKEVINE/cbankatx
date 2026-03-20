package com.cbank.atx.controller;

import com.cbank.atx.service.FlatFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FlatFileController {

    private final FlatFileService flatFileService;

    // ─────────────────────────────────────────
    // GET /api/files/flat/generate/{requestId}
    // → Génère le fichier plat
    //   pour une demande donnée
    // → Retourne le fichier texte
    //   téléchargeable
    // ─────────────────────────────────────────
    @GetMapping("/flat/generate/{requestId}")
    public ResponseEntity<String> generate(
            @PathVariable String requestId) {

        try {
            // Générer le fichier plat
            String content = flatFileService
                    .generateFlatFile(requestId);

            // Configurer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData(
                    "attachment",
                    "flat_"
                            + requestId + ".txt"
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(content);

        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body("Erreur : "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // GET /api/files/flat/preview/{requestId}
    // → Prévisualise le fichier plat
    //   sans téléchargement
    // ─────────────────────────────────────────
    @GetMapping("/flat/preview/{requestId}")
    public ResponseEntity<String> preview(
            @PathVariable String requestId) {

        try {
            String content = flatFileService
                    .generateFlatFile(requestId);
            return ResponseEntity.ok(content);

        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body("Erreur : "
                            + e.getMessage());
        }
    }
}
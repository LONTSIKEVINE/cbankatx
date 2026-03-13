package com.cbank.atx.controller;

import com.cbank.atx.domain.atx.Atx;
import com.cbank.atx.domain.atx.LocaleAtx;
import com.cbank.atx.domain.atx.Param;
import com.cbank.atx.service.AtxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/atxs")
@RequiredArgsConstructor
public class AtxController {

    // Le service qui contient la logique métier des attestations
    private final AtxService atxService;

    // ─────────────────────────────────────────────────
    // POST /api/atxs
    // → Crée un nouveau type d'attestation
    // → Postman envoie :
    //   {
    //     "locales": {
    //       "fr": {
    //         "label": "Attestation de Solde",
    //         "atxCode": "ATX-SOL-001",
    //         "price": 2500,
    //         "taxable": true,
    //         "taxePercentage": 19
    //       }
    //     }
    //   }
    // ─────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Atx> create(
            @RequestBody Atx atx) {
        // Le service vérifie que le code est unique
        return ResponseEntity.ok(atxService.create(atx));
    }

    // ─────────────────────────────────────────────────
    // GET /api/atxs
    // → Retourne tous les types d'attestations
    // ─────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Atx>> getAll() {
        return ResponseEntity.ok(atxService.getAll());
    }

    // ─────────────────────────────────────────────────
    // GET /api/atxs/{id}
    // → Retourne UN type d'attestation par son ID
    // ─────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Atx> getById(
            @PathVariable String id) {
        return ResponseEntity.ok(atxService.getById(id));
    }

    // ─────────────────────────────────────────────────
    // GET /api/atxs/{id}/locale/{lang}
    // → Retourne la version localisée d'une attestation
    // → Exemple : GET /api/atxs/64a1b2c3/locale/fr
    //   retourne la version française
    // ─────────────────────────────────────────────────
    @GetMapping("/{id}/locale/{lang}")
    public ResponseEntity<LocaleAtx> getLocale(
            @PathVariable String id,   // ID de l'attestation
            @PathVariable String lang) { // "fr", "ar", "en"...
        return ResponseEntity.ok(
                atxService.getLocale(id, lang));
    }

    // ─────────────────────────────────────────────────
    // GET /api/atxs/{id}/price/{lang}
    // → Calcule et retourne le prix TTC
    // → Exemple : GET /api/atxs/64a1b2c3/price/fr
    //   retourne 2975.0 (2500 + 19% de taxe)
    // ─────────────────────────────────────────────────
    @GetMapping("/{id}/price/{lang}")
    public ResponseEntity<Double> getPrice(
            @PathVariable String id,
            @PathVariable String lang) {
        return ResponseEntity.ok(
                atxService.calculateTTC(id, lang));
    }

    // ─────────────────────────────────────────────────
    // GET /api/atxs/{id}/params/{lang}
    // → Retourne les paramètres à saisir manuellement
    // → Utilisé pour afficher le formulaire de saisie
    //   lors de la création d'une demande
    // ─────────────────────────────────────────────────
    @GetMapping("/{id}/params/{lang}")
    public ResponseEntity<List<Param>> getManualParams(
            @PathVariable String id,
            @PathVariable String lang) {
        return ResponseEntity.ok(
                atxService.getManualParams(id, lang));
    }

    // ─────────────────────────────────────────────────
    // PUT /api/atxs/{id}
    // → Modifie un type d'attestation
    // ─────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Atx> update(
            @PathVariable String id,
            @RequestBody Atx atx) {
        return ResponseEntity.ok(atxService.update(id, atx));
    }

    // ─────────────────────────────────────────────────
    // DELETE /api/atxs/{id}
    // → Supprime un type d'attestation
    // ─────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id) {
        atxService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
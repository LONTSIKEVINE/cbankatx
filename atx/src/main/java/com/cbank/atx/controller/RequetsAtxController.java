package com.cbank.atx.controller;
import java.util.Map;

import com.cbank.atx.domain.request.RequetsAtx;
import com.cbank.atx.enums.RequestStatus;
import com.cbank.atx.service.RequetsAtxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequetsAtxController {

    // Le service qui gère le workflow des demandes
    private final RequetsAtxService requetsAtxService;

    // ─────────────────────────────────────────────────
    // POST /api/requests
    // → Crée une nouvelle demande d'attestation
    // → Postman envoie :
    //   {
    //     "reason": "Demande de visa",
    //     "customer": "CLI-001",
    //     "accountNumber": "CM21-001",
    //     "atxId": "64a1b2c3",
    //     "createdBy": "64a1b2c4"
    //   }
    // → Le service ajoute automatiquement :
    //   status = PENDING
    //   requestedAt = date du jour
    // ─────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<RequetsAtx> create(
            @RequestBody RequetsAtx requetsAtx) {
        return ResponseEntity.ok(
                requetsAtxService.create(requetsAtx));
    }

    // ─────────────────────────────────────────────────
    // GET /api/requests
    // → Retourne toutes les demandes
    // ─────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<RequetsAtx>> getAll() {
        return ResponseEntity.ok(requetsAtxService.getAll());
    }

    // ─────────────────────────────────────────────────
    // GET /api/requests/{id}
    // → Retourne UNE seule demande par son ID
    // ─────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<RequetsAtx> getById(
            @PathVariable String id) {
        return ResponseEntity.ok(
                requetsAtxService.getById(id));
    }

    // ─────────────────────────────────────────────────
    // GET /api/requests/status/{status}
    // → Retourne les demandes par statut
    // → Exemple : GET /api/requests/status/PENDING
    //   retourne toutes les demandes en attente
    // ─────────────────────────────────────────────────
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequetsAtx>> getByStatus(
            // Spring convertit automatiquement
            // "PENDING" en RequestStatus.PENDING
            @PathVariable RequestStatus status) {
        return ResponseEntity.ok(
                requetsAtxService.getByStatus(status));
    }

    // ─────────────────────────────────────────────────
    // GET /api/requests/agent/{userId}
    // → Retourne les demandes assignées à un agent
    // → Exemple : GET /api/requests/agent/64a1b2c3
    //   retourne toutes les demandes de Jean Dupont
    // ─────────────────────────────────────────────────
    @GetMapping("/agent/{userId}")
    public ResponseEntity<List<RequetsAtx>> getByAgent(
            @PathVariable String userId) {
        return ResponseEntity.ok(
                requetsAtxService.getByAgent(userId));
    }

    // ─────────────────────────────────────────────────
    // PUT /api/requests/{id}/assign?userId={userId}
    // → Assigne une demande à un agent
    // → Exemple :
    //   PUT /api/requests/64a1b2c3/assign?userId=64a1b2c4
    // → Le service vérifie que l'agent est actif
    // → status passe de PENDING à PROCESSING
    // ─────────────────────────────────────────────────
    @PutMapping("/{id}/assign")
    public ResponseEntity<RequetsAtx> assign(
            @PathVariable String id,
            // @RequestParam récupère le paramètre
            // dans l'URL après le "?"
            // ex: ?userId=64a1b2c4
            @RequestParam String userId) {
        return ResponseEntity.ok(
                requetsAtxService.assign(id, userId));
    }

    // ─────────────────────────────────────────────────
    // PUT /api/requests/{id}/deliver
    // → Marque une demande comme livrée
    // → status passe de PROCESSING à DELIVERED
    // → deliveredAt = date du jour automatique
    // ─────────────────────────────────────────────────
    @PutMapping("/{id}/deliver")
    public ResponseEntity<RequetsAtx> deliver(
            @PathVariable String id) {
        return ResponseEntity.ok(
                requetsAtxService.deliver(id));
    }

    // ─────────────────────────────────────────────────
    // PUT /api/requests/{id}/close
    // → Clôture une demande
    // → status passe de DELIVERED à ENDED
    // → La demande est définitivement terminée
    // ─────────────────────────────────────────────────
    @PutMapping("/{id}/close")
    public ResponseEntity<RequetsAtx> close(
            @PathVariable String id) {
        return ResponseEntity.ok(
                requetsAtxService.close(id));
    }
    // DELETE /api/requests/{id} → supprimer une demande
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id) {
        requetsAtxService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // ─────────────────────────────────────────
// PUT /api/requests/{id}/fill-params?lang=fr
// → Remplit les paramètres d'une demande
// → Body contient les valeurs manuelles
// ─────────────────────────────────────────
    @PutMapping("/{id}/fill-params")
    public ResponseEntity<RequetsAtx> fillParams(
            @PathVariable String id,
            @RequestParam(defaultValue = "fr") String lang,
            @RequestBody Map<String, String> manualValues) {
        return ResponseEntity.ok(
                requetsAtxService.fillParams(
                        id, lang, manualValues));
    }
    // ─────────────────────────────────────────
// POST /api/requests/{id}/execute-param
// → Exécute la requête SQL pour UN param
// → Appelé quand BO clique "Exécuter"
//
// Postman envoie :
// {
//   "paramName": "Nom du client"
// }
// ─────────────────────────────────────────
    @PostMapping("/{id}/execute-param")
    public ResponseEntity<Map<String, String>>
    executeParam(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                requetsAtxService.executeParam(
                        id,
                        body.get("paramName")
                ));
    }

    // ─────────────────────────────────────────
// POST /api/requests/{id}/save-param
// → Sauvegarde une valeur manuelle
// → Appelé quand BO saisit manuellement
//
// Postman envoie :
// {
//   "paramName": "Date de référence",
//   "value": "18/03/2026"
// }
// ─────────────────────────────────────────
    @PostMapping("/{id}/save-param")
    public ResponseEntity<RequetsAtx> saveParam(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                requetsAtxService.saveParam(
                        id,
                        body.get("paramName"),
                        body.get("value")
                ));
    }



}
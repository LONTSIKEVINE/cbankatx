package com.cbank.atx.controller;

import com.cbank.atx.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    // ─────────────────────────────────────────
    // GET /api/stats/global
    // → Retourne toutes les statistiques
    //   globales du système
    // → Accessible par BO_ADMIN seulement
    // ─────────────────────────────────────────
    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>>
    getGlobalStats() {
        return ResponseEntity.ok(
                statsService.getGlobalStats());
    }

    // ─────────────────────────────────────────
    // GET /api/stats/requests
    // → Retourne les statistiques
    //   des demandes par statut
    // → Accessible par BO_ADMIN et BO_METIER
    // ─────────────────────────────────────────
    @GetMapping("/requests")
    public ResponseEntity<Map<String, Object>>
    getRequestStats() {
        return ResponseEntity.ok(
                statsService.getRequestStats());
    }

    // ─────────────────────────────────────────
    // GET /api/stats/agent/{userId}
    // → Retourne les statistiques
    //   d'un agent spécifique
    // → Accessible par BO_ADMIN et BO_METIER
    // ─────────────────────────────────────────
    @GetMapping("/agent/{userId}")
    public ResponseEntity<Map<String, Object>>
    getStatsByAgent(
            @PathVariable String userId) {
        return ResponseEntity.ok(
                statsService.getStatsByAgent(userId));
    }
}
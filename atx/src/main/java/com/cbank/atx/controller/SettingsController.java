package com.cbank.atx.controller;

import com.cbank.atx.domain.settings.Settings;
import com.cbank.atx.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    // Le service qui gère la configuration globale
    private final SettingsService settingsService;

    // ─────────────────────────────────────────────────
    // GET /api/settings
    // → Retourne la configuration globale
    // → Chargée depuis le cache en mémoire
    //   si déjà lue, sinon depuis MongoDB
    // ─────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<Settings> get() {
        return ResponseEntity.ok(settingsService.get());
    }

    // ─────────────────────────────────────────────────
    // POST /api/settings
    // → Crée la configuration initiale
    // → À appeler UNE SEULE FOIS au démarrage
    // → L'ID est toujours "settings" (singleton)
    // ─────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Settings> create(
            @RequestBody Settings settings) {
        return ResponseEntity.ok(
                settingsService.create(settings));
    }

    // ─────────────────────────────────────────────────
    // PUT /api/settings
    // → Modifie la configuration globale
    // → Met à jour le cache automatiquement
    // → Accessible uniquement par BO_ADMIN
    //   (sera sécurisé en Phase 2)
    // ─────────────────────────────────────────────────
    @PutMapping
    public ResponseEntity<Settings> update(
            @RequestBody Settings settings) {
        return ResponseEntity.ok(
                settingsService.update(settings));
    }
}
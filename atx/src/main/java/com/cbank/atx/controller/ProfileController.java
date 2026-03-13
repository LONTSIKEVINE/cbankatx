package com.cbank.atx.controller;

import com.cbank.atx.domain.user.Profile;
import com.cbank.atx.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// @RestController → dit à Spring que cette classe
// est un Controller REST qui retourne du JSON
// @RequestMapping → toutes les URLs de ce controller
// commencent par /api/profils
@RestController
@RequestMapping("/api/profils")
@RequiredArgsConstructor  // Lombok injecte ProfileService automatiquement
public class ProfileController {

    // Le service qui contient la logique métier des profils
    private final ProfileService profileService;

    // ─────────────────────────────────────────────────
    // GET /api/profils
    // → Retourne la liste des 2 profils fixes
    // → Appelé quand on veut afficher
    //   la liste déroulante "Choisir un profil"
    //   dans le formulaire de création d'utilisateur
    // ─────────────────────────────────────────────────
    @GetMapping  // répond aux requêtes HTTP GET
    public ResponseEntity<List<Profile>> getAll() {
        // ResponseEntity.ok() → retourne le code HTTP 200 (succès)
        // profileService.getAll() → appelle le service
        //   qui va chercher les profils dans MongoDB
        return ResponseEntity.ok(profileService.getAll());
    }

    // ─────────────────────────────────────────────────
    // GET /api/profils/{id}
    // → Retourne UN seul profil par son ID
    // → Exemple : GET /api/profils/64a1b2c3
    // ─────────────────────────────────────────────────
    @GetMapping("/{id}")  // {id} = variable dans l'URL
    public ResponseEntity<Profile> getById(
            // @PathVariable récupère la valeur de {id} dans l'URL
            // Exemple : /api/profils/64a1b2c3 → id = "64a1b2c3"
            @PathVariable String id) {
        return ResponseEntity.ok(profileService.getById(id));
    }
}
package com.cbank.atx.controller;

import com.cbank.atx.domain.branch.City;
import com.cbank.atx.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    // Le service qui contient la logique métier des villes
    private final CityService cityService;



    @PostMapping  // répond aux requêtes HTTP POST
    public ResponseEntity<City> create(
            // @RequestBody → lit le JSON envoyé par Postman
            // et le convertit automatiquement en objet City
            @RequestBody City city) {
        // Le service vérifie que le code est unique
        // avant de sauvegarder dans MongoDB
        return ResponseEntity.ok(cityService.create(city));
    }

    // ─────────────────────────────────────────────────
    // GET /api/cities
    // → Retourne la liste de toutes les villes
    // ─────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<City>> getAll() {
        return ResponseEntity.ok(cityService.getAll());
    }


    // GET /api/cities/{id}
    // → Retourne UNE seule ville par son ID
    // → Exemple : GET /api/cities/64a1b2c3

    @GetMapping("/{id}")
    public ResponseEntity<City> getById(
            @PathVariable String id) {
        return ResponseEntity.ok(cityService.getById(id));
    }

    // PUT /api/cities/{id}
    // → Modifie une ville existante
    // → Postman envoie :
    // → Exemple : PUT /api/cities/64a1b2c
    @PutMapping("/{id}")  // répond aux requêtes HTTP PUT
    public ResponseEntity<City> update(
            @PathVariable String id,  // ID de la ville à modifier
            @RequestBody City city) { // nouvelles données
        return ResponseEntity.ok(cityService.update(id, city));
    }


    // DELETE /api/cities/{id}
    // → Supprime une ville
    // → Exemple : DELETE /api/cities/64a1b2c3
    // → Retourne HTTP 204 (No Content) si succès

    @DeleteMapping("/{id}")  // répond aux requêtes HTTP DELETE
    public ResponseEntity<Void> delete(
            @PathVariable String id) {
        cityService.delete(id);
        // noContent() → retourne HTTP 204
        // pas de données à retourner après suppression
        return ResponseEntity.noContent().build();
    }
}
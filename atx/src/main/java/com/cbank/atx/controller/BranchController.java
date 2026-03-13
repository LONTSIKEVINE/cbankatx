package com.cbank.atx.controller;

import com.cbank.atx.domain.branch.Branch;
import com.cbank.atx.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/branchs")
@RequiredArgsConstructor
public class BranchController {

    // Le service qui contient la logique métier des agences
    private final BranchService branchService;

    // ─────────────────────────────────────────────────
    // POST /api/branchs
    // → Crée une nouvelle agence bancaire
    // → Postman envoie :
    //   {
    //     "label": "Agence Bastos",
    //     "code": "AG-001",
    //     "taxesAccount": "CM21-TAX-001",
    //     "productsAccount": "CM21-PRD-001",
    //     "cityId": "64a1b2c3"
    //   }
    // ─────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Branch> create(
            @RequestBody Branch branch) {
        // Le service vérifie que le code agence est unique
        return ResponseEntity.ok(branchService.create(branch));
    }

    // ─────────────────────────────────────────────────
    // GET /api/branchs
    // → Retourne la liste de toutes les agences
    // ─────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Branch>> getAll() {
        return ResponseEntity.ok(branchService.getAll());
    }

    // ─────────────────────────────────────────────────
    // GET /api/branchs/{id}
    // → Retourne UNE seule agence par son ID
    // ─────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Branch> getById(
            @PathVariable String id) {
        return ResponseEntity.ok(branchService.getById(id));
    }

    // ─────────────────────────────────────────────────
    // GET /api/branchs/city/{cityId}
    // → Retourne toutes les agences d'une ville
    // → Exemple : GET /api/branchs/city/64a1b2c3
    //   retourne toutes les agences de Yaoundé
    // ─────────────────────────────────────────────────
    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<Branch>> getByCity(
            // cityId = ID de la ville dans MongoDB
            @PathVariable String cityId) {
        return ResponseEntity.ok(
                branchService.getByCity(cityId));
    }

    // ─────────────────────────────────────────────────
    // PUT /api/branchs/{id}
    // → Modifie une agence existante
    // ─────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Branch> update(
            @PathVariable String id,
            @RequestBody Branch branch) {
        return ResponseEntity.ok(
                branchService.update(id, branch));
    }

    // ─────────────────────────────────────────────────
    // DELETE /api/branchs/{id}
    // → Supprime une agence
    // ─────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id) {
        branchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
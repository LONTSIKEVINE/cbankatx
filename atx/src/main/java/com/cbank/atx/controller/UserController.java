package com.cbank.atx.controller;

import com.cbank.atx.domain.user.User;
import com.cbank.atx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cbank.atx.exception.BusinessException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    // Le service qui contient la logique métier des utilisateurs
    private final UserService userService;

    // ─────────────────────────────────────────────────
    // POST /api/users
    // → Crée un nouvel utilisateur
    // → Postman envoie :
    //   {
    //     "firstname": "Jean",
    //     "lastname": "Dupont",
    //     "email": "jean@cbank.cm",
    //     "branchId": "64a1b2c3",
    //     "profilId": "64a1b2c4"
    //   }
    // → Le service vérifie :
    //   1. Email unique
    //   2. active = true automatiquement
    // ─────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<User> create(
            @RequestBody User user) {
        return ResponseEntity.ok(userService.create(user));
    }



    // ─────────────────────────────────────────
// PUT /api/users/{id}/change-password
// → Permet à l'utilisateur de changer
//   son mot de passe
// ─────────────────────────────────────────
    @PutMapping("/{id}/change-password")
    public ResponseEntity<Map<String, String>>
    changePassword(
            @PathVariable String id,
            @RequestBody
            Map<String, String> body) {

        String oldPassword =
                body.get("oldPassword");
        String newPassword =
                body.get("newPassword");

        // Vérifie que les 2 champs sont présents
        if (oldPassword == null
                || newPassword == null) {
            throw new BusinessException(
                    "oldPassword et newPassword "
                            + "sont requis !");
        }

        userService.changePassword(
                id, oldPassword, newPassword);

        Map<String, String> response =
                new HashMap<>();
        response.put("message",
                "Mot de passe modifié avec succès !");

        return ResponseEntity.ok(response);
    }








    // ─────────────────────────────────────────────────
    // GET /api/users
    // → Retourne la liste de tous les utilisateurs
    // ─────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    // ─────────────────────────────────────────────────
    // GET /api/users/{id}
    // → Retourne UN seul utilisateur par son ID
    // ─────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(
            @PathVariable String id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // ─────────────────────────────────────────────────
    // GET /api/users/branch/{branchId}
    // → Retourne tous les utilisateurs d'une agence
    // → Exemple : GET /api/users/branch/64a1b2c3
    //   retourne tous les agents de l'Agence Bastos
    // ─────────────────────────────────────────────────
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<User>> getByBranch(
            @PathVariable String branchId) {
        return ResponseEntity.ok(
                userService.getByBranch(branchId));
    }

    // ─────────────────────────────────────────────────
    // GET /api/users/active
    // → Retourne tous les utilisateurs actifs
    // → Utilisé pour choisir un agent
    //   lors de l'assignation d'une demande
    // ─────────────────────────────────────────────────
    @GetMapping("/active")
    public ResponseEntity<List<User>> getAllActive() {
        return ResponseEntity.ok(userService.getAllActive());
    }

    // ─────────────────────────────────────────────────
    // PUT /api/users/{id}
    // → Modifie un utilisateur existant
    // ─────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable String id,
            @RequestBody User user) {
        return ResponseEntity.ok(userService.update(id, user));
    }

    // ─────────────────────────────────────────────────
    // PUT /api/users/{id}/activate
    // → Active le compte d'un utilisateur
    // → Exemple : PUT /api/users/64a1b2c3/activate
    // ─────────────────────────────────────────────────
    @PutMapping("/{id}/activate")
    public ResponseEntity<User> activate(
            @PathVariable String id) {
        return ResponseEntity.ok(userService.activate(id));
    }

    // ─────────────────────────────────────────────────
    // PUT /api/users/{id}/deactivate
    // → Désactive le compte d'un utilisateur
    // → Le service vérifie qu'un backup est configuré
    //   avant de désactiver
    // ─────────────────────────────────────────────────
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivate(
            @PathVariable String id) {
        return ResponseEntity.ok(userService.deactivate(id));
    }

    // DELETE /api/users/{id} → supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
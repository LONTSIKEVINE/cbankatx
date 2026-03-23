package com.cbank.atx.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice → intercepte
// toutes les exceptions de tous
// les Controllers automatiquement
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─────────────────────────────────────────
    // Gère RuntimeException
    // → Email déjà utilisé
    // → User non trouvé
    // → Demande non trouvée
    // → etc.
    // ─────────────────────────────────────────
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>>
    handleRuntimeException(
            RuntimeException ex) {

        Map<String, Object> error =
                new HashMap<>();
        error.put("status", 400);
        error.put("message", ex.getMessage());
        error.put("timestamp",
                LocalDateTime.now().toString());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // ─────────────────────────────────────────
    // Gère les ressources non trouvées
    // ─────────────────────────────────────────
    @ExceptionHandler(
            org.springframework.web.servlet
                    .resource.NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>>
    handleNotFoundException(Exception ex) {

        Map<String, Object> error =
                new HashMap<>();
        error.put("status", 404);
        error.put("message",
                "Ressource non trouvée !");
        error.put("timestamp",
                LocalDateTime.now().toString());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    // ─────────────────────────────────────────
    // Gère toutes les autres exceptions
    // ─────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    handleException(Exception ex) {

        Map<String, Object> error =
                new HashMap<>();
        error.put("status", 500);
        error.put("message",
                "Erreur interne du serveur !");
        error.put("timestamp",
                LocalDateTime.now().toString());

        // Log l'erreur dans la console
        System.out.println(
                "❌ Erreur : " + ex.getMessage());

        return ResponseEntity
                .status(HttpStatus
                        .INTERNAL_SERVER_ERROR)
                .body(error);
    }
}

package com.cbank.atx.service;

import com.cbank.atx.domain.user.User;
import com.cbank.atx.exception.BusinessException;
import com.cbank.atx.exception.ResourceNotFoundException;
import com.cbank.atx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService
            notificationService;

    // ─────────────────────────────────────────
    // CRÉER un utilisateur
    // ─────────────────────────────────────────
    public User create(User user) {

        // Règle 1 : email doit être unique
        if (userRepository.findByEmail(
                user.getEmail()).isPresent()) {
            throw new BusinessException(
                    "Email déjà utilisé : "
                            + user.getEmail());
        }

        // Règle 2 : hasher le mot de passe
        if (user.getPassword() != null) {
            user.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()));
        }

        // Règle 3 : actif par défaut
        user.setActive(true);

        return userRepository.save(user);
    }

    // ─────────────────────────────────────────
    // LIRE tous les utilisateurs
    // ─────────────────────────────────────────
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // ─────────────────────────────────────────
    // LIRE un utilisateur par ID
    // ─────────────────────────────────────────
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Utilisateur non trouvé : "
                                        + id));
    }

    // ─────────────────────────────────────────
    // LIRE les utilisateurs d'une agence
    // ─────────────────────────────────────────
    public List<User> getByBranch(
            String branchId) {
        return userRepository
                .findByBranchId(branchId);
    }

    // ─────────────────────────────────────────
    // LIRE tous les utilisateurs actifs
    // ─────────────────────────────────────────
    public List<User> getAllActive() {
        return userRepository.findByActiveTrue();
    }

    // ─────────────────────────────────────────
    // MODIFIER un utilisateur
    // ─────────────────────────────────────────
    public User update(String id, User newData) {
        User existing = getById(id);
        existing.setFirstname(
                newData.getFirstname());
        existing.setLastname(
                newData.getLastname());
        existing.setEmail(newData.getEmail());
        existing.setBranchId(
                newData.getBranchId());
        existing.setProfilId(
                newData.getProfilId());
        existing.setBackupId(
                newData.getBackupId());
        return userRepository.save(existing);
    }

    // ─────────────────────────────────────────
    // ACTIVER un compte
    // ─────────────────────────────────────────
    public User activate(String id) {
        User user = getById(id);
        user.setActive(true);
        return userRepository.save(user);
    }


    // ─────────────────────────────────────────
    // DÉSACTIVER un compte
    // ─────────────────────────────────────────
    // ─────────────────────────────────────────
// DÉSACTIVER un compte
// ─────────────────────────────────────────
    public User deactivate(String id) {
        User user = getById(id);

        // Vérifie que le backup est configuré
        if (user.getBackupId() == null) {
            throw new BusinessException(
                    "Impossible de désactiver : "
                            + "backup non configuré !");
        }

        // Désactive le compte
        user.setActive(false);

        // Sauvegarde dans MongoDB
        User saved = userRepository.save(user);

        // ✅ Notifie l'utilisateur désactivé
        notificationService
                .notifyAccountDeactivated(
                        user.getEmail()
                );

        return saved;
    }

    // ─────────────────────────────────────────
    // SUPPRIMER un utilisateur
    // ─────────────────────────────────────────
    public void delete(String id) {
        getById(id);
        userRepository.deleteById(id);
    }
}
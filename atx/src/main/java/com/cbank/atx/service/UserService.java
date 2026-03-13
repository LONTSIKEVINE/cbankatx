package com.cbank.atx.service;

import com.cbank.atx.domain.user.User;
import com.cbank.atx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Créer un utilisateur
    public User create(User user) {
        // Règle 1 : email doit être unique
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException(
                    "Un utilisateur avec l'email "
                            + user.getEmail()
                            + " existe déjà !"
            );
        }
        // Règle 2 : nouvel utilisateur toujours actif
        user.setActive(true);
        return userRepository.save(user);
    }

    // Lister tous les utilisateurs
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // Trouver un utilisateur par ID
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur non trouvé : " + id));
    }

    // Lister les utilisateurs d'une agence
    public List<User> getByBranch(String branchId) {
        return userRepository.findByBranchId(branchId);
    }

    // Lister tous les utilisateurs actifs
    public List<User> getAllActive() {
        return userRepository.findByActiveTrue();
    }

    // Modifier un utilisateur
    public User update(String id, User newData) {
        User existing = getById(id);
        existing.setFirstname(newData.getFirstname());
        existing.setLastname(newData.getLastname());
        existing.setEmail(newData.getEmail());
        existing.setBranchId(newData.getBranchId());
        existing.setProfilId(newData.getProfilId());
        existing.setBackupId(newData.getBackupId());
        return userRepository.save(existing);
    }

    // Activer un compte
    public User activate(String id) {
        User user = getById(id);
        user.setActive(true);
        return userRepository.save(user);
    }

    // Désactiver un compte
    public User deactivate(String id) {
        User user = getById(id);
        // Règle : on ne peut pas désactiver
        // si pas de backup configuré
        if (user.getBackupId() == null) {
            throw new RuntimeException(
                    "Impossible de désactiver : "
                            + "aucun utilisateur backup configuré !"
            );
        }
        user.setActive(false);
        return userRepository.save(user);
    }
}
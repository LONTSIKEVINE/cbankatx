package com.cbank.atx.service;

import com.cbank.atx.domain.settings.Settings;
import com.cbank.atx.exception.ResourceNotFoundException;
import com.cbank.atx.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    // Repository qui parle à MongoDB
    // collection "settings"
    // Contient UN SEUL document
    // avec l'ID fixe "settings"
    private final SettingsRepository
            settingsRepository;

    // Cache en mémoire
    // → Évite d'aller chercher dans MongoDB
    //   à chaque requête
    // → Mis à jour après chaque modification
    private Settings cache = null;

    // ─────────────────────────────────────────
    // LIRE la configuration
    // → Utilise le cache si disponible
    // → Sinon charge depuis MongoDB
    // ─────────────────────────────────────────
    public Settings get() {

        // Si cache vide → charger depuis MongoDB
        if (cache == null) {
            cache = settingsRepository
                    .findById("settings")
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Settings non configuré !"
                                            + " Créez la configuration"
                                            + " via POST /api/settings"));
        }

        // Retourne le cache directement
        // sans toucher à MongoDB ✅
        return cache;
    }

    // ─────────────────────────────────────────
    // CRÉER la configuration initiale
    // → ID toujours "settings" (singleton)
    // → Met à jour le cache
    // ─────────────────────────────────────────
    public Settings create(Settings settings) {

        // Force l'ID à "settings"
        settings.setId("settings");

        // Sauvegarde dans MongoDB
        cache = settingsRepository.save(settings);

        return cache;
    }

    // ─────────────────────────────────────────
    // MODIFIER la configuration
    // → Met à jour le cache automatiquement
    // ─────────────────────────────────────────
    public Settings update(Settings newSettings) {

        // Force l'ID à "settings"
        newSettings.setId("settings");

        // Sauvegarde et rafraîchit le cache
        cache = settingsRepository
                .save(newSettings);

        return cache;
    }

    // ─────────────────────────────────────────
    // RACCOURCIS utiles pour les autres Services
    // ─────────────────────────────────────────

    // Le 2FA est-il activé ?
    // → Utilisé par AuthService lors du login
    public boolean is2FAEnabled() {
        return get().getSecurity().is2FA();
    }

    // Un BO peut-il assigner inter-agences ?
    // → Utilisé par RequetsAtxService
    public boolean canAssignOtherBranch() {
        return get().getBranchAssignments()
                .getBoCanAssignBoOtherBran();
    }

    // Assigner au backup si absent ?
    // → Utilisé par RequetsAtxService
    public boolean assignToBackupWhenAbsent() {
        return get().getBranchAssignments()
                .getAssignBackupWhnStarterAbsent();
    }
}
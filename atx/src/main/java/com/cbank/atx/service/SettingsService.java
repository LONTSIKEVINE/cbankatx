package com.cbank.atx.service;

import com.cbank.atx.domain.settings.Settings;
import com.cbank.atx.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {
    private final SettingsRepository settingsRepository;
    private Settings cache = null;

    public Settings get() {
        if (cache == null) {
            cache = settingsRepository.findById("settings")
                    .orElseThrow(() ->
                            new RuntimeException("Settings non configuré !"));
        }
        return cache;
    }

    public Settings create(Settings settings) {
        settings.setId("settings");
        cache = settingsRepository.save(settings);
        return cache;
    }

    public Settings update(Settings newSettings) {
        newSettings.setId("settings");
        cache = settingsRepository.save(newSettings);
        return cache;
    }

    public boolean is2FAEnabled() {
        return get().getSecurity().is2FA();
    }

    public boolean canAssignOtherBranch() {
        return get().getBranchAssignments()
                .getBoCanAssignBoOtherBran();
    }

    public boolean assignToBackupWhenAbsent() {
        return get().getBranchAssignments()
                .getAssignBackupWhnStarterAbsent();
    }
}
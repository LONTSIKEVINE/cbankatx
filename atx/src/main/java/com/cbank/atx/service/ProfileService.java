
package com.cbank.atx.service;

import com.cbank.atx.domain.user.Profile;
import com.cbank.atx.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service                    // ← dit à Spring que c'est un Service
@RequiredArgsConstructor    // ← Lombok injecte le repository automatiquement
public class ProfileService {

    private final ProfileRepository profileRepository;

    // Lister les 2 profils — lecture seule
    public List<Profile> getAll() {
        return profileRepository.findAll();
    }

    // Trouver un profil par son ID
    public Profile getById(String id) {
        return profileRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Profil non trouvé : " + id));
    }
}

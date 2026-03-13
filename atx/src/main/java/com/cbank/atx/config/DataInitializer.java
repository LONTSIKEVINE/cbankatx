package com.cbank.atx.config;

import com.cbank.atx.domain.user.Profile;
import com.cbank.atx.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final ProfileRepository profileRepository;

    @Override
    public void run(String... args) throws Exception {
        if (profileRepository.count() == 0) {
            System.out.println("⚙️ Initialisation des profils...");
            Profile boAdmin = new Profile();
            boAdmin.setLabel("BackOffice Administrateur");
            boAdmin.setCode("BO_ADMIN");
            profileRepository.save(boAdmin);

            Profile boMetier = new Profile();
            boMetier.setLabel("BackOffice Métier");
            boMetier.setCode("BO_METIER");
            profileRepository.save(boMetier);

            System.out.println("✅ Profils créés avec succès !");
        } else {
            System.out.println(
                    "✅ Profils déjà initialisés — rien à faire.");
        }
    }
}

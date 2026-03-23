package com.cbank.atx.service;

import com.cbank.atx.domain.user.Profile;
import com.cbank.atx.domain.user.User;
import com.cbank.atx.dto.LoginRequest;
import com.cbank.atx.dto.LoginResponse;
import com.cbank.atx.dto.TwoFactorRequest;
import com.cbank.atx.exception.BusinessException;
import com.cbank.atx.exception.ResourceNotFoundException;
import com.cbank.atx.repository.ProfileRepository;
import com.cbank.atx.repository.UserRepository;
import com.cbank.atx.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository
            profileRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final SettingsService settingsService;

    // Cache temporaire des codes 2FA
    // → email → code à 6 chiffres
    // → Supprimé après vérification
    private final Map<String, String> twoFACodes
            = new HashMap<>();

    // ─────────────────────────────────────────
    // CONNEXION — Étape 1
    // ─────────────────────────────────────────
    public LoginResponse login(
            LoginRequest request) {

        // Étape 1 : cherche le user par email
        // → Si non trouvé → 404
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email ou mot de passe "
                                        + "incorrect !"));

        // Étape 2 : compte actif ?
        // → Si désactivé → 400
        if (!user.getActive()) {
            throw new BusinessException(
                    "Compte désactivé !");
        }

        // Étape 3 : password configuré ?
        // → Si null → 400
        if (user.getPassword() == null) {
            throw new BusinessException(
                    "Mot de passe non configuré !");
        }

        // Étape 4 : vérifie le password
        // → BCrypt compare le password saisi
        //   avec le hash stocké dans MongoDB
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new BusinessException(
                    "Email ou mot de passe "
                            + "incorrect !");
        }

        // Étape 5 : charge le profil
        // → BO_ADMIN ou BO_METIER
        Profile profil = profileRepository
                .findById(user.getProfilId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Profil non trouvé !"));

        // Étape 6 : 2FA activé ?
        boolean require2FA = false;
        try {
            require2FA =
                    settingsService.is2FAEnabled();
        } catch (Exception e) {
            // Settings pas configuré
            // → 2FA désactivé par défaut
            require2FA = false;
        }

        if (require2FA) {
            // Génère un code à 6 chiffres
            String code = generateCode();

            // Sauvegarde temporairement
            twoFACodes.put(
                    user.getEmail(), code);

            // Affiche dans la console en DEV
            // En PROD → envoyer par email
            System.out.println(
                    "🔐 Code 2FA pour "
                            + user.getEmail()
                            + " : " + code);

            // Retourne sans token
            // → le frontend doit vérifier le 2FA
            return new LoginResponse(
                    null,
                    user.getEmail(),
                    profil.getCode(),
                    true  // 2FA requis !
            );
        }

        // Étape 7 : génère le JWT Token
        // → Contient email + profil
        // → Valide 24h
        String token = jwtUtil.generateToken(
                user.getEmail(),
                profil.getCode()
        );

        return new LoginResponse(
                token,
                user.getEmail(),
                profil.getCode(),
                false  // pas de 2FA
        );
    }

    // ─────────────────────────────────────────
    // VÉRIFICATION 2FA — Étape 2
    // ─────────────────────────────────────────
    public LoginResponse verify2FA(
            TwoFactorRequest request) {

        // Récupère le code sauvegardé
        String savedCode = twoFACodes
                .get(request.getEmail());

        // Vérifie que le code est correct
        if (savedCode == null
                || !savedCode.equals(
                request.getCode())) {
            throw new BusinessException(
                    "Code 2FA incorrect !");
        }

        // Supprime le code utilisé
        // → ne peut pas être réutilisé
        twoFACodes.remove(request.getEmail());

        // Charge user et profil
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Utilisateur non trouvé !"));

        Profile profil = profileRepository
                .findById(user.getProfilId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Profil non trouvé !"));

        // Génère le JWT Token
        String token = jwtUtil.generateToken(
                user.getEmail(),
                profil.getCode()
        );

        return new LoginResponse(
                token,
                user.getEmail(),
                profil.getCode(),
                false
        );
    }

    // ─────────────────────────────────────────
    // Générer un code 2FA à 6 chiffres
    // Ex: 123456, 789012, 456789
    // ─────────────────────────────────────────
    private String generateCode() {
        Random random = new Random();
        // Génère un nombre entre 100000 et 999999
        int code = 100000
                + random.nextInt(900000);
        return String.valueOf(code);
    }
}

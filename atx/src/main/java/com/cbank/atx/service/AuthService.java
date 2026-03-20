package com.cbank.atx.service;

import com.cbank.atx.domain.user.Profile;
import com.cbank.atx.domain.user.User;
import com.cbank.atx.dto.LoginRequest;
import com.cbank.atx.dto.LoginResponse;
import com.cbank.atx.dto.TwoFactorRequest;
import com.cbank.atx.repository.ProfileRepository;
import com.cbank.atx.repository.UserRepository;
import com.cbank.atx.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password
        .PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final SettingsService settingsService;

    // ⚠️ LDAP retiré temporairement
    // car serveur LDAP non disponible
    // private final LdapService ldapService;

    // Cache temporaire des codes 2FA
    private final Map<String, String> twoFACodes
            = new HashMap<>();

    // ─────────────────────────────────────────
    // CONNEXION
    // ─────────────────────────────────────────
    public LoginResponse login(
            LoginRequest request) {

        // Étape 1 : chercher le user dans MongoDB
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Email ou mot de passe "
                                        + "incorrect !"));

        // Étape 2 : vérifier le compte actif
        if (!user.getActive()) {
            throw new RuntimeException(
                    "Compte désactivé !");
        }

        // Étape 3 : vérifier le password
        // → authentification locale MongoDB
        // → LDAP sera activé plus tard
        System.out.println(
                "🔐 Authentification locale "
                        + "pour : " + request.getEmail());

        if (user.getPassword() == null) {
            throw new RuntimeException(
                    "Mot de passe non configuré !");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new RuntimeException(
                    "Email ou mot de passe "
                            + "incorrect !");
        }

        // Étape 4 : récupérer le profil
        Profile profil = profileRepository
                .findById(user.getProfilId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Profil non trouvé !"));

        // Étape 5 : vérifier si 2FA activé
        boolean require2FA = false;
        try {
            require2FA =
                    settingsService.is2FAEnabled();
        } catch (Exception e) {
            require2FA = false;
        }

        if (require2FA) {
            String code = generateCode();
            twoFACodes.put(
                    user.getEmail(), code);
            System.out.println(
                    "🔐 Code 2FA pour "
                            + user.getEmail()
                            + " : " + code);

            return new LoginResponse(
                    null,
                    user.getEmail(),
                    profil.getCode(),
                    true
            );
        }

        // Étape 6 : générer le JWT Token
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
    // VÉRIFICATION 2FA
    // ─────────────────────────────────────────
    public LoginResponse verify2FA(
            TwoFactorRequest request) {

        String savedCode = twoFACodes
                .get(request.getEmail());

        if (savedCode == null
                || !savedCode.equals(
                request.getCode())) {
            throw new RuntimeException(
                    "Code 2FA incorrect !");
        }

        twoFACodes.remove(request.getEmail());

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur non trouvé !"));

        Profile profil = profileRepository
                .findById(user.getProfilId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Profil non trouvé !"));

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
    // ─────────────────────────────────────────
    private String generateCode() {
        Random random = new Random();
        int code = 100000
                + random.nextInt(900000);
        return String.valueOf(code);
    }
}
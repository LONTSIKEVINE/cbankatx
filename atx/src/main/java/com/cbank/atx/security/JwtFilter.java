package com.cbank.atx.security;

import com.cbank.atx.domain.user.User;
import com.cbank.atx.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority
        .SimpleGrantedAuthority;
import org.springframework.security.core.context
        .SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter
        .OncePerRequestFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // ✅ Utilise getServletPath()
        // au lieu de getRequestURI()
        String path = request.getServletPath();

        System.out.println(
                "🔍 Request path : " + path);

        // ✅ Laisser passer /api/auth
        // sans vérifier le token
        if (path.startsWith("/api/auth")) {
            System.out.println(
                    "✅ Path public : " + path);
            filterChain.doFilter(
                    request, response);
            return;
        }

        // Récupérer le header Authorization
        String authHeader = request
                .getHeader("Authorization");

        // Pas de token → 401
        if (authHeader == null
                || !authHeader
                .startsWith("Bearer ")) {
            sendError(response, 401,
                    "Token manquant ou invalide");
            return;
        }

        try {
            // Extraire le token
            String token =
                    authHeader.substring(7);

            // Token invalide → 401
            if (!jwtUtil.isTokenValid(token)) {
                sendError(response, 401,
                        "Token invalide ou expiré");
                return;
            }

            // Extraire email et profil
            String email =
                    jwtUtil.extractEmail(token);
            String profil =
                    jwtUtil.extractProfil(token);

            System.out.println(
                    "🔐 Email : " + email);
            System.out.println(
                    "🔐 Profil : " + profil);

            // Charger l'utilisateur
            User user = userRepository
                    .findByEmail(email)
                    .orElse(null);

            // User inactif → 401
            if (user == null
                    || !user.getActive()) {
                sendError(response, 401,
                        "Utilisateur inactif "
                                + "ou introuvable");
                return;
            }

            // Créer l'authentification
            UsernamePasswordAuthenticationToken
                    auth =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + profil))
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(auth);

            filterChain.doFilter(
                    request, response);

        } catch (Exception e) {
            System.out.println(
                    "❌ Erreur JWT : "
                            + e.getMessage());
            sendError(response, 401,
                    "Erreur token : "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // Envoyer une erreur JSON
    // ─────────────────────────────────────────
    private void sendError(
            HttpServletResponse response,
            int status,
            String message)
            throws IOException {

        response.setStatus(status);
        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> error =
                new HashMap<>();
        error.put("status", status);
        error.put("message", message);

        new ObjectMapper()
                .writeValue(
                        response.getWriter(),
                        error);
    }
}
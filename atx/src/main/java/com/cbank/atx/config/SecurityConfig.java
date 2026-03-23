package com.cbank.atx.config;

import com.cbank.atx.security.JwtFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // Bean pour encoder les mots de passe
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Temporaire pour tester
        // → accepte le password en clair

            return new BCryptPasswordEncoder();


    }

    // Bean pour exposer AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    // Configuration principale de Spring Security
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // ✅ Endpoints publics
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // 🔐 BO_ADMIN seulement
                        // → gestion du système
                        .requestMatchers(
                                "/api/users/**",
                                "/api/branchs/**",
                                "/api/cities/**",
                                "/api/settings/**",
                                "/api/profils/**",
                                "/api/stats/global"
                        ).hasAuthority("ROLE_BO_ADMIN")

                        // 🔐 BO_ADMIN et BO_METIER
                        // → traitement des demandes
                        .requestMatchers(
                                "/api/requests/**",
                                "/api/atxs/**",
                                "/api/documents/**",
                                "/api/files/**",
                                "/api/stats/requests",  // ← ajoute
                                "/api/stats/agent/**"   // ← ajoute
                        ).hasAnyAuthority(
                                "ROLE_BO_ADMIN",
                                "ROLE_BO_METIER")

                        // Tout le reste → authentifié
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter
                                .class
                );

        return http.build();
    }
}
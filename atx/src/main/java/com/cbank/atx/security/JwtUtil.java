
package com.cbank.atx.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Générer un token JWT
    public String generateToken(
            String email,
            String profilCode) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("profil", profilCode);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis()
                                + expiration))
                .signWith(getKey(),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraire l'email depuis un token
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Extraire le profil depuis un token
    public String extractProfil(String token) {
        return (String) getClaims(token).get("profil");
    }

    // Vérifier si le token est valide
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Vérifier si le token est expiré
    public boolean isTokenExpired(String token) {
        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes());
    }
}
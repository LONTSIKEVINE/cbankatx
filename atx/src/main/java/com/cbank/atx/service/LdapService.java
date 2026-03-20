package com.cbank.atx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support
        .BaseLdapPathContextSource;
import org.springframework.ldap.filter
        .AndFilter;
import org.springframework.ldap.filter
        .EqualsFilter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LdapService {

    private final LdapTemplate ldapTemplate;

    // ─────────────────────────────────────────
    // VÉRIFIER les credentials LDAP
    // → Appelé lors du login
    // → Retourne true si l'agent existe
    //   et le password est correct
    // ─────────────────────────────────────────
    public boolean authenticate(
            String email,
            String password) {

        try {
            // Construire le filtre LDAP :
            // (&(objectClass=person)(mail=email))
            AndFilter filter = new AndFilter();
            filter.and(new EqualsFilter(
                    "objectClass", "person"));
            filter.and(new EqualsFilter(
                    "mail", email));

            // Vérifier dans LDAP
            return ldapTemplate.authenticate(
                    "",
                    filter.toString(),
                    password
            );

        } catch (Exception e) {
            System.out.println(
                    " LDAP non disponible : "
                            + e.getMessage());

            // En développement →
            // si LDAP non disponible
            // → authentification locale
            return false;
        }
    }

    // ─────────────────────────────────────────
    // VÉRIFIER si LDAP est disponible
    // ─────────────────────────────────────────
    public boolean isAvailable() {
        try {
            ldapTemplate.list("");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
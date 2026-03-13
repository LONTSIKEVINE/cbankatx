
package com.cbank.atx.domain.user;

import lombok.Data;
import java.util.Date;

@Data
// ⚠️ Pas de @Document — Session est imbriquée dans User
// Elle n'a PAS sa propre collection MongoDB
public class Session {

    private String ip;             // adresse IP de connexion
    private String device;         // navigateur/appareil utilisé
    private String token;          // JWT token d'accès (expire vite)
    private String refreshToken;   // token pour renouveler le JWT
    private Date expiresAt;        // date d'expiration du token
}

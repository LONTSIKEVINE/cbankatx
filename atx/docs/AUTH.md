# 🔐 Authentication API

## Base URL
http://localhost:8080/api/auth

## ⚠️ Ces endpoints ne nécessitent PAS de token

---

### 1. POST /api/auth/login
**Description** : Connecte un utilisateur
et retourne un token JWT valide 24h.

**Body** :
```json
{
  "email": "admin@cbank.cm",
  "password": "admin123"
}
```

**Résultat 200 OK** :
```json
{
  "token": "eyJhbGci...",
  "email": "admin@cbank.cm",
  "profil": "BO_ADMIN",
  "require2FA": false
}
```

**Erreurs** :
```json
{ "status": 400, "message": "Email ou mot de passe incorrect !" }
{ "status": 400, "message": "Compte désactivé !" }
```

---

### 2. POST /api/auth/verify-2fa
**Description** : Vérifie le code 2FA
(seulement si require2FA = true).

**Body** :
```json
{
  "email": "admin@cbank.cm",
  "code": "123456"
}
```

**Résultat 200 OK** :
```json
{
  "token": "eyJhbGci...",
  "email": "admin@cbank.cm",
  "profil": "BO_ADMIN",
  "require2FA": false
}
```

**Erreurs** :
```json
{ "status": 400, "message": "Code 2FA incorrect !" }
```
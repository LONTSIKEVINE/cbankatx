# 👥 Profiles API

## Base URL
http://localhost:8080/api/profils

## Authorization
🔐 BO_ADMIN seulement

## ⚠️ Lecture seule — 2 profils fixes

| ID | Code | Description |
|----|------|-------------|
| 69b3be73cf3e9afa9efaf9ba | BO_ADMIN | Administrateur |
| 69b3be73cf3e9afa9efaf9bb | BO_METIER | Agent métier |

---

### 1. GET /api/profils
**Description** : Retourne les 2 profils.

**Résultat 200 OK** :
```json
[
  { "id": "69b3be73cf3e9afa9efaf9ba", "code": "BO_ADMIN" },
  { "id": "69b3be73cf3e9afa9efaf9bb", "code": "BO_METIER" }
]
```

---

### 2. GET /api/profils/{id}
**Description** : Retourne un profil par ID.
# 🌍 Cities API

## Base URL
http://localhost:8080/api/cities

## Authorization
🔐 BO_ADMIN seulement

---

### 1. POST /api/cities
**Body** :
```json
{
  "label": "Yaoundé",
  "code": "YDE"
}
```

---

### 2. GET /api/cities
**Description** : Retourne toutes les villes.

---

### 3. GET /api/cities/{id}
**Description** : Retourne une ville par ID.

---

### 4. PUT /api/cities/{id}
**Description** : Modifie une ville.

---

### 5. DELETE /api/cities/{id}
**Description** : Supprime une ville.
**Résultat** : 204 No Content
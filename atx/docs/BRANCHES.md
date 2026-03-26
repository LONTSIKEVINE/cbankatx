# 🏦 Branches API

## Base URL
http://localhost:8080/api/branchs

## Authorization
🔐 BO_ADMIN seulement

---

### 1. POST /api/branchs
**Body** :
```json
{
  "label": "Agence Bastos",
  "code": "AG-001",
  "cityId": "ID_VILLE",
  "taxesAccount": "TAXES-001",
  "productsAccount": "PROD-001",
  "managerId": "ID_MANAGER"
}
```

---

### 2. GET /api/branchs
**Description** : Retourne toutes les agences.

---

### 3. GET /api/branchs/{id}
**Description** : Retourne une agence par ID.

---

### 4. GET /api/branchs/city/{cityId}
**Description** : Retourne les agences d'une ville.

---

### 5. PUT /api/branchs/{id}
**Description** : Modifie une agence.

---

### 6. DELETE /api/branchs/{id}
**Description** : Supprime une agence.
**Résultat** : 204 No Content
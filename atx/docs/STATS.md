# 📊 Statistics API

## Base URL
http://localhost:8080/api/stats

---

### 1. GET /api/stats/global
**Authorization** : 🔐 BO_ADMIN seulement
**Description** : Retourne toutes les stats.

**Résultat 200 OK** :
```json
{
  "totalUsers": 3,
  "activeUsers": 3,
  "totalBranches": 1,
  "totalAtxTypes": 3,
  "totalRequests": 5,
  "total": 5,
  "pending": 1,
  "processing": 2,
  "delivered": 1,
  "ended": 1
}
```

---

### 2. GET /api/stats/requests
**Authorization** : 🔐 BO_ADMIN et BO_METIER
**Description** : Stats des demandes par statut.

**Résultat 200 OK** :
```json
{
  "total": 5,
  "pending": 1,
  "processing": 2,
  "delivered": 1,
  "ended": 1
}
```

---

### 3. GET /api/stats/agent/{userId}
**Authorization** : 🔐 BO_ADMIN et BO_METIER
**Description** : Stats d'un agent.

**Résultat 200 OK** :
```json
{
  "userId": "69ba7d39abc42f598e4099d8",
  "total": 3,
  "processing": 1
}
```
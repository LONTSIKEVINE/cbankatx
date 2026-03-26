# 📋 Requests API

## Base URL
http://localhost:8080/api/requests

## Authorization
🔐 BO_ADMIN et BO_METIER

## Workflow des statuts
PENDING → PROCESSING → DELIVERED → ENDED

---

### 1. POST /api/requests
**Description** : Crée une demande.
Status initial = PENDING automatique.

**Body** :
```json
{
  "atxId": "69ba69edc1adb537a269487e",
  "customer": "CLI-001",
  "accountNumber": "CM21-10005-00012",
  "createdBy": "69ba7d39abc42f598e4099d8",
  "reason": "Demande de visa Schengen"
}
```

---

### 2. GET /api/requests
**Description** : Retourne toutes les demandes.

---

### 3. GET /api/requests/{id}
**Description** : Retourne une demande par ID.

---

### 4. GET /api/requests/status/{status}
**Description** : Filtre par statut.
**Valeurs** : PENDING, PROCESSING, DELIVERED, ENDED

---

### 5. GET /api/requests/agent/{userId}
**Description** : Demandes d'un agent.

---

### 6. PUT /api/requests/{id}/assign?userId={userId}
**Description** : Assigne à un agent.
PENDING → PROCESSING
⚠️ Agent doit être actif !
⚠️ Email envoyé automatiquement à l'agent !

---

### 7. PUT /api/requests/{id}/deliver
**Description** : Livre une demande.
PROCESSING → DELIVERED

---

### 8. PUT /api/requests/{id}/close
**Description** : Clôture une demande.
DELIVERED → ENDED

---

### 9. POST /api/requests/{id}/save-param
**Description** : Sauvegarde valeur manuelle.

**Body** :
```json
{
  "paramName": "Nom du client",
  "value": "Jean Dupont"
}
```

---

### 10. POST /api/requests/{id}/execute-param
**Description** : Exécute requête SQL via Node.js.

**Body** :
```json
{
  "paramName": "Solde du compte"
}
```

**Résultat 200 OK** :
```json
{
  "paramName": "Solde du compte",
  "value": "1 500 000"
}
```

---

### 11. PUT /api/requests/{id}/fill-params?lang=fr
**Description** : Remplit tous les params.

---

### 12. DELETE /api/requests/{id}
**Description** : Supprime une demande.
**Résultat** : 204 No Content
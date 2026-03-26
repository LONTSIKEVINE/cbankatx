# 📜 Attestations API

## Base URL
http://localhost:8080/api/atxs

## Authorization
🔐 BO_ADMIN et BO_METIER

---

### 1. POST /api/atxs
**Description** : Crée un type d'attestation.

**Body** :
```json
{
  "locales": {
    "fr": {
      "atxCode": "ATX-SOL-001",
      "label": "Attestation de Solde",
      "templateUrl": "solde-fr.docx",
      "price": 2500,
      "taxable": true,
      "taxePercentage": 19,
      "params": [
        {
          "name": "Nom du client",
          "dataSource": "MANUAL"
        },
        {
          "name": "Solde du compte",
          "dataSource": "DB",
          "dataSourceValue": "SELECT solde FROM comptes WHERE numero = @"
        }
      ],
      "deliverableInBranchs": [
        { "branchId": "69b3e864af0062d9b4f4f4db" }
      ]
    }
  }
}
```

---

### 2. GET /api/atxs
**Description** : Retourne toutes les attestations.

---

### 3. GET /api/atxs/{id}
**Description** : Retourne une attestation par ID.

---

### 4. GET /api/atxs/{id}/locale/{lang}
**Description** : Retourne la version localisée.
**Exemple** : GET /api/atxs/{id}/locale/fr

---

### 5. GET /api/atxs/{id}/price/{lang}
**Description** : Retourne le prix TTC.
**Formule** : TTC = HT + (HT × taxe / 100)

**Résultat 200 OK** :
```json
2975.0
```

---

### 6. GET /api/atxs/{id}/params/{lang}
**Description** : Retourne les params manuels.

---

### 7. PUT /api/atxs/{id}
**Description** : Modifie une attestation.

---

### 8. DELETE /api/atxs/{id}
**Description** : Supprime une attestation.
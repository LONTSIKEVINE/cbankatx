# ⚙️ Settings API

## Base URL
http://localhost:8080/api/settings

## Authorization
🔐 BO_ADMIN seulement

## ⚠️ Un seul document (singleton)

---

### 1. POST /api/settings
**Description** : Crée la configuration initiale.

**Body** :
```json
{
  "notifications": {
    "requestAssignment": {
      "backOffice": true,
      "customer": false
    },
    "requestEndProcessing": {
      "backOffice": true,
      "customer": true
    },
    "userAccountDesactivate": {
      "backOffice": true
    },
    "userInvitation": {
      "backOffice": true
    },
    "flatFileTransferFailed": {
      "backOffice": true
    }
  },
  "security": {
    "is2FA": false,
    "ldapSsoApiKey": "",
    "ldapSsoApiSecret": "",
    "cftApiKey": "",
    "cftApiSecret": ""
  },
  "bank": {
    "dbmsServer": "192.168.1.100",
    "dbmsName": "bank_db",
    "dbmsUsername": "bank_user",
    "dbmsPassword": "bank_pass"
  },
  "branchAssignments": {
    "boCanAssignBoOtherBran": false,
    "assignBackupWhnStarterAbsent": true
  }
}
```

---

### 2. GET /api/settings
**Description** : Retourne la configuration.

---

### 3. PUT /api/settings
**Description** : Modifie la configuration.
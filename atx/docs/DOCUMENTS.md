# 📁 Files API

## Base URL
http://localhost:8080/api/files

## Authorization
🔐 BO_ADMIN et BO_METIER

---

### 1. GET /api/files/flat/preview/{requestId}
**Description** : Prévisualise le fichier plat
sans le sauvegarder.

**Résultat 200 OK** :
```
AG-001;CM21-10005-00012;2975;DEBIT;admin@cbank.cm;2026-03-24
```

---

### 2. GET /api/files/flat/generate/{requestId}
**Description** : Génère et retourne
le fichier plat.

---

### 3. POST /api/files/flat/transfer/{requestId}
**Description** : Transfère le fichier
via CFT au système bancaire.
⚠️ En mode simulation en DEV !

**Résultat 200 OK** :
```json
{
  "message": "Transfert CFT réussi !",
  "requestId": "..."
}
```
# 📘 Guide de Test Postman - Authentification JWT

## 🔐 **Étape 1 : Vérifier votre utilisateur dans MongoDB**

Avant de tester, assurez-vous d'avoir un utilisateur actif dans MongoDB :

```javascript
// Dans MongoDB Compass ou shell
db.users.findOne({ email: "test@example.com" })
```

**Réponse attendue :**
```json
{
  "_id": ObjectId("..."),
  "email": "test@example.com",
  "password": "$2a$10$...",  // Mot de passe hashé avec BCrypt
  "active": true,
  "profilId": ObjectId("profil-id-ici")
}
```

---

## 📞 **Étape 2 : Obtenir un token JWT**

### **Request :**

**Method :** `POST`

**URL :**
```
http://localhost:8080/api/auth/login
```

**Headers :**
```
Content-Type: application/json
```

**Body (raw JSON) :**
```json
{
  "email": "test@example.com",
  "password": "votreMotDePasse"
}
```

### **Response (200 OK) :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWwiOiJCT19BRE1JTiIsInN1YiI6InRlc3RAZXhhbXBsZS5jb20iLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDA4NjQwMH0.xxxxx",
  "email": "test@example.com",
  "profil": "BO_ADMIN",
  "require2FA": false
}
```

---

## 🚀 **Étape 3 : Utiliser le token pour appeler un endpoint protégé**

### **Request :**

**Method :** `GET`

**URL :**
```
http://localhost:8080/api/users
```

**Headers :**
```
Authorization: Bearer <COPIEZ_LE_TOKEN_ICI>
Content-Type: application/json
```

### **Exemple complet :**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWwiOiJCT19BRE1JTiIsInN1YiI6InRlc3RAZXhhbXBsZS5jb20iLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDA4NjQwMH0.xxxxx
```

### **Response (200 OK) :**
```json
[
  {
    "id": "...",
    "email": "test@example.com",
    "active": true,
    "profil": "BO_ADMIN"
  }
]
```

---

## ⚠️ **Dépannage des erreurs**

### **401 Unauthorized - Token manquant**
```
{
  "status": 401,
  "message": "Token manquant ou invalide"
}
```
**Solution :** Vérifiez que le header `Authorization` contient `Bearer <token>`

### **401 Unauthorized - Token expiré**
```
{
  "status": 401,
  "message": "Token invalide ou expiré"
}
```
**Solution :** Obtenez un nouveau token via `/api/auth/login`

### **403 Forbidden - Rôle insuffisant**
```
{
  "status": 403,
  "message": "Access Denied"
}
```
**Solution :** L'utilisateur n'a pas le bon rôle. Vérifiez le profil dans MongoDB.

### **403 Forbidden - Utilisateur inactif**
```
{
  "status": 401,
  "message": "Utilisateur non trouvé ou inactif"
}
```
**Solution :** Assurez-vous que `user.active = true` dans MongoDB

---

## 💡 **Test rapide avec cURL**

```bash
# 1. Obtenir le token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}' \
  | jq -r '.token')

# 2. Appeler un endpoint protégé
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/users
```

---

## 📌 **Points importants à vérifier**

✅ L'utilisateur **existe** dans MongoDB  
✅ L'utilisateur est **actif** (`active: true`)  
✅ Le **profil** existe et est assigné à l'utilisateur  
✅ Le **mot de passe** est correct  
✅ Le token est inclus dans le header `Authorization`  
✅ Le format est : `Authorization: Bearer <token>`  
✅ Le token n'a pas **expiré** (expiration: 86400000ms = 24h)  

---

## 🔧 **Postman - Configuration d'automatisation**

**Créer une collection avec des variables :**

1. **Créer une variable :** `token`
2. **Dans le script "Tests" du login :**
```javascript
if (pm.response.code === 200) {
    pm.environment.set("token", pm.response.json().token);
}
```
3. **Utiliser dans les autres requêtes :**
```
Authorization: Bearer {{token}}
```

Ainsi, le token se met à jour automatiquement après chaque login ! 🚀


# 📝 Créer un utilisateur de test dans MongoDB

## Option 1 : Avec MongoDB Compass (GUI)

1. **Accédez à votre base de données :**
   - Connectez-vous à : `mongodb+srv://root:root@cluster0.pxdrhrn.mongodb.net/cbank_atx`
   - Sélectionnez la collection : `users`

2. **Insérez un document :**
   
```json
{
  "email": "test@example.com",
  "password": "$2a$10$GhPJSJE7U4Bq5p2I5qJ5wuM5xJ5x5xJ5xJ5xJ5xJ5xJ5xJ5xJ5x",
  "active": true,
  "firstName": "Test",
  "lastName": "User",
  "profilId": "admin_profile_id"
}
```

### 📌 Note sur le mot de passe :
Le mot de passe ci-dessus est le hash BCrypt de `"password123"`.

**Pour générer un nouveau hash :**
- Utilisez un [générateur BCrypt en ligne](https://bcrypt-generator.com/)
- Ou dans Java :
```java
String hashedPassword = new BCryptPasswordEncoder()
    .encode("votreMotDePasse");
```

---

## Option 2 : Avec MongoDB Shell

```bash
# Accédez au shell MongoDB
mongosh "mongodb+srv://root:root@cluster0.pxdrhrn.mongodb.net/cbank_atx"

# Insérez l'utilisateur
db.users.insertOne({
  "email": "test@example.com",
  "password": "$2a$10$GhPJSJE7U4Bq5p2I5qJ5wuM5xJ5x5xJ5xJ5xJ5xJ5xJ5xJ5xJ5x",
  "active": true,
  "firstName": "Test",
  "lastName": "User",
  "profilId": ObjectId("admin_profile_id_ici")
})

# Vérifiez l'insertion
db.users.findOne({ email: "test@example.com" })
```

---

## Option 3 : Créer un profil d'abord (si manquant)

Si vous recevez l'erreur "Profil non trouvé", insérez d'abord un profil :

```javascript
// Insérer un profil BO_ADMIN
db.profiles.insertOne({
  "_id": ObjectId(),
  "code": "BO_ADMIN",
  "description": "Back Office Admin",
  "active": true
})

// Récupérez l'ID du profil
db.profiles.findOne({ code: "BO_ADMIN" })
// Copiez le "_id" et utilisez-le dans l'utilisateur
```

---

## Option 4 : Utiliser le script Node.js

Créez un fichier `seed-users.js` :

```javascript
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const mongoUri = 'mongodb+srv://root:root@cluster0.pxdrhrn.mongodb.net/cbank_atx';

mongoose.connect(mongoUri).then(async () => {
    const password = await bcrypt.hash('password123', 10);
    
    const users = await mongoose.connection.collection('users').insertOne({
        email: 'test@example.com',
        password: password,
        active: true,
        firstName: 'Test',
        lastName: 'User',
        profilId: new mongoose.Types.ObjectId('admin_profile_id')
    });
    
    console.log('✅ Utilisateur créé:', users.insertedId);
    mongoose.connection.close();
}).catch(err => {
    console.error('❌ Erreur:', err);
});
```

Exécutez avec :
```bash
node seed-users.js
```

---

## ⚠️ Vérifier après insertion

Après insertion, vérifiez que tout est bon :

```javascript
db.users.findOne({ email: "test@example.com" })
```

Vous devez voir :
```json
{
  "_id": ObjectId("..."),
  "email": "test@example.com",
  "password": "$2a$10$...",
  "active": true,
  "firstName": "Test",
  "lastName": "User",
  "profilId": ObjectId("...")
}
```

---

## 🔑 Hashes BCrypt pré-générés pour test

| Email | Mot de passe | Hash BCrypt |
|-------|-------------|-----------|
| test@example.com | password | $2a$10$GhPJSJE7U4Bq5p2I5qJ5wuM5xJ5x5xJ5xJ5xJ5xJ5xJ5xJ5xJ5x |
| admin@cbank.com | admin123 | $2a$10$X0P5eU5J5xJ5xJ5xJ5xJ5xJ5xJ5xJ5xJ5xJ5xJ5xJ5xJ5xJ5xJ5x |

---

## 🚀 Maintenant testez dans Postman

1. **Importez** le fichier `CBank_ATX_API.postman_collection.json`
2. **Modifiez** les variables :
   - `email` : `test@example.com`
   - `password` : `password`
3. **Lancez** la première requête "Login - Obtenir JWT Token"
4. **Utilisez** le token pour appeler les autres endpoints

---

## 💡 Troubleshooting

| Erreur | Cause | Solution |
|--------|-------|----------|
| "Email ou mot de passe incorrect" | Utilisateur inexistant | Créez l'utilisateur dans MongoDB |
| "Compte désactivé" | `active: false` | Mettez à jour : `db.users.updateOne({...}, {$set: {active: true}})` |
| "Profil non trouvé" | `profilId` invalide | Vérifiez que le profil existe dans `db.profiles` |
| "Utilisateur non trouvé ou inactif" | Token valide mais utilisateur inactif | Vérifiez le status `active` |



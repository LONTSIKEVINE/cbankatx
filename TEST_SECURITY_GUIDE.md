# Guide : Débloquer les tests avec Spring Security & JWT

## 🎯 Problème identifié

Votre configuration de sécurité bloquait les tests pour ces raisons :

1. **JwtFilter** vérifie tous les endpoints
2. **SecurityConfig** nécessite une authentification : `anyRequest().authenticated()`
3. Les tests n'avaient pas de contexte de sécurité

---

## ✅ Solutions appliquées

### 1️⃣ **Ajout de `spring-security-test`** (pom.xml)
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 2️⃣ **Configuration du test** (AtxApplicationTests.java)
```java
@SpringBootTest
@AutoConfigureMockMvc  // ← Crée MockMvc automatiquement
class AtxApplicationTests {
    @Autowired
    private MockMvc mockMvc;  // ← Client HTTP pour les tests
    
    @Test
    @WithMockUser(username = "test@example.com", roles = "BO_ADMIN")
    void testAuthenticatedEndpoint() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }
}
```

---

## 📋 Bonnes pratiques pour les tests

### Tester différents rôles
```java
@Test
@WithMockUser(roles = "BO_ADMIN")
void testAdminAccess() { ... }

@Test
@WithMockUser(roles = "BO_METIER")
void testMetierAccess() { ... }

@Test
@WithAnonymousUser
void testPublicEndpoint() { ... }
```

### Tester sans authentification
```java
@Test
void testAuthenticationRequired() throws Exception {
    mockMvc.perform(get("/api/users"))
            .andExpect(status().isUnauthorized());
}
```

### Tester les endpoints publics
```java
@Test
void testPublicAuthEndpoint() throws Exception {
    mockMvc.perform(post("/api/auth/login"))
            .andExpect(status().isOk());
}
```

---

## 🔧 Configuration alternative : Profil de test

Vous pouvez aussi créer un profil de test qui désactive la sécurité :

**application-test.properties**
```properties
security.jwt.enabled=false
```

**SecurityConfig.java** (optionnel)
```java
@Configuration
@ConditionalOnProperty(name = "security.jwt.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig { ... }
```

---

## 🚀 Prochaines étapes

1. Exécutez : `mvn clean test`
2. Vérifiez que les tests passent
3. Créez des tests pour chaque endpoint

---

## 📚 Ressources

- [Spring Security Testing](https://spring.io/guides/topicals/spring-security-architecture/)
- [@WithMockUser Documentation](https://spring.io/guides/gs/testing-web/)


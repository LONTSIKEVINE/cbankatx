# CBank ATX — Documentation API Backend

## Stack technique
- Spring Boot 4.0.3 + Java 17
- MongoDB Atlas
- Node.js microservice (port 3008)
- JWT Authentication

## Base URL
http://localhost:8080

## Authentication
Tous les endpoints (sauf /api/auth/**)
nécessitent un token JWT dans le header :
Authorization: Bearer {token}

## Profils
| Profil | Accès |
|--------|-------|
| BO_ADMIN | Tous les endpoints |
| BO_METIER | Demandes, Attestations, Documents |

## Endpoints disponibles
| # | Fichier | Endpoints |
|---|---------|-----------|
| 1 | 01-AUTH.md | /api/auth/** |
| 2 | 02-USERS.md | /api/users/** |
| 3 | 03-BRANCHES.md | /api/branchs/** |
| 4 | 04-CITIES.md | /api/cities/** |
| 5 | 05-PROFILES.md | /api/profils/** |
| 6 | 06-ATX.md | /api/atxs/** |
| 7 | 07-REQUESTS.md | /api/requests/** |
| 8 | 08-SETTINGS.md | /api/settings/** |
| 9 | 09-DOCUMENTS.md | /api/documents/** |
| 10 | 10-FILES.md | /api/files/** |
| 11 | 11-STATS.md | /api/stats/** |

## IDs de test
- Agence Bastos : 69b3e864af0062d9b4f4f4db
- BO_ADMIN : 69b3be73cf3e9afa9efaf9ba
- BO_METIER : 69b3be73cf3e9afa9efaf9bb
- Jean Dupont : 69ba7d39abc42f598e4099d8
- ATX-SOL-002 : 69ba69edc1adb537a269487e
- Demande visa : 69ba87c966da046385e97c54
# Clinix Manager — Backend

API REST pour le système de gestion de clinique médicale. Projet PFE Master 2025-2026.

## Stack technique

| Technologie | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.3 |
| PostgreSQL | 15+ |
| JWT (jjwt) | 0.12.5 |
| Lombok | 1.18.30 |
| MapStruct | 1.5.5 |
| springdoc-openapi | (webmvc-ui) |

## Architecture

```
src/main/java/com/clinix/clinic/
├── controller/          # Endpoints REST
│   ├── AuthController       → /api/auth
│   ├── PatientController    → /api/patients
│   ├── MedecinController    → /api/medecins
│   ├── RendezVousController → /api/rendez-vous
│   ├── UserController       → /api/users
│   └── DashboardController  → /api/dashboard
├── service/             # Logique métier
├── repository/          # Accès données (Spring Data JPA)
├── model/               # Entités JPA (Clinic, Patient, Medecin, RendezVous, User)
├── dto/                 # Objets de transfert (request / response)
├── security/            # Filtre JWT, UserDetailsService
├── config/              # CORS, Security config, OpenAPI
├── exception/           # Gestionnaire d'erreurs global
└── component/           # Composants utilitaires
```

### Isolation multi-tenant

Chaque ressource est rattachée à une `clinic_id` extraite du token JWT. Les requêtes ne retournent que les données de la clinique du compte connecté.

### Rôles utilisateurs

| Rôle | Accès |
|---|---|
| `ADMIN` | Accès complet (patients, médecins, utilisateurs, dashboard) |
| `USER` | Patients et rendez-vous uniquement |
| `MEDECIN` | Dashboard et rendez-vous filtrés sur son propre profil |

## Prérequis

- **Java 17** (JDK)
- **Maven 3.6+**
- **PostgreSQL 15+** en cours d'exécution

## Configuration

Créer la base de données :

```sql
CREATE DATABASE clinix_db;
```

Paramètres dans `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clinix_db
spring.datasource.username=postgres
spring.datasource.password=
```

Les tables sont créées automatiquement par Hibernate (`ddl-auto=update`). Les migrations ponctuelles sont gérées via `src/main/resources/db/migration/`.

Un jeu de données initial est injecté au démarrage via `src/main/resources/data.sql`.

## Démarrage

```bash
mvn spring-boot:run
```

L'API écoute sur **http://localhost:8080**.

## Endpoints principaux

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Authentification — retourne un JWT |
| `POST` | `/api/auth/register` | Création de compte clinique |
| `GET` | `/api/patients` | Liste paginée des patients |
| `GET` | `/api/patients/search?keyword=` | Recherche patients |
| `GET` | `/api/medecins` | Liste paginée des médecins |
| `GET` | `/api/medecins/search?keyword=` | Recherche médecins |
| `GET` | `/api/rendez-vous` | Liste paginée des RDV (filtre statut) |
| `GET` | `/api/dashboard/stats` | Statistiques globales |
| `GET` | `/api/dashboard/top-medecins` | Top 5 médecins par nombre de RDV |
| `GET` | `/api/dashboard/rdv-distribution` | Répartition RDV par statut |

Toutes les routes (sauf `/api/auth/**`) nécessitent le header :

```
Authorization: Bearer <token>
```

## Documentation interactive

Swagger UI disponible au démarrage :

**http://localhost:8080/swagger-ui.html**

# Clinix Manager Backend

Système de Gestion de Clinique Médicale - API REST.

## 🛠 Technologies

- **Java 17**
- **Spring Boot 3** (Web, Data JPA, Security)
- **PostgreSQL**
- **JWT** (JSON Web Tokens)
- **Lombok & MapStruct**
- **Swagger UI / OpenAPI** pour la documentation de l'API

## ⚙️ Configuration de la base de données

L'application utilise **PostgreSQL**. Vous devez avoir le serveur en cours d'exécution et créer la base de données manuellement :

```sql
CREATE DATABASE clinix_db;
```

Identifiants par défaut (modifiables dans `src/main/resources/application.properties`) :
- **Port** : `5432`
- **Utilisateur** : `postgres`
- **Mot de passe** : ``

*Les tables et un jeu de données initial (`data.sql`) seront automatiquement créés et insérés au démarrage.*

## 🚀 Démarrage rapide

1. Assurez-vous que la base de données est opérationnelle.
2. Démarrez l'application avec Maven :
   ```bash
   mvn spring-boot:run
   ```
3. L'API démarrera sur le port `8080`.

**Documentation Swagger UI** : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

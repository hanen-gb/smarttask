# backend/ — API REST Spring Boot

> 📌 **Dossier volontairement vide pour l'instant.** Il sera rempli à l'**étape 2**.

## Ce qui sera généré ici

Le projet sera créé avec **Spring Initializr** (https://start.spring.io) avec ces
paramètres :

| Paramètre | Valeur |
|---|---|
| Project | Maven |
| Language | Java |
| Spring Boot | 4.1.1 |
| Group | `com.smarttask` |
| Artifact | `backend` |
| Package name | `com.smarttask` |
| Packaging | Jar |
| Java | 17 |

### Dépendances à cocher

| Dépendance | Rôle |
|---|---|
| **Spring Web** | Controllers REST, serveur Tomcat embarqué |
| **Spring Data JPA** | Accès base de données via Hibernate |
| **Spring Security** | Authentification et autorisation |
| **Validation** | Annotations `@NotBlank`, `@Email`, `@Size`… |
| **PostgreSQL Driver** | Pilote JDBC |
| **Flyway Migration** | Migrations SQL versionnées |
| **Lombok** | Suppression du code répétitif (getters, setters, builders) |
| **Spring Boot Actuator** | Endpoints de santé et de métriques |
| **Spring Boot DevTools** | Redémarrage automatique au changement de code |

Ajoutées manuellement dans le `pom.xml` (absentes d'Initializr) :

- `springdoc-openapi-starter-webmvc-ui` → Swagger UI
- `jjwt` (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) → génération/validation des JWT
- `testcontainers` + `postgresql` → tests d'intégration (étape 12)

## Commandes (une fois le projet généré)

```powershell
.\mvnw spring-boot:run      # lancer l'application
.\mvnw clean install        # compiler, tester, packager
.\mvnw test                 # lancer les tests
.\mvnw dependency:tree      # inspecter les dépendances
```

> ⚠️ Toujours `.\mvnw` sous PowerShell — pas `mvn`, qui n'est pas installé
> (et c'est voulu : voir `docs/00-INSTALLATIONS.md` § 4).

## Structure cible

Voir [`../docs/01-ARCHITECTURE.md`](../docs/01-ARCHITECTURE.md) § 3.4.

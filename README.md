# SmartTask — AI-Ready Task & Project Management Platform

Application web full-stack de gestion de projets et de tâches, construite pas à pas
pour apprendre **Angular** (frontend) et **Spring Boot** (backend).

> Projet pédagogique : chaque étape est documentée dans [`docs/`](docs/) avant d'être codée.

> 📍 **[BILAN-SMARTTASK.md](BILAN-SMARTTASK.md)** — document de référence à jour :
> avancement précis, inventaire du code, pièges déjà résolus, prochaine étape.
> À lire en premier pour reprendre le projet (humain ou nouvelle session IA).

---

## 1. En deux phrases

SmartTask permet à une équipe de créer des **projets**, d'y ajouter des **tâches**
(statut, priorité, échéance), de les **assigner** à des membres, de **commenter**,
de joindre des **fichiers**, de recevoir des **notifications** et de suivre
l'avancement sur un **dashboard**. L'accès est protégé par une
**authentification JWT** avec **rôles et permissions**.

## 2. Stack technique

| Couche | Technologie | Version visée |
|---|---|---|
| Frontend | Angular (standalone components + signals) | 21.x |
| Backend | Spring Boot (Web, Data JPA, Security, Validation) | 4.1.1 |
| Langage backend | Java | 17 |
| Base de données | PostgreSQL | 16 |
| Migrations SQL | Flyway | fourni par Spring Boot |
| Documentation API | OpenAPI / Swagger UI (springdoc) | 2.x |
| Tests backend | JUnit 5, Mockito, Testcontainers | — |
| Tests frontend | Karma/Jasmine (défaut Angular) | — |
| Conteneurisation | Docker + Docker Compose | 28.x / v2 |
| Build backend | Maven (via le *wrapper* `mvnw`) | 3.9.x |

## 3. Documentation (à lire dans cet ordre)

| Fichier | Contenu |
|---|---|
| [`docs/00-INSTALLATIONS.md`](docs/00-INSTALLATIONS.md) | **⬅ Commence ici.** Tout ce qu'il faut installer et configurer sur ton PC |
| [`docs/01-ARCHITECTURE.md`](docs/01-ARCHITECTURE.md) | Architecture globale, découpage en couches, arborescence des dossiers |
| [`docs/02-MODELE-DONNEES.md`](docs/02-MODELE-DONNEES.md) | Entités, relations, diagramme ER, règles métier |
| [`docs/03-API-REST.md`](docs/03-API-REST.md) | Contrat d'API : tous les endpoints, formats JSON, codes d'erreur |
| [`docs/04-ROADMAP.md`](docs/04-ROADMAP.md) | Les 14 étapes du projet, de zéro au déploiement |
| [`docs/adr/0001-choix-techniques.md`](docs/adr/0001-choix-techniques.md) | **Pourquoi** ces technologies et pas d'autres |

## 4. Démarrage rapide

```powershell
# 1. Vérifier que l'environnement est complet
.\scripts\check-env.ps1

# 2. Démarrer la base de données (Docker Desktop doit être lancé)
docker compose up -d

# 3. Backend  (disponible à partir de l'étape 2)
cd backend
.\mvnw spring-boot:run          # -> http://localhost:8080

# 4. Frontend (disponible à partir de l'étape 3)
cd frontend
npm start                       # -> http://localhost:4200
```

| Service | URL | Identifiants |
|---|---|---|
| Frontend Angular | http://localhost:4200 | — |
| API backend | http://localhost:8080/api | — |
| Swagger UI | http://localhost:8080/swagger-ui.html | — |
| PostgreSQL (Docker) | `localhost:5433` | `smarttask` / `smarttask` |
| pgAdmin | http://localhost:5050 | `admin@smarttask.local` / `admin` |

> ⚠️ Le port **5433** (et non 5432) est volontaire : tu as déjà un PostgreSQL 16
> installé localement qui occupe 5432. Voir `docs/00-INSTALLATIONS.md` § 5.

## 5. Arborescence

```
smarttask/
├── backend/            # API REST Spring Boot          (étape 2)
├── frontend/           # SPA Angular                   (étape 3)
├── docs/               # Documentation du projet
├── scripts/            # Scripts utilitaires
├── docker-compose.yml  # PostgreSQL + pgAdmin pour le dev
├── .env.example        # Modèle de variables d'environnement
└── README.md
```

## 6. Avancement

- [x] **Étape 1** — Architecture, environnement et documentation
- [ ] **Étape 2** — Backend : squelette Spring Boot + connexion PostgreSQL
- [x] **Étape 3** — Frontend : squelette Angular + routing + layout
- [x] **Étape 4** — Authentification JWT (backend + frontend)
- [ ] Étapes 5 à 14 → voir [`docs/04-ROADMAP.md`](docs/04-ROADMAP.md)

---

## Licence

Projet personnel d'apprentissage.

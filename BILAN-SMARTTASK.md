# 📍 BILAN SMARTTASK — Document de référence

> **🤖 Si tu es une IA (Claude Code) qui découvre ce projet dans une nouvelle session :
> lis ce fichier en entier avant de faire quoi que ce soit.** Il contient tout le
> contexte nécessaire : qui est l'utilisateur, ce qui a été fait, comment, les pièges
> déjà rencontrés, et ce qui reste à faire. Ne redemande pas ces informations à
> l'utilisateur, elles sont toutes ici.
>
> **Dernière mise à jour** : 2026-10-04 (session couvrant les étapes 1 à 6)

---

## 1. Qui est l'utilisateur et comment travailler avec lui

- **Hanen Goubaa**, étudiante à l'ENIG Gabès (Tunisie), apprend **Angular + Spring Boot**
  via ce projet. Communique en **français**.
- **Débutante** sur les deux frameworks. Connaît les bases de la programmation mais
  découvre Spring Boot, JPA, Spring Security, Angular, les signals, etc.
- **Style de travail attendu, très important** :
  - Agir comme un **enseignant/accompagnant**, pas comme un exécutant silencieux.
  - **Expliquer le "pourquoi" de chaque ligne de code**, pas juste le livrer.
  - **Avancer petit à petit**, une brique à la fois, avec confirmation avant de
    passer à la suite.
  - **Par défaut, c'est elle qui tape le code** (dans IntelliJ pour le backend, VS
    Code pour le frontend) — je lui donne le contenu exact à coller + les clics
    précis à faire (New → Package, New → Java Class...). Je ne code à sa place
    que pour : la configuration (XML, YAML), les corrections de bugs, les tâches
    mécaniques fastidieuses (ex: générer un thème CSS complet), ou quand elle le
    demande explicitement ("mets-le toi-même").
  - Elle confond parfois les emplacements lors de `New → Package`/`New → File`
    dans IntelliJ et VS Code (voir section Pièges § 7) — toujours vérifier le
    résultat réel sur le disque après qu'elle dit "c'est fait", plutôt que de
    supposer que c'est bien placé.
  - Elle apprécie les **tableaux, schémas Mermaid, et analogies concrètes** dans
    les explications.
  - Elle a tendance à tester l'appli elle-même et à coller les logs/captures
    d'écran — toujours les lire attentivement avant de répondre, le diagnostic
    précis compte plus qu'une réponse générique.

## 2. Objectif du projet

**SmartTask** — plateforme de gestion de projets/tâches façon Jira/Trello
simplifié, avec 3 rôles (ADMIN/MANAGER/MEMBER), projets, tâches (kanban),
commentaires, fichiers joints, notifications, dashboard, et un petit module IA à
la fin. **Mais l'objectif réel n'est pas de livrer vite : c'est d'apprendre
Angular et Spring Boot en profondeur.** Chaque étape doit être comprise avant de
passer à la suivante.

La documentation complète du **projet cible** (architecture, modèle de données,
contrat d'API à 53 endpoints, roadmap en 14 étapes, justifications techniques)
est dans [`docs/`](docs/) — **toujours valide comme référence de ce qu'on construit**,
avec quelques divergences mineures listées en § 6 ci-dessous.

| Document | Contenu |
|---|---|
| [docs/00-INSTALLATIONS.md](docs/00-INSTALLATIONS.md) | Environnement, installations, ports |
| [docs/01-ARCHITECTURE.md](docs/01-ARCHITECTURE.md) | Architecture 3 couches, conventions, arborescences cibles |
| [docs/02-MODELE-DONNEES.md](docs/02-MODELE-DONNEES.md) | Entités, relations, règles métier R1-R17 |
| [docs/03-API-REST.md](docs/03-API-REST.md) | Contrat des 53 endpoints |
| [docs/04-ROADMAP.md](docs/04-ROADMAP.md) | Les 14 étapes détaillées, avec critères de validation |
| [docs/adr/0001-choix-techniques.md](docs/adr/0001-choix-techniques.md) | Pourquoi chaque techno |

---

## 3. Avancement — 6 étapes sur 14 terminées

```
✅ Étape 1  — Architecture & environnement
✅ Étape 2  — Backend squelette (Spring Boot + PostgreSQL + Flyway + Swagger)
✅ Étape 3  — Frontend squelette (Angular + routing + layout)
✅ Étape 4  — Authentification JWT (backend ET frontend, testee de bout en bout)
✅ Étape 5  — Rôles & permissions (@PreAuthorize + page admin /users)
✅ Étape 6  — CRUD Projets (Project + ProjectMember, membres, suppression)
→  Étape 7  — CRUD Tâches + kanban                   ⬅ PROCHAINE ÉTAPE
   Étape 8  — Recherche, filtres, tri, pagination
   Étape 9  — Commentaires & notifications
   Étape 10 — Fichiers joints
   Étape 11 — Dashboard & statistiques
   Étape 12 — Tests
   Étape 13 — Docker & déploiement
   Étape 14 — Module IA
```

Détail complet de chaque étape (programme, critères de validation) :
[docs/04-ROADMAP.md](docs/04-ROADMAP.md).

---

## 4. Stack technique RÉELLEMENT utilisée (vérifiée, pas supposée)

| Élément | Valeur réelle |
|---|---|
| Backend | Spring Boot **4.1.1**, Java **17** |
| Frontend | Angular **21.2.0** (CLI 21.2.24) — *pas 22.x, corrigé en cours de route* |
| Package Java racine | `com.smarttask.backend` — *pas `com.smarttask`, voir § 6* |
| Build backend | Maven **via le wrapper** `.\mvnw` (Maven n'est pas installé en global, volontaire) |
| Base de données | PostgreSQL **16.15**, dans Docker, port **5433** (le 5432 est pris par un Postgres natif Windows) |
| Migrations | Flyway (`flyway-database-postgresql`) |
| Doc API | springdoc-openapi **2.8.6** → Swagger UI sur `/swagger-ui.html` |
| JWT | `io.jsonwebtoken` (jjwt) **0.12.6** (api + impl + jackson) |
| IDE backend | IntelliJ IDEA **Community** 2025.2.6.2 (pas Ultimate — pas de support Spring natif, on lance tout via `.\mvnw` en CLI) |
| IDE frontend | VS Code |
| OS | Windows 10 Pro, PowerShell |

---

## 5. Ce qui a été construit — inventaire précis

### 5.1 Backend (`backend/src/main/java/com/smarttask/backend/`)

```
BackendApplication.java              -- point d'entree

common/
├── config/
│   ├── JpaConfig.java               -- @EnableJpaAuditing (active createdAt/updatedAt auto)
│   ├── OpenApiConfig.java           -- titre/description Swagger ("SmartTask API")
│   └── SecurityConfig.java          -- LA config centrale (voir detail ci-dessous)
└── entity/
    └── BaseEntity.java              -- @MappedSuperclass : id (UUID), createdAt, updatedAt

user/
├── Role.java                        -- enum ADMIN, MANAGER, MEMBER
├── User.java                        -- @Entity implements UserDetails (voir detail)
└── UserRepository.java              -- JpaRepository<User,UUID> + findByEmail()

security/
├── JwtService.java                  -- genere/valide les JWT (jjwt 0.12.x API fluide)
├── CustomUserDetailsService.java    -- implements UserDetailsService, cherche via UserRepository
└── JwtAuthenticationFilter.java     -- extends OncePerRequestFilter, lit "Authorization: Bearer ..."

auth/
├── dto/
│   ├── RegisterRequest.java         -- record, @NotBlank/@Email/@Size/@Pattern
│   ├── LoginRequest.java            -- record
│   └── AuthResponse.java            -- record + record imbrique UserSummary
├── AuthService.java                 -- register(), login(), toUserSummary() (reutilisee par /me)
└── AuthController.java              -- POST /register (201), POST /login, GET /me (protege)

user/dto/                            -- (etape 5)
├── UserResponse.java                -- record (id, email, nom, role, enabled)
├── UpdateProfileRequest.java        -- record (firstName, lastName)
└── UpdateRoleRequest.java           -- record (role)
user/UserService.java                -- (etape 5) getAllUsers(), getUserById(), updateOwnProfile(),
                                          updateRole(), updateStatus()
user/UserController.java             -- (etape 5) GET /users (@PreAuthorize ADMIN), GET /users/{id},
                                          PUT /users/me, PUT /users/{id}/role (ADMIN), PUT /users/{id}/status (ADMIN)

project/                             -- (etape 6)
├── ProjectStatus.java               -- enum PLANNING/ACTIVE/ON_HOLD/COMPLETED/ARCHIVED
├── ProjectRole.java                 -- enum OWNER/MANAGER/CONTRIBUTOR/VIEWER
├── Project.java                     -- @Entity, @ManyToOne(LAZY) vers owner (User)
├── ProjectMember.java               -- @Entity, @ManyToOne vers Project ET User + projectRole
├── ProjectRepository.java
├── ProjectMemberRepository.java     -- findByProjectIdAndUserId, findByUserId, existsBy...
├── dto/
│   ├── ProjectRequest.java          -- record (name, description, color, startDate, endDate)
│   ├── ProjectResponse.java         -- record + record imbrique OwnerSummary
│   └── AddMemberRequest.java        -- record (userId)
├── ProjectService.java              -- createProject() [R4: createur->OWNER membre auto],
                                          getMyProjects(), getProjectById() [controle contextuel
                                          requireMembership], deleteProject() [R6: owner/ADMIN
                                          seulement], addMember() [requireOwnerOrManager]
└── ProjectController.java           -- GET/POST /projects, GET/DELETE /projects/{id},
                                          POST /projects/{id}/members
```

**`User.java` implémente `UserDetails`** directement (pas de classe wrapper séparée) :
`getUsername()` → email, `getPassword()` → passwordHash, `getAuthorities()` →
`["ROLE_" + role.name()]`, `isEnabled()` → le champ `enabled`.

**`SecurityConfig.java`** — les règles actuelles :
```java
PUBLIC_ROUTES = {
    "/api/auth/register", "/api/auth/login",   // PAS "/api/auth/**" (sinon /me serait public)
    "/swagger-ui.html", "/swagger-ui/**",       // les DEUX sont necessaires, /swagger-ui/** seul ne matche pas /swagger-ui.html
    "/v3/api-docs/**",
    "/actuator/health"
};
```
- CSRF désactivé (stateless, pas de session/cookie)
- CORS autorisé pour `http://localhost:4200` uniquement
- Session `STATELESS`
- `JwtAuthenticationFilter` inséré avant `UsernamePasswordAuthenticationFilter`
- `BCryptPasswordEncoder` pour les mots de passe
- ⚠️ Pas de `httpBasic()`/`formLogin()` configuré → une route protégée sans token
  renvoie **`403`**, pas `401` (différent de ce que dit `docs/03-API-REST.md`,
  comportement par défaut de Spring Security sans entry point configuré — **ce
  n'est pas un bug**, mais une différence à connaître)
- `.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()` — **ajouté étape 6**,
  CRITIQUE. Sans cette ligne, TOUTE erreur métier (`ResponseStatusException`,
  409/404/etc.) était écrasée par un `403` avant d'atteindre le client. Voir
  piège § 7 n°15.
- `@EnableMethodSecurity` — ajouté étape 5, active `@PreAuthorize`.

### 5.2 Migrations Flyway

```
backend/src/main/resources/db/migration/
├── V1__create_users.sql                    -- table users
└── V2__create_projects_and_members.sql     -- (etape 6) tables projects, project_members
                                                (ON DELETE CASCADE sur project_members.project_id)
```
Prochaine migration à créer : `V3__create_tasks.sql` (étape 7).

### 5.3 Configuration (`application.yml` / `application-dev.yml`)

- `application.yml` : nom de l'app, port 8080, profil actif `dev`, actuator
  (`health`, `info` exposés)
- `application-dev.yml` : datasource (`jdbc:postgresql://localhost:5433/smarttask`,
  user/pass `smarttask`/`smarttask`), `ddl-auto: validate` (jamais `update` —
  décision ADR), `show-sql: true`, Flyway activé, config JWT :
  ```yaml
  jwt:
    secret: dev-secret-key-never-use-this-in-production-min-32-chars
    access-token-expiration-ms: 900000       # 15 min
    refresh-token-expiration-ms: 604800000   # 7 jours -- DEFINI MAIS PAS ENCORE UTILISE
  ```

### 5.4 Frontend (`frontend/src/app/`)

```
app.config.ts       -- provideRouter, provideHttpClient(withInterceptors([jwtInterceptor]))
app.routes.ts        -- voir structure ci-dessous
app.html              -- juste <router-outlet></router-outlet>

core/
├── models/
│   ├── user.model.ts        -- Role, UserSummary, UserResponse (etape 5)
│   ├── auth.model.ts        -- RegisterRequest, LoginRequest, AuthResponse (miroir backend)
│   └── project.model.ts     -- (etape 6) Project, ProjectStatus, ProjectRequest
├── services/
│   ├── token.service.ts     -- localStorage (cle "smarttask_access_token")
│   ├── auth.service.ts      -- signal currentUser, login(), register(), logout(),
│   │                           fetchCurrentUser() appele au demarrage si token present
│   ├── user.service.ts      -- (etape 5) getAll(), updateRole(), updateStatus()
│   └── project.service.ts   -- (etape 6) getMyProjects(), create(), getById(), delete()
├── interceptors/
│   └── jwt.interceptor.ts   -- intercepteur fonctionnel, ajoute "Authorization: Bearer ..."
└── guards/
    ├── auth.guard.ts        -- guard fonctionnel, verifie la presence du token
    └── role.guard.ts        -- (etape 5) guard PARAMETRABLE : roleGuard(['ADMIN'])

layout/main-layout/    -- navbar bleue + sidebar (Dashboard/Projets/Utilisateurs-si-ADMIN)
                           + nom utilisateur + bouton deconnexion

features/
├── auth/
│   ├── login/        -- formulaire reactif, design bleu/jaune (ecran split)
│   └── register/     -- idem
├── dashboard/         -- page placeholder (construite a l'etape 11)
├── users/
│   └── user-list/     -- (etape 5) page admin /users, tableau + toggle actif/desactive
└── projects/           -- (etape 6)
    ├── project-list/   -- liste "mes projets" + formulaire inline de creation
    └── project-detail/ -- detail d'un projet + bouton suppression (teste R6)

environments/
├── environment.ts       -- apiUrl: http://localhost:8080/api
└── environment.prod.ts  -- apiUrl: /api
```

**Structure des routes (`app.routes.ts`)** :
```
/login, /register           -- PUBLIQUES, en dehors du layout (ecran plein, pas de navbar)
/  (= MainLayoutComponent)  -- protege par authGuard
  ├── /dashboard            -- lazy-loaded
  ├── /users                -- lazy-loaded, PROTEGE EN PLUS par roleGuard(['ADMIN'])
  ├── /projects              -- lazy-loaded
  ├── /projects/:id          -- lazy-loaded
  └── '' → redirectTo dashboard
```

**Design** : thème bleu (`#1D4ED8` primaire, `#1E3A8A` foncé) + jaune/ambre
(`#F59E0B`, utilisé en accent uniquement — pastille logo, soulignement lien actif,
cercle décoratif) dans `src/styles.scss` (variables CSS globales) + fichiers
`.scss` par composant.

### 5.5 Comptes de test déjà en base

| Email | Mot de passe | Créé via |
|---|---|---|
| `hanen@smarttask.io` | `Password123` | Swagger (étape 2) — **confirmé fonctionnel** |

D'autres comptes de test ont pu être créés pendant les essais UI (étape 4) avec
des emails variables — pas de liste garantie, mais celui ci-dessus est fiable.

---

## 6. Divergences connues entre `docs/` et le code réel

| Doc dit | Réalité | Impact |
|---|---|---|
| Package `com.smarttask` | `com.smarttask.backend` | Spring Initializr combine group+artifact automatiquement. Aucun impact fonctionnel, juste un niveau de dossier en plus. |
| Angular `22.x` | Angular **21.2.0** | `ng new` a tiré la dernière version dispo au moment de l'install. Mettre à jour la doc si on la relit un jour. |
| Routes protégées → `401` | → **`403`** | Pas de `httpBasic()`/`formLogin()` configuré (choix voulu, JWT pur). Comportement par défaut de Spring Security. |
| `AuthResponse.refreshToken` rempli | Toujours **`null`** | Le refresh token n'est pas encore implémenté (pas de `/api/auth/refresh`). À faire si besoin, pas bloquant pour la suite. |
| `GlobalExceptionHandler` (étape 2) | **Pas encore créé** | Les erreurs utilisent le format par défaut de Spring Boot / `ResponseStatusException`, pas encore le format unifié décrit dans `docs/03-API-REST.md` § 1. **De plus, la réponse d'erreur actuelle inclut la stack trace Java complète dans le JSON** (champ `"trace"`) — acceptable en dev, **à corriger avant l'étape 13** (fuite d'information en production). Priorité montée : à faire tôt étape 7. |

---

## 7. Pièges déjà rencontrés — pour ne pas les refaire

| # | Piège | Solution appliquée |
|---|---|---|
| 1 | Commentaire XML avec `--` au milieu casse `pom.xml` (`Non-parseable POM`) | Ne jamais mettre `--` ailleurs qu'en toute fin d'un commentaire XML |
| 2 | `new DaoAuthenticationProvider()` + `setUserDetailsService(...)` → erreur de compilation | Dans cette version de Spring Security, le constructeur sans argument et le setter ont été supprimés. Utiliser `new DaoAuthenticationProvider(userDetailsService)` |
| 3 | `org.springframework.lang.NonNull` déprécié | Utiliser `org.jspecify.annotations.NonNull` (Spring Framework 7) |
| 4 | `/swagger-ui/**` ne matche PAS `/swagger-ui.html` | Ajouter les deux patterns explicitement dans `PUBLIC_ROUTES` |
| 5 | `/api/auth/**` rendait `/api/auth/me` public par erreur | Lister les routes publiques exactes (`/api/auth/register`, `/api/auth/login`), pas de wildcard sur tout `auth` |
| 6 | Angular : `readonly form = this.fb.group(...)` → `TS2729: used before initialization` | Les initialiseurs de champs s'exécutent avant le corps du constructeur. Utiliser `inject()` pour `fb` au lieu de l'injection par constructeur quand un autre champ en dépend immédiatement |
| 7 | IntelliJ / VS Code compactent visuellement les dossiers imbriqués à enfant unique (`core/models` affiché sur une ligne) | **Normal, cosmétique.** Toujours vérifier sur le disque (`find`/`ls`) avant de conclure à un problème |
| 8 | Fichiers créés au mauvais niveau lors de `New → Package`/`New → Java Class` (ex: `AuthController` dans `auth/dto/` au lieu de `auth/`, ou un package égaré nommé `AuthResponse/` créé par erreur) | Vérifier la ligne `package` de chaque fichier vs son emplacement physique réel ; déplacer avec `mv` si besoin. **Cause récurrente de fichiers "en rouge" dans l'IDE** |
| 9 | Docker Desktop plantait (`com.docker.build: exit status 1`), `LxssManager` bloqué | `wsl --shutdown` + `wsl --unregister docker-desktop` + tuer tous les process `com.docker.*`/`Docker Desktop` + relancer Docker Desktop (il recrée la distro WSL2 proprement) |
| 10 | `PGADMIN_DEFAULT_EMAIL: admin@smarttask.local` rejeté (`.local` = domaine réservé) | Utiliser `admin@smarttask.io` |
| 11 | Premiers appels HTTP très lents après démarrage (jusqu'à timeout) | Normal : "démarrage à froid" de la couche réseau Windows↔Docker/WSL2 + JIT Java. Se stabilise à 200-700ms après quelques appels. **Rien à corriger** |
| 12 | `Ctrl+C` dans un terminal Windows pour "copier" du texte | **Ça tue le processus en cours**, ça ne copie rien ! Toujours sélectionner à la souris (copie automatique) pour copier dans un terminal Windows |
| 13 | `JAVA_HOME` nouvellement défini pas pris en compte dans le terminal IntelliJ/VS Code | Il faut fermer et **rouvrir tout l'IDE** (pas juste un nouvel onglet terminal) — l'IDE transmet son propre environnement figé au démarrage |
| 14 | `docker compose stop` qui plante (`tried to kill container, but did not receive an exit event`) | Récurrent, lié à l'instabilité Docker Desktop/WSL2 sur cette machine. Réessayer une fois suffit généralement ; sinon fermer Docker Desktop directement règle le problème |
| 15 | ⚠️ **IMPORTANT** — Toute `ResponseStatusException` (409, 404...) renvoyait **`403`** au lieu du vrai code, avec un corps vide | Spring Boot redirige en interne (`forward`) vers `/error` pour construire la reponse. Cette route n'etait **pas** dans `PUBLIC_ROUTES`, donc Spring Security la bloquait et ecrasait le vrai code. **Fix** : `.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()` dans `authorizeHttpRequests`. Touchait TOUTE l'appli depuis l'étape 4 (même le `409` "email déjà utilisé"), pas juste les projets. Diagnostiqué en comparant le vrai statut *résolu* dans les logs serveur (`ResponseStatusExceptionResolver : Resolved [...]`) avec le statut *réellement reçu* côté client — les deux divergeaient, preuve que le problème était entre les deux, pas dans le code métier |
| 16 | PowerShell `Invoke-RestMethod`/`Invoke-WebRequest` (Windows PowerShell 5.1) peut afficher un `StatusCode` qui ne correspond pas toujours exactement à ce que montre `curl -w "%{http_code}"` sur une même reponse | Face a un code HTTP surprenant, **toujours croiser avec `curl`** et/ou les logs serveur avant de conclure a un bug applicatif — ne jamais faire confiance a un seul outil de diagnostic |

---

## 8. État de l'environnement (pour redémarrer une session)

### Ports du projet
| Port | Service |
|---|---|
| 4200 | Angular dev server (`npm start` dans `frontend/`) |
| 8080 | API Spring Boot (`.\mvnw spring-boot:run` dans `backend/`) |
| 5433 | PostgreSQL du projet (Docker — **5432 est pris par un Postgres natif Windows, ne pas y toucher**) |
| 5050 | pgAdmin (`admin@smarttask.io` / `admin`) |

### Pour relancer après une pause
```powershell
# 1. Docker Desktop doit tourner (lancer depuis le menu Demarrer si besoin)
cd "C:\Users\Hanen GB\Desktop\smarttask"
docker compose up -d

# 2. Backend
cd backend
.\mvnw spring-boot:run

# 3. Frontend (autre terminal)
cd frontend
npm start
```

### État au moment de la rédaction de ce fichier (mise à jour étape 6)
Docker + backend (`.\mvnw spring-boot:run`, port 8080) tournaient activement
pendant la rédaction, mais seront probablement fermés à la fin de la session —
vérifier avec `docker info` / `curl localhost:8080/actuator/health` plutôt que
de supposer, comme indiqué en § 11.

---

## 9. État Git

- Dépôt local (`branch main`), **remote configuré** :
  `https://github.com/hanen-gb/smarttask.git` (publié via GitHub Desktop).
- **3 commits** :
  ```
  81027cc feat: etapes 2 a 4 (backend Spring Boot + frontend Angular)
  44be99a fix(scripts): detection de la version Java dans check-env.ps1
  1b2bdc6 chore: etape 1 - architecture, documentation et environnement
  ```
- ✅ Tout le travail des étapes 1 à 4 (jusqu'a la creation des DTO `user.dto` de
  l'etape 5 inclue) est maintenant commité.
- ⚠️ **Le commit local n'a PAS encore été poussé vers GitHub** — l'utilisatrice
  doit ouvrir **GitHub Desktop** et cliquer **"Push origin"** pour le publier.
  Vérifier à chaque nouvelle session si c'est fait (`git log` local vs ce qui
  est visible sur github.com/hanen-gb/smarttask).
- ⚠️ `.claude/` (racine du projet) a été ajouté à `.gitignore` — c'est un
  dossier technique interne à l'outil Claude Code, pas du code du projet.
  `frontend/.claude/CLAUDE.md` (bonnes pratiques Angular, générées par
  `ng new`) reste suivi, lui, car c'est un vrai fichier de référence utile.
- **Rappel pour la suite** : committer à la fin de chaque étape terminée (pas
  seulement tous les 3-4 étapes comme cette fois) pour limiter le risque.

---

## 10. Prochaine étape : Étape 5 — Rôles et permissions

D'après [docs/04-ROADMAP.md](docs/04-ROADMAP.md) :

- **Backend** : `@EnableMethodSecurity` + `@PreAuthorize` sur les futurs
  contrôleurs, autorisation **contextuelle** dans les services (ex: "est-il
  membre de CE projet précis ?" — pas seulement un contrôle de rôle global),
  endpoints d'administration des utilisateurs, profil modifiable.
- **Frontend** : `roleGuard`, affichage conditionnel selon le rôle dans les
  templates (`@if (user.role === 'ADMIN')`).
- **Avant de s'y lancer**, envisager de régler en premier :
  - Le commit Git en retard (§ 9)
  - Éventuellement le `GlobalExceptionHandler` manquant (§ 6), pour avoir un
    format d'erreur propre avant d'ajouter plus d'endpoints

---

## 11. Comment utiliser ce fichier dans une nouvelle session

1. Lis-le en entier.
2. Vérifie l'état réel avec quelques commandes rapides avant de supposer quoi
   que ce soit (`git status`, `git log --oneline`, `docker compose ps`,
   `find backend/src -name "*.java"`) — ce fichier est une **photo à un instant
   donné**, pas une garantie que rien n'a changé depuis.
3. Reprends le style de travail décrit en § 1 (enseignant, petites étapes,
   explications).
4. **Mets à jour ce fichier** à la fin de chaque session significative (nouvelle
   étape terminée, nouveau piège rencontré, nouvelle divergence avec `docs/`) —
   c'est lui qui permet la continuité d'une session à l'autre.

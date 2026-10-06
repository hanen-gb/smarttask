# 📍 BILAN SMARTTASK — Document de référence

> **🤖 Si tu es une IA (Claude Code) qui découvre ce projet dans une nouvelle session :
> lis ce fichier en entier avant de faire quoi que ce soit.** Il contient tout le
> contexte nécessaire : qui est l'utilisateur, ce qui a été fait, comment, les pièges
> déjà rencontrés, et ce qui reste à faire. Ne redemande pas ces informations à
> l'utilisateur, elles sont toutes ici.
>
> **Dernière mise à jour** : 2026-10-06 (session couvrant les étapes 7 à 12 + un
> gros lot CRUD/permissions/mode sombre + une ronde complète de retours
> utilisateur post-tests manuels : contraste pagination, icônes, sélection de
> personnes par recherche, formulaires de création enrichis, page Profil)

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
  - **Mode "build then explain"** : quand elle manque de temps, elle demande
    explicitement de construire vite moi-même ("fais-le puis explique"), avec
    l'attente d'une explication complète et rétroactive ensuite (glossaire,
    schémas Mermaid, détail terme par terme). Ce mode a été utilisé pour les
    étapes 7 à 12 et pour la ronde de retours utilisateur de cette session —
    ce n'est PAS un abandon du mode enseignant, juste son application différée.
  - Les commits Git sont **ma responsabilité** (toujours avec le trailer
    `Co-Authored-By`) ; le **push vers GitHub reste le sien**, via GitHub
    Desktop — je n'ai pas les identifiants pour pousser.
  - Quand elle teste l'appli en main et remonte plusieurs points en vrac dans
    un seul message, les trier et vérifier chaque hypothèse (ex: "l'admin ne
    peut pas créer de tâche" peut être une vraie régression backend OU un
    simple formulaire frontend incomplet qui donne cette impression — toujours
    vérifier par `curl` avant de conclure).

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

## 3. Avancement — 12 étapes sur 14 terminées

```
✅ Étape 1  — Architecture & environnement
✅ Étape 2  — Backend squelette (Spring Boot + PostgreSQL + Flyway + Swagger)
✅ Étape 3  — Frontend squelette (Angular + routing + layout)
✅ Étape 4  — Authentification JWT (backend ET frontend, testee de bout en bout)
✅ Étape 5  — Rôles & permissions (@PreAuthorize + page admin /users)
✅ Étape 6  — CRUD Projets (Project + ProjectMember, membres, suppression)
✅ Étape 7  — CRUD Tâches + kanban
✅ Étape 8  — Recherche, filtres, tri, pagination (Specifications dynamiques)
✅ Étape 9  — Commentaires & notifications (event-driven, ApplicationEventPublisher)
✅ Étape 10 — Fichiers joints (FileStorageService, upload/download securise)
✅ Étape 11 — Dashboard & statistiques (faite APRES l'étape 12, a la demande explicite)
✅ Étape 12 — Tests (13 tests backend : unit/web-slice/integration Testcontainers,
                      5 tests frontend)
   Étape 13 — Docker & déploiement                   ⬅ PROCHAINE ÉTAPE PLANIFIÉE
   Étape 14 — Module IA
```

**Après l'étape 12**, un gros lot de travail hors-roadmap a eu lieu, déclenché
par des tests manuels de l'utilisatrice sur l'app en conditions réelles (avec
plusieurs comptes : ADMIN, MANAGER, MEMBER) : édition complète projets/tâches,
gestion des membres, changement de rôles, **mode sombre**, puis toute une ronde
de corrections UX/design détaillées en § 5.6. Les étapes 13/14 n'ont pas encore
démarré — l'app est actuellement dans une phase de polish fonctionnel avant de
s'y attaquer.

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
└── ProjectController.java           -- GET/POST /projects, GET/PUT/DELETE /projects/{id},
                                          GET/POST /projects/{id}/members,
                                          DELETE /projects/{id}/members/{userId}

task/                                 -- (etape 7-8)
├── TaskStatus.java, TaskPriority.java -- enums
├── Task.java                        -- @Entity, 2 @ManyToOne vers User (assignee ET
│                                        reporter) -- @JoinColumn explicite sur chacun,
│                                        Hibernate ne peut pas deviner lequel est lequel
├── TaskRepository.java              -- extends JpaSpecificationExecutor<Task> (etape 8)
├── TaskSpecifications.java          -- Specifications statiques combinables (projectIdEquals,
│                                        assigneeIdEquals, unassigned, statusIn, priorityIn,
│                                        titleContains, dueBefore)
├── dto/ (TaskRequest, UpdateTaskRequest, TaskResponse+UserRef imbrique)
├── TaskService.java                 -- createTask() [assigneeId optionnel, publie
│   │                                    TaskAssignedEvent si assigne], getTaskById(),
│   │                                    updateTask(), getBoard() [group by status],
│   │                                    searchTasks() [assemble les Specifications
│   │                                    dynamiquement selon les filtres non-null],
│   │                                    updateStatus() [R10: DONE renseigne completedAt],
│   │                                    updateAssignee() [requireOwnerOrManagerOfProject
│   │                                    -- PLUS STRICT que requireCanModify : un simple
│   │                                    MEMBER ne peut PAS re-assigner a quelqu'un d'autre]
│   └── resolveAssignee() [R9: on ne peut assigner qu'a un membre du projet, 409 sinon]
└── TaskController.java              -- GET /projects/{id}/board, POST/GET/PUT/DELETE /tasks/{id},
                                          PATCH /tasks/{id}/status, PATCH /tasks/{id}/assignee,
                                          GET /tasks (recherche paginee multi-criteres)

comment/ + notification/              -- (etape 9, event-driven)
├── Comment.java, CommentRepository, CommentService/Controller
├── notification/event/TaskAssignedEvent.java, CommentAddedEvent.java
├── NotificationListener.java        -- @EventListener, cree une Notification en base
│                                        -- respecte R17 : jamais de self-notification
│                                        (si l'auteur de l'action == le destinataire, on
│                                        n'envoie rien)
└── Notification.java/Repository/Service/Controller -- GET /notifications, PATCH .../read

attachment/                           -- (etape 10)
├── Attachment.java/Repository
├── FileStorageService.java          -- INTERFACE (store/load/delete) -- permet de
│                                        remplacer le disque local par S3 plus tard
│                                        SANS toucher AttachmentService/Controller
├── LocalFileStorageService.java     -- implementation disque local, nom de fichier
│                                        TOUJOURS un UUID genere (jamais le nom fourni
│                                        par l'utilisateur -- anti path-traversal)
└── AttachmentService/Controller     -- POST (multipart) /tasks/{id}/attachments,
                                          GET .../attachments, GET /attachments/{id}/download,
                                          DELETE /attachments/{id} -- whitelist de content-types

dashboard/                            -- (etape 11, faite apres l'etape 12)
└── DashboardService/Controller      -- GET /dashboard/stats (compteurs par statut/priorite,
                                          taches en retard, etc., scope selon le role)
```

**Photo de profil (ajoutee hors-roadmap, cette session)** -- reutilise directement
le patron `FileStorageService` de l'etape 10, pas de nouvelle abstraction :
```
user/UserService.java   -- uploadAvatar() [valide que c'est une image, stocke via
│                            FileStorageService, enregistre juste le nom de fichier
│                            genere sur User.avatarUrl (colonne deja presente depuis
│                            l'etape 2, jamais utilisee jusqu'ici)], loadAvatar()
user/UserController.java -- POST /users/me/avatar (multipart, authentifie),
                             GET /users/{id}/avatar (PUBLIQUE -- voir piege § 7 n°18)
```
`UserResponse` expose maintenant `avatarUrl` : soit `null`, soit un chemin
STABLE `"/users/{id}/avatar"` (jamais le nom de fichier UUID stocke sur le
disque, qui reste un detail d'implementation cache -- meme philosophie que les
pieces jointes).

**`User.java` implémente `UserDetails`** directement (pas de classe wrapper séparée) :
`getUsername()` → email, `getPassword()` → passwordHash, `getAuthorities()` →
`["ROLE_" + role.name()]`, `isEnabled()` → le champ `enabled`.

**`SecurityConfig.java`** — les règles actuelles :
```java
PUBLIC_ROUTES = {
    "/api/auth/register", "/api/auth/login",   // PAS "/api/auth/**" (sinon /me serait public)
    "/swagger-ui.html", "/swagger-ui/**",       // les DEUX sont necessaires, /swagger-ui/** seul ne matche pas /swagger-ui.html
    "/v3/api-docs/**",
    "/actuator/health",
    "/api/users/*/avatar"    // ajoute cette session -- une photo de profil n'est pas
                             // sensible, et <img src="..."> ne peut pas envoyer
                             // d'en-tete Authorization (contrairement a HttpClient)
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
├── V2__create_projects_and_members.sql     -- (etape 6) tables projects, project_members
│                                               (ON DELETE CASCADE sur project_members.project_id)
├── V3__create_tasks.sql                    -- (etape 7) table tasks (assignee_id/reporter_id)
├── V4__create_comments_notifications.sql   -- (etape 9)
└── V5__create_attachments.sql              -- (etape 10)
```
`ddl-auto: validate` reste la regle (jamais `update`) -- **aucune migration V6**
n'a ete necessaire pour la photo de profil : la colonne `avatar_url` existait
deja depuis V1, juste jamais utilisee avant cette session.

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
├── models/           -- un fichier par entite, miroir exact des records backend
│   (user, auth, project, task, comment, notification, attachment)
├── services/         -- un service par entite (HttpClient), + :
│   ├── token.service.ts     -- localStorage (cle "smarttask_access_token")
│   ├── auth.service.ts      -- signal currentUser, login/register/logout,
│   │                           refreshCurrentUser() [ajoute cette session, pour que
│   │                           la navbar se mette a jour apres edition du profil]
│   └── theme.service.ts     -- (ajoute cette session) signal Theme 'light'|'dark',
│                                localStorage "smarttask_theme", respecte
│                                prefers-color-scheme si rien d'enregistre,
│                                toggle() + setTheme() [set() ajoute pour la page Profil]
├── interceptors/jwt.interceptor.ts
└── guards/ (auth.guard, role.guard parametrable)

shared/                -- (nouveau cette session) composants reutilisables inter-features
└── person-picker/      -- <app-person-picker [people]="..." (picked)="...">
                            champ "tape un nom -> suggestions filtrees en direct",
                            remplace tous les <select> de choix de personne. Inputs/
                            outputs en signals (input()/output(), pas @Input/@Output).

layout/main-layout/    -- navbar + sidebar. Nom utilisateur -> lien vers /profile.
                          Icone theme : SVG inline (soleil/lune), plus d'emoji.

features/
├── auth/ (login, register)
├── dashboard/          -- stats (etape 11)
├── profile/            -- (NOUVEAU cette session) page /profile : photo de profil
│                          (upload + apercu), prenom/nom editables, email/role en
│                          lecture seule, choix du theme clair/sombre explicite.
│                          PAS de reglage de langue (decision explicite : l'app
│                          reste francais uniquement pour l'instant, coherent avec
│                          l'ADR 0001 -- voir § 6)
├── users/
│   └── user-list/      -- tableau avec avatars (initiales), changement de role en
│                           DEUX temps (select -> bouton ✓ pour confirmer / ✕ pour
│                           annuler, plus jamais instantane), confirm() JS avant
│                           activer/desactiver un compte
└── projects/
    ├── project-list/    -- creation de projet AVEC selection des membres initiaux
    │                        (app-person-picker + chips), via des appels addMember()
    │                        sequentiels juste apres la creation (pas de modif
    │                        backend -- le createur est OWNER donc autorise)
    ├── project-detail/  -- edition complete (nom/description/statut/dates),
    │                        liste des membres style "Teams" (avatars dégradé bleu->
    │                        jaune, pastille active/inactive), ajout via person-picker
    ├── project-board/   -- kanban. Formulaire "+ Nouvelle tache" enrichi cette
    │                        session : priorite + assignation (person-picker) DES
    │                        LA CREATION, plus seulement apres coup
    └── tasks/task-detail/ -- edition complete + assignation via person-picker
                               (bascule select -> "chip" avec ✕ une fois assigne)

environments/ (apiUrl: http://localhost:8080/api en dev, /api en prod)
```

**Structure des routes (`app.routes.ts`)** :
```
/login, /register           -- PUBLIQUES, en dehors du layout (ecran plein, pas de navbar)
/  (= MainLayoutComponent)  -- protege par authGuard
  ├── /dashboard
  ├── /profile              -- (nouveau cette session)
  ├── /users                -- PROTEGE EN PLUS par roleGuard(['ADMIN'])
  ├── /projects, /projects/:id, /projects/:id/board
  ├── /tasks, /tasks/:id
  └── '' → redirectTo dashboard
```

**Design** : thème bleu (`#1D4ED8` primaire) + jaune/ambre (`#F59E0B`, accent)
dans `src/styles.scss` (variables CSS globales, redefinies a l'identique sous
`:root[data-theme='dark']` pour le mode sombre -- aucun composant n'a besoin
d'etre modifie pour ca). Composants partages ajoutes dans `styles.scss` cette
session : `.btn-sm` (boutons compacts pour tableaux/listes, deplaces depuis
project-detail car reutilises par user-list), `.avatar` (rond degrade bleu→jaune
avec initiales), `.field-row` (grille 2 colonnes pour formulaires).

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
| 17 | ⚠️ **IMPORTANT** — Login impossible depuis le frontend ("Email ou mot de passe incorrect") alors que les memes identifiants marchaient via curl/Swagger | `JwtAuthenticationFilter` ne gerait pas les `JwtException` (token perime/corrompu). Comme `jwtInterceptor` attache le token a **toutes** les requetes (meme `/login`), un vieux token perime en `localStorage` faisait planter la requete de login elle-meme. **Fix** : try/catch `JwtException` dans `JwtAuthenticationFilter`, la requete continue simplement non-authentifiee au lieu de planter. Si ce symptome revient : `localStorage.clear()` dans la console du navigateur en attendant, mais le vrai fix est deja applique |
| 18 | ⚠️ **IMPORTANT** — Les boutons de pagination ("← Précédent" / "Suivant →") etaient **invisibles en mode clair** alors qu'ils s'affichaient bien en mode sombre | Le CSS de l'app etait correct, mais `:root` ne declarait pas la propriete `color-scheme`. Sans elle, les **widgets natifs non-stylés** (un `<button class="btn">` sans variante `--primary`/`--accent` n'avait ni `background` ni `color` explicites) suivent la preference sombre/claire du **systeme/navigateur**, pas celle de l'app -- sur un Windows en theme sombre, ca rendait un bouton natif sombre sur une page claire. **Fix double** : (1) `color-scheme: light` sous `:root` et `color-scheme: dark` sous `:root[data-theme='dark']`, pour forcer les widgets natifs a suivre le theme de l'app : (2) `.btn` a recu des valeurs par defaut explicites (`background: var(--color-surface)`, `color: var(--color-text)`) pour ne plus JAMAIS dependre d'un rendu navigateur par defaut, meme sans cette propriete. **Lecon generale** : un bouton/input/select sans aucun style de couleur explicite n'est pas "neutre", il herite du theme du NAVIGATEUR, pas de celui de la page |
| 19 | `curl -F "file=@/tmp/xxx"` echoue silencieusement (`exit code 26`) sous le curl natif Windows, meme depuis Git Bash | Le curl natif Windows ne comprend pas les chemins style Git-Bash (`/tmp/...`, `/c/Users/...`) dans l'argument `-F @chemin`. **Fix** : convertir en chemin Windows avant de l'utiliser dans `-F`, via `cygpath -w "$CHEMIN_POSIX"` |
| 20 | RxJS : `(cond ? forkJoin(obsArray) : of(null)).subscribe(...)` → erreur TS2349 "this expression is not callable" | `forkJoin` d'un tableau renvoie `Observable<T[]>`, alors que `of(null)` renvoie `Observable<null>` -- les deux branches du ternaire n'ont pas le meme type d'Observable, et TypeScript ne sait plus choisir la bonne surcharge de `.subscribe()`. **Fix** : faire correspondre les types des deux branches (ici, `of([])` au lieu de `of(null)`, puisque `forkJoin` renvoie toujours un tableau) |

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

### État au moment de la rédaction de ce fichier (mise à jour 2026-10-06)
Docker (Postgres `smarttask-postgres` sur 5433), backend (port 8080) et
frontend (port 4200, `ng serve`) tournaient activement pendant la rédaction —
toujours vérifier avec `docker ps` / `curl localhost:8080/actuator/health` /
`curl localhost:4200` plutôt que de supposer, comme indiqué en § 11.

---

## 9. État Git

- Dépôt local (`branch main`), **remote configuré** :
  `https://github.com/hanen-gb/smarttask.git` (publié via GitHub Desktop).
- **Commits** (du plus recent au plus ancien) :
  ```
  (a venir) fix/feat: ronde de retours UX (contraste pagination, icones theme,
            person-picker reutilisable, creation projet/tache enrichie,
            page /users en 2 temps, page Profil + upload photo)
  f029b2c style: refonte esthetique de la page detail projet
  867815f feat: edition complete des projets/taches, membres, roles, mode sombre
  30d8ad9 feat: etape 11 (dashboard et statistiques)
  7045c5b feat: etape 12 (tests backend et frontend)
  0c2dc81 feat: etapes 9 et 10 (commentaires/notifications, fichiers joints)
  4feccd9 feat: etapes 7 et 8 (CRUD Taches + kanban, recherche/filtres/pagination)
  b0fd2eb docs: ajout du piege JwtAuthenticationFilter au bilan
  e678907 fix(security): JwtAuthenticationFilter ne gerait pas les tokens invalides/perimes
  616c3b0 feat: etapes 5 et 6 (roles/permissions + CRUD Projets)
  81027cc feat: etapes 2 a 4 (backend Spring Boot + frontend Angular)
  44be99a fix(scripts): detection de la version Java dans check-env.ps1
  1b2bdc6 chore: etape 1 - architecture, documentation et environnement
  ```
- ✅ Tout le travail des étapes 1 à 12 est commité.
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

## 10. Ce qui reste à faire

### 10.1 Dette accumulée (pas bloquante, mais à garder en tête)
- `GlobalExceptionHandler` toujours **absent** (§ 6) — la réponse d'erreur
  expose encore la stack trace Java complète dans le JSON. **À corriger avant
  l'étape 13** (fuite d'information en production).
- Pousser les commits vers GitHub (§ 9) — toujours la responsabilité de
  l'utilisatrice via GitHub Desktop.

### 10.2 Polish restant identifié lors de la dernière ronde de tests manuels
Tout le reste de la liste de retours a été traité cette session. Rien
d'identifié ne reste en attente à ce stade — si de nouveaux tests manuels
remontent d'autres points, les ajouter ici avant de les traiter.

### 10.3 Étape 13 — Docker & déploiement
D'après [docs/04-ROADMAP.md](docs/04-ROADMAP.md) : conteneuriser le backend et
le frontend (actuellement seul PostgreSQL tourne dans Docker), `docker-compose`
complet, variables d'environnement pour la prod (le secret JWT en dur dans
`application-dev.yml` ne doit pas partir tel quel en prod), et régler le point
du `GlobalExceptionHandler` avant d'exposer quoi que ce soit publiquement.

### 10.4 Étape 14 — Module IA
Pas encore spécifié en détail à ce stade — revoir `docs/04-ROADMAP.md` le
moment venu.

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

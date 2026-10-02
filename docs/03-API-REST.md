# 03 — Contrat d'API REST

> **Objectif** : figer la liste des endpoints **avant** de coder. Le frontend et
> le backend sont alors développables en parallèle, chacun sachant exactement ce
> que l'autre attend.

Base : `http://localhost:8080/api`
Documentation interactive (dès l'étape 2) : `http://localhost:8080/swagger-ui.html`

---

## 1. Conventions générales

### Authentification

Toutes les routes exigent un JWT, **sauf** `/auth/register` et `/auth/login`.

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Format d'erreur unique

Toutes les erreurs de l'API ont **exactement** cette forme — le frontend n'a donc
qu'un seul cas à traiter :

```json
{
  "timestamp": "2026-10-02T14:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "La validation a échoué",
  "path": "/api/tasks",
  "fieldErrors": [
    { "field": "title", "message": "doit contenir entre 3 et 200 caractères" }
  ]
}
```

`fieldErrors` n'est présent que sur les erreurs de validation (`400`).

### Pagination

Toutes les listes sont paginées.

**Paramètres** : `?page=0&size=20&sort=createdAt,desc`

**Réponse** :

```json
{
  "content": [ /* ... */ ],
  "page": 0,
  "size": 20,
  "totalElements": 137,
  "totalPages": 7,
  "first": true,
  "last": false
}
```

### Dates

Toujours en **ISO-8601 UTC** : `2026-10-02T14:30:00Z`.

---

## 2. Authentification — `/api/auth`

| Méthode | URL | Accès | Description |
|---|---|---|---|
| `POST` | `/auth/register` | public | Créer un compte |
| `POST` | `/auth/login` | public | Se connecter |
| `POST` | `/auth/refresh` | public | Renouveler le token d'accès |
| `POST` | `/auth/logout` | authentifié | Invalider le refresh token |
| `GET` | `/auth/me` | authentifié | Profil de l'utilisateur courant |

<details>
<summary><b>POST /auth/register</b></summary>

```json
// Requête
{
  "email": "hanen@smarttask.io",
  "password": "Password123",
  "firstName": "Hanen",
  "lastName": "Goubaa"
}
```
```json
// 201 Created
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": "6f1c...", "email": "hanen@smarttask.io",
    "firstName": "Hanen", "lastName": "Goubaa",
    "role": "MEMBER", "avatarUrl": null
  }
}
```
Erreurs : `400` validation · `409` email déjà utilisé
</details>

<details>
<summary><b>POST /auth/login</b></summary>

```json
// Requête
{ "email": "hanen@smarttask.io", "password": "Password123" }
```
Réponse : identique à `register`. Erreurs : `401` identifiants invalides ·
`403` compte désactivé
</details>

---

## 3. Utilisateurs — `/api/users`

| Méthode | URL | Accès | Description |
|---|---|---|---|
| `GET` | `/users` | `ADMIN` | Liste paginée, `?search=` sur nom/email |
| `GET` | `/users/{id}` | authentifié | Détail d'un utilisateur |
| `PUT` | `/users/me` | authentifié | Modifier son propre profil |
| `PUT` | `/users/me/password` | authentifié | Changer son mot de passe |
| `POST` | `/users/me/avatar` | authentifié | Téléverser un avatar (`multipart`) |
| `PUT` | `/users/{id}/role` | `ADMIN` | Changer le rôle d'un utilisateur |
| `PUT` | `/users/{id}/status` | `ADMIN` | Activer / désactiver un compte |

---

## 4. Projets — `/api/projects`

| Méthode | URL | Accès | Description |
|---|---|---|---|
| `GET` | `/projects` | authentifié | Mes projets — filtres `?status=&search=` |
| `GET` | `/projects/{id}` | membre | Détail + membres + compteurs |
| `POST` | `/projects` | `MANAGER`, `ADMIN` | Créer un projet |
| `PUT` | `/projects/{id}` | `OWNER`/`MANAGER` du projet | Modifier |
| `DELETE` | `/projects/{id}` | `OWNER`, `ADMIN` | Supprimer (cascade) |
| `GET` | `/projects/{id}/members` | membre | Liste des membres |
| `POST` | `/projects/{id}/members` | `OWNER`/`MANAGER` | Ajouter un membre |
| `PUT` | `/projects/{id}/members/{userId}` | `OWNER` | Changer le rôle d'un membre |
| `DELETE` | `/projects/{id}/members/{userId}` | `OWNER`/`MANAGER` | Retirer un membre |
| `GET` | `/projects/{id}/stats` | membre | Statistiques du projet |

<details>
<summary><b>POST /projects</b></summary>

```json
// Requête
{
  "name": "Refonte du site vitrine",
  "description": "Migration vers Angular",
  "status": "PLANNING",
  "color": "#4F46E5",
  "startDate": "2026-10-06",
  "endDate": "2026-12-20"
}
```
```json
// 201 Created
{
  "id": "a3f2...",
  "name": "Refonte du site vitrine",
  "description": "Migration vers Angular",
  "status": "PLANNING",
  "color": "#4F46E5",
  "startDate": "2026-10-06",
  "endDate": "2026-12-20",
  "owner": { "id": "6f1c...", "firstName": "Hanen", "lastName": "Goubaa" },
  "memberCount": 1,
  "taskCount": 0,
  "createdAt": "2026-10-02T14:30:00Z"
}
```
</details>

---

## 5. Tâches — `/api/tasks`

| Méthode | URL | Accès | Description |
|---|---|---|---|
| `GET` | `/tasks` | authentifié | Recherche multi-critères (voir ci-dessous) |
| `GET` | `/tasks/{id}` | membre du projet | Détail d'une tâche |
| `POST` | `/tasks` | `CONTRIBUTOR`+ | Créer une tâche |
| `PUT` | `/tasks/{id}` | voir règle R11 | Modifier |
| `PATCH` | `/tasks/{id}/status` | assignee, `MANAGER` | Changer le statut (drag & drop kanban) |
| `PATCH` | `/tasks/{id}/assignee` | `MANAGER`+ | (Ré)assigner |
| `DELETE` | `/tasks/{id}` | reporter, `MANAGER`+ | Supprimer |
| `GET` | `/tasks/my` | authentifié | Mes tâches, tous projets confondus |
| `GET` | `/projects/{id}/tasks` | membre | Tâches d'un projet |
| `GET` | `/projects/{id}/board` | membre | Tâches groupées par statut (kanban) |

### Paramètres de recherche de `GET /tasks`

| Paramètre | Type | Exemple |
|---|---|---|
| `search` | texte libre sur titre + description | `search=login` |
| `projectId` | UUID | |
| `assigneeId` | UUID, ou `me`, ou `unassigned` | `assigneeId=me` |
| `status` | liste | `status=TODO,IN_PROGRESS` |
| `priority` | liste | `priority=HIGH,URGENT` |
| `dueBefore` / `dueAfter` | date ISO | `dueBefore=2026-10-31` |
| `overdue` | booléen | `overdue=true` |
| `page` / `size` / `sort` | pagination | `sort=priority,desc` |

Exemple complet :

```http
GET /api/tasks?assigneeId=me&status=TODO,IN_PROGRESS&priority=HIGH,URGENT&sort=dueDate,asc&page=0&size=20
```

> 💡 Côté backend, ce type de filtrage combinatoire se traite avec les
> **JPA Specifications** : chaque critère devient un petit prédicat, et on les
> combine. L'alternative (une méthode de repository par combinaison) donnerait
> des dizaines de méthodes ingérables.

<details>
<summary><b>POST /tasks</b></summary>

```json
// Requête
{
  "title": "Implémenter la page de login",
  "description": "Formulaire réactif + appel à /auth/login",
  "projectId": "a3f2...",
  "assigneeId": "6f1c...",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-10-15T17:00:00Z"
}
```
```json
// 201 Created
{
  "id": "b7d4...",
  "title": "Implémenter la page de login",
  "description": "Formulaire réactif + appel à /auth/login",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-10-15T17:00:00Z",
  "project": { "id": "a3f2...", "name": "Refonte du site vitrine", "color": "#4F46E5" },
  "assignee": { "id": "6f1c...", "firstName": "Hanen", "lastName": "Goubaa", "avatarUrl": null },
  "reporter": { "id": "6f1c...", "firstName": "Hanen", "lastName": "Goubaa", "avatarUrl": null },
  "commentCount": 0,
  "attachmentCount": 0,
  "completedAt": null,
  "createdAt": "2026-10-02T14:35:00Z"
}
```
Erreurs : `400` validation · `403` pas membre du projet ·
`404` projet ou assigné inexistant · `409` l'assigné n'est pas membre du projet (R9)
</details>

---

## 6. Commentaires — `/api/tasks/{taskId}/comments`

| Méthode | URL | Accès | Description |
|---|---|---|---|
| `GET` | `/tasks/{taskId}/comments` | membre | Liste paginée, ordre chronologique |
| `POST` | `/tasks/{taskId}/comments` | membre | Ajouter un commentaire |
| `PUT` | `/comments/{id}` | auteur, `ADMIN` | Modifier |
| `DELETE` | `/comments/{id}` | auteur, `ADMIN` | Supprimer |

---

## 7. Fichiers joints — `/api/tasks/{taskId}/attachments`

| Méthode | URL | Accès | Description |
|---|---|---|---|
| `GET` | `/tasks/{taskId}/attachments` | membre | Liste des fichiers |
| `POST` | `/tasks/{taskId}/attachments` | membre | Téléverser (`multipart/form-data`) |
| `GET` | `/attachments/{id}/download` | membre | Télécharger |
| `DELETE` | `/attachments/{id}` | uploader, `MANAGER`+ | Supprimer |

Contraintes : **10 Mo** max · types acceptés : `image/*`, `application/pdf`,
documents Office, `application/zip`.
Erreurs : `413 Payload Too Large` · `415 Unsupported Media Type`

---

## 8. Notifications — `/api/notifications`

| Méthode | URL | Accès | Description |
|---|---|---|---|
| `GET` | `/notifications` | authentifié | Mes notifications — `?isRead=false` |
| `GET` | `/notifications/unread-count` | authentifié | `{ "count": 7 }` |
| `PATCH` | `/notifications/{id}/read` | destinataire | Marquer comme lue |
| `PATCH` | `/notifications/read-all` | authentifié | Tout marquer comme lu |
| `DELETE` | `/notifications/{id}` | destinataire | Supprimer |

---

## 9. Dashboard — `/api/dashboard`

| Méthode | URL | Accès | Description |
|---|---|---|---|
| `GET` | `/dashboard/summary` | authentifié | Indicateurs clés |
| `GET` | `/dashboard/tasks-by-status` | authentifié | Données du donut |
| `GET` | `/dashboard/tasks-by-priority` | authentifié | Données du bar chart |
| `GET` | `/dashboard/activity` | authentifié | Activité récente |
| `GET` | `/dashboard/workload` | `MANAGER`+ | Charge par membre |

<details>
<summary><b>GET /dashboard/summary</b></summary>

```json
{
  "totalProjects": 4,
  "activeProjects": 3,
  "totalTasks": 137,
  "myOpenTasks": 12,
  "myOverdueTasks": 2,
  "tasksCompletedThisWeek": 9,
  "completionRate": 0.68
}
```
</details>

---

## 10. Module IA — `/api/ai` *(étape 14)*

| Méthode | URL | Description |
|---|---|---|
| `POST` | `/ai/tasks/suggest` | À partir d'une description de projet, proposer une liste de tâches |
| `POST` | `/ai/tasks/{id}/summarize` | Résumer une tâche et son fil de commentaires |
| `POST` | `/ai/tasks/{id}/estimate-priority` | Suggérer une priorité |

---

## 11. Récapitulatif

| Domaine | Endpoints | Étape de réalisation |
|---|---|---|
| Authentification | 5 | 4 |
| Utilisateurs | 7 | 5 |
| Projets | 10 | 6 |
| Tâches | 10 | 7 |
| Commentaires | 4 | 9 |
| Fichiers joints | 4 | 10 |
| Notifications | 5 | 9 |
| Dashboard | 5 | 11 |
| IA | 3 | 14 |
| **Total** | **53** | |

---

## 12. Étape suivante

→ [`04-ROADMAP.md`](04-ROADMAP.md) : le plan des 14 étapes.

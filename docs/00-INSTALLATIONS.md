# 00 — Environnement de développement : installations et configuration

> **Objectif de ce document** : lister *exactement* ce qui doit être installé sur ton PC
> pour développer SmartTask, dire ce que tu as déjà, ce qui manque, et comment
> l'installer et le vérifier.
>
> Plateforme de référence : **Windows 10 Pro (x64)**, shell **PowerShell**.

---

## 1. Tableau de bord : état de ton PC

Relevé effectué le **2026-10-02** sur ta machine.

| # | Outil | Rôle dans le projet | État | Version détectée |
|---|---|---|---|---|
| 1 | **JDK (Java)** | Compiler et exécuter le backend Spring Boot | ✅ Installé | `17.0.12` (Oracle JDK) |
| 2 | **Node.js** | Exécuter les outils Angular (le CLI, le serveur de dev) | ✅ Installé | `24.13.1` |
| 3 | **npm** | Installer les dépendances JavaScript | ✅ Installé | `11.8.0` |
| 4 | **Git** | Versionner le code | ✅ Installé | `2.49.0` |
| 5 | **Docker Desktop** | Faire tourner PostgreSQL, puis conteneuriser l'app | ✅ Installé | `28.1.1` + Compose `v2.35.1` |
| 6 | **PostgreSQL** | Base de données | ✅ Installé (local) | `16.13` |
| 7 | **VS Code** | Éditeur de code | ✅ Installé | — |
| 8 | **Angular CLI** | Générer et builder le frontend | ❌ **À installer** | — |
| 9 | **Maven** | Builder le backend | ⚠️ **Pas nécessaire** — on utilise le *wrapper* | — |
| 10 | **`JAVA_HOME`** | Variable d'environnement attendue par les outils Java | ❌ **À configurer** | non défini |

**Conclusion : il ne te reste que 2 choses à faire** → installer Angular CLI (§ 3)
et définir `JAVA_HOME` (§ 2.2). Tout le reste est prêt.

---

## 2. Java (JDK 17) — déjà installé, à configurer

### 2.1 Pourquoi Java 17 ?

Spring Boot 4.1 exige **Java 17 minimum**. Ton JDK 17.0.12 est donc suffisant et
on ne touche à rien. *(Java 21 ou 25 apporteraient les « virtual threads » et
quelques améliorations de syntaxe, mais ce n'est pas nécessaire pour apprendre —
on reste sur 17 pour éviter une installation inutile.)*

> ℹ️ Tu as aussi un vieux `jdk1.8.0_202` sur la machine. On ne s'en sert pas,
> mais c'est précisément pour ça qu'il faut définir `JAVA_HOME` explicitement :
> pour qu'aucun outil ne choisisse Java 8 par erreur.

### 2.2 Définir `JAVA_HOME` (obligatoire)

Ouvre **PowerShell** et exécute :

```powershell
# Définition permanente, au niveau de ton compte utilisateur
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "User")
```

Puis **ferme et rouvre ton terminal** (et VS Code), et vérifie :

```powershell
$env:JAVA_HOME          # doit afficher : C:\Program Files\Java\jdk-17
java -version           # doit afficher : 17.0.12
javac -version          # doit afficher : javac 17.0.12
```

> `javac` (le compilateur) doit répondre. S'il répond, tu as bien un **JDK** et
> pas seulement un **JRE**. ✅ C'est déjà ton cas.

---

## 3. Angular CLI — à installer

### 3.1 Pourquoi ?

Le **CLI** (Command Line Interface) d'Angular est l'outil qui génère le projet,
crée les composants/services, lance le serveur de développement et compile pour
la production. C'est le point d'entrée de tout le travail frontend.

### 3.2 Installation

```powershell
npm install -g @angular/cli
```

### 3.3 Vérification

```powershell
ng version
```

Tu dois voir **Angular CLI 22.x** et **Node 24.13.1**.

> ✅ Compatibilité confirmée : Angular 22 accepte Node 20.19+, 22.12+ ou 24.x.
> Ton Node 24.13.1 convient.

### 3.4 Si `ng` n'est pas reconnu après installation

Le dossier des binaires npm globaux n'est pas dans ton `PATH`. Trouve-le puis ajoute-le :

```powershell
npm config get prefix                       # ex: C:\Users\Hanen GB\AppData\Roaming\npm
$p = npm config get prefix
[Environment]::SetEnvironmentVariable("PATH", "$env:PATH;$p", "User")
```

Rouvre le terminal.

---

## 4. Maven — volontairement **non installé**

### 4.1 Pourquoi on ne l'installe pas

Maven est l'outil qui télécharge les dépendances Java et construit le `.jar`.
Mais Spring Initializr génère dans le projet un **Maven Wrapper** : deux fichiers
`mvnw` (Linux/macOS) et `mvnw.cmd` (Windows) qui **téléchargent et utilisent
automatiquement la bonne version de Maven**.

Avantages :

- aucune installation système à faire ;
- **tout le monde build avec exactement la même version de Maven** (toi
  aujourd'hui, toi dans six mois, le serveur de CI, un collègue) ;
- c'est la pratique standard sur les projets Spring Boot professionnels.

### 4.2 Comment on l'utilisera

À partir de l'étape 2, dans le dossier `backend/` :

```powershell
.\mvnw clean install        # compiler + tester + packager
.\mvnw spring-boot:run      # lancer l'application
.\mvnw test                 # lancer uniquement les tests
```

> ⚠️ Toujours `.\mvnw` (avec `.\`) sous PowerShell, jamais `mvn`.

### 4.3 Si tu veux quand même Maven en global (optionnel)

```powershell
winget install Apache.Maven
```

Ce n'est **pas requis** pour ce projet.

---

## 5. PostgreSQL — deux installations, un piège à éviter

### 5.1 La situation

Tu as **PostgreSQL 16.13 installé directement sur Windows**. Il écoute sur le
port **5432** (le port par défaut). Or on va aussi lancer un PostgreSQL **dans
Docker**, pour les raisons expliquées ci-dessous.

Deux serveurs ne peuvent pas écouter sur le même port. Donc :

| Installation | Port | Usage dans ce projet |
|---|---|---|
| PostgreSQL local (Windows) | `5432` | **On n'y touche pas.** Laissé tel quel. |
| PostgreSQL Docker (SmartTask) | **`5433`** | ✅ **C'est celui qu'on utilise** |

Le fichier [`docker-compose.yml`](../docker-compose.yml) fait la correspondance
`5433` (ton PC) → `5432` (dans le conteneur).

### 5.2 Pourquoi Docker plutôt que le PostgreSQL local ?

1. **Isolation** : la base de SmartTask ne pollue pas tes autres projets, et
   inversement.
2. **Reproductibilité** : la version, les identifiants et la configuration sont
   écrits dans `docker-compose.yml`, versionnés dans git.
3. **Réinitialisation en 10 secondes** : `docker compose down -v` efface tout et
   on repart d'une base propre — très pratique quand on apprend et qu'on casse
   des choses.
4. **C'est ce qu'on fera en production** (étapes 12-13), donc autant s'y habituer.

### 5.3 Démarrer la base

Lance d'abord **Docker Desktop** (il n'est pas démarré actuellement), puis :

```powershell
cd "C:\Users\Hanen GB\Desktop\smarttask"
docker compose up -d
```

### 5.4 Vérifier

```powershell
docker compose ps                 # les 2 conteneurs doivent être "running"/"healthy"
docker compose logs -f postgres   # Ctrl+C pour quitter
```

Test de connexion avec le client `psql` que tu as déjà :

```powershell
psql -h localhost -p 5433 -U smarttask -d smarttask -c "SELECT version();"
# mot de passe : smarttask
```

Ou via l'interface graphique **pgAdmin** : http://localhost:5050
(`admin@smarttask.local` / `admin`).

> Dans pgAdmin, pour créer la connexion au serveur, utilise comme *Host* le nom
> `postgres` (le nom du service Docker) et le port `5432` — **pas** `localhost:5433`.
> Raison : pgAdmin tourne lui aussi dans Docker, il voit le réseau interne.

### 5.5 Commandes utiles

```powershell
docker compose up -d        # démarrer
docker compose stop         # arrêter (les données sont conservées)
docker compose down         # arrêter et supprimer les conteneurs (données conservées)
docker compose down -v      # ⚠️ TOUT supprimer, y compris les données
```

---

## 6. Docker Desktop

Installé en version 28.1.1, mais **non démarré** au moment du relevé.

- Lance-le depuis le menu Démarrer → *Docker Desktop*.
- Pour éviter de devoir y penser à chaque fois :
  *Settings → General → ✅ Start Docker Desktop when you sign in*.

Vérification :

```powershell
docker info --format "{{.ServerVersion}}"      # doit afficher 28.1.1
docker compose version
```

> Docker Desktop sous Windows a besoin de **WSL 2**. Comme Docker fonctionne déjà
> chez toi, c'est que WSL 2 est en place. Rien à faire.

---

## 7. VS Code — extensions recommandées

Déjà installé. Ajoute ces extensions (elles font 90 % du confort de travail) :

### Pour le backend Java / Spring

| Extension | ID | À quoi ça sert |
|---|---|---|
| Extension Pack for Java | `vscjava.vscode-java-pack` | Complétion, navigation, debug, refactoring Java |
| Spring Boot Extension Pack | `vmware.vscode-boot-dev-pack` | Autocomplétion des `application.yml`, dashboard de lancement |

### Pour le frontend Angular

| Extension | ID | À quoi ça sert |
|---|---|---|
| Angular Language Service | `Angular.ng-template` | Complétion et erreurs **dans les templates HTML** |
| ESLint | `dbaeumer.vscode-eslint` | Détection des erreurs de style/qualité TS |
| Prettier | `esbenp.prettier-vscode` | Formatage automatique du code |

### Transverses

| Extension | ID | À quoi ça sert |
|---|---|---|
| Docker | `ms-azuretools.vscode-docker` | Gérer conteneurs et images depuis l'éditeur |
| REST Client | `humao.rest-client` | Tester l'API depuis des fichiers `.http` (alternative à Postman) |

Installation en une commande :

```powershell
code --install-extension vscjava.vscode-java-pack
code --install-extension vmware.vscode-boot-dev-pack
code --install-extension Angular.ng-template
code --install-extension dbaeumer.vscode-eslint
code --install-extension esbenp.prettier-vscode
code --install-extension ms-azuretools.vscode-docker
code --install-extension humao.rest-client
```

---

## 8. Outils optionnels

| Outil | Utilité | Commande d'installation |
|---|---|---|
| **Postman** | Tester l'API avec une interface graphique | `winget install Postman.Postman` |
| **IntelliJ IDEA Community** | IDE Java bien plus confortable que VS Code pour Spring | `winget install JetBrains.IntelliJIDEA.Community` |
| **DBeaver** | Client SQL universel, alternative à pgAdmin | `winget install dbeaver.dbeaver` |
| **Node Version Manager (nvm-windows)** | Jongler entre plusieurs versions de Node | `winget install CoreyButler.NVMforWindows` |

> 💡 Si tu trouves le développement Spring Boot pénible dans VS Code,
> **IntelliJ IDEA Community est gratuit** et change vraiment l'expérience
> (navigation, complétion, exécution des tests). Beaucoup de développeurs Java
> font : IntelliJ pour le backend + VS Code pour le frontend.

---

## 9. Vérification complète de l'environnement

Un script fait tout le contrôle d'un coup :

```powershell
cd "C:\Users\Hanen GB\Desktop\smarttask"
.\scripts\check-env.ps1
```

Il vérifie chaque outil, chaque version, la variable `JAVA_HOME`, et te dit
précisément ce qui manque. **Lance-le avant de passer à l'étape 2.**

> Si PowerShell refuse d'exécuter le script (« exécution de scripts désactivée ») :
> ```powershell
> Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
> ```

---

## 10. Récapitulatif : ta liste d'actions

```powershell
# 1) Définir JAVA_HOME
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "User")

# 2) Fermer puis rouvrir le terminal, ensuite :
npm install -g @angular/cli

# 3) Lancer Docker Desktop (menu Démarrer), puis :
cd "C:\Users\Hanen GB\Desktop\smarttask"
docker compose up -d

# 4) Installer les extensions VS Code (§ 7)

# 5) Contrôle final
.\scripts\check-env.ps1
```

Quand `check-env.ps1` affiche tout en vert → **on passe à l'étape 2**
(création du backend Spring Boot).

---

## 11. Ports utilisés par le projet

Garde cette liste sous la main : la quasi-totalité des problèmes de démarrage
vient d'un port déjà occupé.

| Port | Service | Remarque |
|---|---|---|
| `4200` | Serveur de dev Angular | |
| `8080` | API Spring Boot | |
| `5432` | PostgreSQL **local Windows** | existant, non utilisé par SmartTask |
| `5433` | PostgreSQL **Docker SmartTask** | ⬅ celui du projet |
| `5050` | pgAdmin | |

Pour savoir qui occupe un port :

```powershell
Get-NetTCPConnection -LocalPort 8080 -State Listen |
  Select-Object LocalPort, OwningProcess,
    @{N='Process';E={(Get-Process -Id $_.OwningProcess).ProcessName}}
```

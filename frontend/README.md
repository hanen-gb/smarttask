# frontend/ — SPA Angular

> 📌 **Dossier volontairement vide pour l'instant.** Il sera rempli à l'**étape 3**.

## Ce qui sera généré ici

```powershell
# Depuis la racine du projet (smarttask/)
ng new frontend --style=scss --ssr=false --routing
```

| Option | Pourquoi |
|---|---|
| `--style=scss` | Variables, imbrication et mixins — indispensable pour un thème |
| `--ssr=false` | Pas de rendu serveur : l'app est servie en statique derrière Nginx |
| `--routing` | Génère le fichier de routes (navigation entre écrans) |

> Angular 22 génère par défaut des **composants standalone** (sans `NgModule`),
> ce qui correspond à l'architecture décrite dans la documentation.

## Commandes (une fois le projet généré)

```powershell
npm start                       # serveur de dev -> http://localhost:4200
npm run build                   # build de production dans dist/
npm test                        # tests unitaires
ng generate component features/tasks/task-list    # générer un composant
ng generate service core/services/task            # générer un service
```

Raccourci : `ng g c` = `ng generate component`, `ng g s` = `ng generate service`.

## Structure cible

Voir [`../docs/01-ARCHITECTURE.md`](../docs/01-ARCHITECTURE.md) § 4.2.

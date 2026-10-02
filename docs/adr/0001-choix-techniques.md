# ADR 0001 — Choix techniques de SmartTask

- **Statut** : accepté
- **Date** : 2026-10-02
- **Contexte** : projet d'apprentissage full-stack Angular + Spring Boot

> **Qu'est-ce qu'un ADR ?** *Architecture Decision Record* : une fiche courte qui
> note une décision d'architecture et **la raison** de ce choix. Six mois plus
> tard, on se souvient du *quoi* mais jamais du *pourquoi*. C'est une pratique
> courante en entreprise, et un bon réflexe à prendre.

---

## 1. Spring Boot 4.1.1 et non 3.x

**Décision** : Spring Boot `4.1.1.RELEASE`.

**Pourquoi** : au 2026-10-02, `start.spring.io` ne propose plus aucune version
3.x — la branche 3 n'est plus en support OSS. Apprendre sur une version non
supportée serait un mauvais investissement.

**Conséquence à connaître** : beaucoup de tutoriels en ligne ont été écrits pour
Spring Boot 3. Les différences portent surtout sur la configuration de Spring
Security et quelques réorganisations de packages. Si un bout de code copié d'un
tutoriel ne compile pas, c'est la première piste à vérifier — et le réflexe à
prendre est de consulter la documentation officielle plutôt que des blogs.

## 2. Java 17 et non 21 / 25

**Décision** : Java 17 (déjà installé sur la machine).

**Pourquoi** : c'est le minimum requis par Spring Boot 4.1 et c'est suffisant
pour tout ce que fait le projet. Éviter une installation supplémentaire réduit la
surface de problèmes au démarrage.

**Alternative écartée** : Java 21 (LTS) apporte les *virtual threads* et le
*pattern matching* plus complet. Intéressant, mais hors sujet pour apprendre les
fondamentaux. Migration possible plus tard en changeant une ligne du `pom.xml`.

## 3. Maven et non Gradle

**Décision** : Maven, via le *wrapper* `mvnw`.

**Pourquoi** :

| Critère | Maven | Gradle |
|---|---|---|
| Documentation Spring Boot | majoritaire | présente |
| Lisibilité pour un débutant | XML verbeux mais **explicite** | DSL concis mais « magique » |
| Vitesse de build | correcte | plus rapide |
| Courbe d'apprentissage | douce | plus raide |

On apprend deux frameworks à la fois : le build doit être le poste qui pose le
moins de questions. Maven gagne.

## 4. PostgreSQL et non MySQL ni H2

**Décision** : PostgreSQL 16, dans Docker.

**Pourquoi** :
- fonctionnalités avancées utiles au projet (type `UUID` natif, `JSONB`,
  recherche plein texte pour l'étape 8) ;
- déjà installé sur la machine, et disponible chez tous les hébergeurs ;
- standard de fait sur les projets Spring Boot récents.

**Alternative écartée — H2 en mémoire** : très pratique au démarrage, mais son
SQL diffère de PostgreSQL. On développerait contre une base qui n'est pas celle
de production : les bugs apparaîtraient au déploiement. **On développe sur la
même base qu'en production.**

## 5. Flyway et non `ddl-auto: update`

**Décision** : migrations SQL versionnées avec Flyway, et
`spring.jpa.hibernate.ddl-auto: validate`.

**Pourquoi** : `ddl-auto: update` ne sait pas supprimer une colonne, ni en
renommer une, ni migrer des données existantes. Le schéma devient non
reproductible, et une mauvaise configuration en production peut détruire des
tables. Avec Flyway, le schéma est du code : versionné, relu, rejouable à
l'identique. `validate` sert de filet : l'application refuse de démarrer si les
entités Java et le schéma SQL divergent.

**Coût accepté** : écrire le SQL à la main. C'est aussi une compétence à acquérir.

## 6. JWT *stateless* et non sessions serveur

**Décision** : authentification par JWT (access token court + refresh token).

**Pourquoi** : le frontend est une SPA sur une autre origine que l'API. Les
sessions avec cookie `JSESSIONID` fonctionnent mais compliquent le CORS, le
partage entre plusieurs instances, et l'ajout futur d'un client mobile. Un
backend sans état se réplique à l'identique derrière un load balancer.

**Inconvénient assumé** : on ne peut pas révoquer un access token déjà émis. On
le compense par une **durée de vie courte** (15 minutes) et un refresh token
stocké en base, lui-même révocable.

## 7. Angular standalone + signals, sans librairie d'état

**Décision** : composants *standalone* (sans `NgModule`), état géré par les
*signals* d'Angular. Pas de NgRx.

**Pourquoi** : c'est l'approche recommandée par Angular aujourd'hui, et elle est
bien plus simple à apprendre. NgRx ajoute actions, reducers, effects et selectors
— beaucoup de cérémonie pour une application de cette taille, et un deuxième
paradigme à digérer en même temps qu'Angular lui-même.

**Quand reconsidérer** : si l'état partagé entre features devient réellement
complexe. Ce ne sera pas le cas ici.

## 8. Pas de librairie de composants UI imposée

**Décision** : SCSS maison, avec des variables CSS.

**Pourquoi** : Angular Material ou PrimeNG donnent une belle interface en 10
minutes, mais on apprend alors *leur* API plutôt qu'Angular, le CSS et
l'accessibilité. Pour un projet d'apprentissage, écrire ses propres composants
(badge, modale, carte) a plus de valeur.

**Révisable** : si le temps passé sur le CSS devient un frein à l'avancement, on
pourra introduire Angular Material à partir de l'étape 6.

## 9. `package-by-feature` et non `package-by-layer`

**Décision** : regrouper le code par domaine fonctionnel (`task/`, `project/`…)
plutôt que par couche technique (`controller/`, `service/`…).

**Pourquoi** : pour travailler sur les tâches, on ouvre **un** dossier au lieu de
naviguer entre cinq. La cohésion est forte à l'intérieur d'un package, le
couplage faible entre packages. C'est aussi un premier pas vers un éventuel
découpage en microservices.

**Note** : beaucoup de tutoriels utilisent `package-by-layer`. Ce n'est pas faux,
c'est juste moins bien à mesure que le projet grossit.

## 10. UUID et non `BIGINT` auto-incrémenté

**Décision** : clés primaires en UUID.

**Pourquoi** : des identifiants séquentiels sont devinables (`/api/users/1`,
`/api/users/2`…) — c'est une fuite d'information et une invitation à
l'énumération. Les UUID peuvent aussi être générés côté application avant
l'insertion.

**Inconvénients assumés** : 16 octets contre 8, index un peu plus gros, moins
lisible en debug. Négligeable à l'échelle du projet.

## 11. DTO systématiques, jamais d'entité exposée

**Décision** : l'API n'échange que des DTO ; les entités JPA ne sortent jamais du
backend.

**Pourquoi** : éviter de fuiter des champs sensibles (hash de mot de passe),
éviter les récursions infinies à la sérialisation, et découpler le contrat d'API
du schéma de base.

**Coût accepté** : du code de mapping. Écrit à la main au début (c'est
pédagogique), éventuellement généré par MapStruct ensuite.

## 12. Docker dès le début, pas seulement à la fin

**Décision** : PostgreSQL en conteneur dès l'étape 1 ; conteneurisation complète
à l'étape 13.

**Pourquoi** : un `docker compose down -v` suffit à repartir d'une base propre.
Quand on apprend, on casse souvent sa base — autant que ce soit indolore. Et on
se familiarise progressivement avec Docker au lieu de le découvrir en bloc le
jour du déploiement.

---

## Décisions reportées

| Sujet | Pourquoi plus tard |
|---|---|
| WebSocket pour les notifications | polling d'abord (plus simple) ; WebSocket en option à l'étape 9 |
| Stockage S3 des fichiers | interface `FileStorageService` prête ; implémentation locale suffit en dev |
| Versionnement de l'API (`/v1`) | aucun client externe pour l'instant |
| Cache Redis | aucun problème de performance mesuré — n'optimisons pas à l'aveugle |
| MapStruct pour le mapping | mapping manuel d'abord, pour comprendre ce qui est généré |
| Internationalisation (i18n) | français uniquement au départ |

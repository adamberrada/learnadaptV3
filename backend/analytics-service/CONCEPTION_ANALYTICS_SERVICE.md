# Conception du Microservice `analytics-service`

## 1. Contexte et objectif
Le microservice `analytics-service` est un composant autonome de la plateforme E-learning adaptive et collaborative.  
Son role est de centraliser les donnees de suivi pedagogique, de calculer des indicateurs (KPIs) et d'exposer des dashboards personnalises pour trois profils :
- Apprenant
- Enseignant
- Administrateur

L'objectif principal est de transformer des evenements metier (inscription, progression, quiz) en informations decisionnelles exploitables.

## 2. Objectifs fonctionnels
Le service doit permettre :
- la consultation d'un dashboard apprenant (temps passe, progression, engagement),
- la consultation d'un dashboard enseignant (revenus, inscriptions, performance quiz, etudiants a risque),
- la consultation d'un dashboard administrateur (KPIs globaux plateforme),
- l'ingestion synchrone d'evenements inter-services pour mettre a jour les metriques.

## 3. Exigences non fonctionnelles
- **Autonomie** : service deployable independamment.
- **Securite** : authentification stateless via headers standardises.
- **Controle d'acces** : RBAC strict selon le role.
- **Simplicite de dev/test** : base H2 en memoire.
- **Maintenabilite** : architecture en couches avec separation claire des responsabilites.

## 4. Stack technique et justification
- **Java 21** : version LTS moderne, robuste pour backend microservices.
- **Spring Boot 4.0.6** : standardisation et acceleration du developpement.
- **Spring Web MVC** : exposition APIs REST.
- **Spring Data JPA** : acces donnees et agregations simples.
- **Spring Security** : controle d'acces et securisation endpoints.
- **Jakarta Validation** : validation des payloads entrants.
- **H2 in-memory** : simulation rapide et reproductible.
- **Lombok** : reduction du code boilerplate.

## 5. Architecture logicielle
Package racine : `com.anouar.elearning.analytics`

- `config` : configuration securite (`SecurityConfig`).
- `controller` : endpoints REST par acteur + ingestion interne.
- `dto` : objets d'echange API et formats dashboard.
- `entity` : modeles JPA de persistance analytique.
- `exception` : exceptions metier et handler global.
- `repository` : interfaces JPA de requetage.
- `service` : logique d'agregation et calcul KPI.

Cette organisation garantit :
- une bonne lisibilite,
- une evolution simple par fonctionnalite,
- une testabilite par couche.

## 6. Securite stateless et RBAC
Le service n'utilise pas de session serveur.  
Chaque requete doit fournir :
- `X-User-Id`
- `X-User-Role`

Un filtre d'authentification (`HeaderAuthenticationFilter`) injecte l'identite dans le `SecurityContext`.

Regles RBAC appliquees :
- `/api/analytics/learner/**` -> `ROLE_LEARNER`
- `/api/analytics/teacher/**` -> `ROLE_TEACHER`
- `/api/analytics/admin/**` -> `ROLE_ADMIN`
- `/api/internal/analytics/**` -> requiert utilisateur authentifie

## 7. Modele de donnees

### 7.1 `CourseMetric`
Metriques agreges par cours :
- `courseId`, `teacherId`, `title`
- `totalEnrollments`
- `completionRate`
- `averageRating`
- `totalRevenue`
- `updatedAt`

**Choix de conception** : ajout de `teacherId` pour rendre possible le filtrage natif des cours d'un enseignant dans son dashboard.

### 7.2 `LearnerDailyActivity`
Activite journaliere par apprenant :
- `learnerId`
- `activityDate`
- `timeSpentInMinutes`
- `lessonsCompletedCount`
- `quizTakenCount`

### 7.3 `QuizPerformanceSummary`
Synthese par quiz :
- `quizId`, `courseId`
- `totalAttempts`
- `globalSuccessRate`
- `hardestQuestionId`

## 8. Logique metier des dashboards

## 8.1 Dashboard Apprenant
Algorithmes appliques :
- somme du `timeSpentInMinutes`,
- somme des lecons completees,
- extraction des activites recentes de quiz,
- calcul d'engagement : `lessonsCompleted / joursActifs`.

Sortie : `LearnerDashboardDTO`.

## 8.2 Dashboard Enseignant
Algorithmes appliques :
- filtrage des `CourseMetric` par `teacherId`,
- calcul des revenus cumules et inscriptions cumulees,
- determination du cours le plus populaire (max inscriptions),
- moyenne des taux de reussite quiz de ses cours,
- detection des apprenants en decrochage : derniere activite > 14 jours.

Sortie : `TeacherDashboardDTO`.

## 8.3 Dashboard Administrateur
KPIs globaux :
- utilisateurs actifs (fenetre 30 jours),
- volume global des inscriptions,
- revenu total plateforme,
- top 5 categories (simule dans cette version),
- courbe d'activite sur 7 jours,
- taux d'engagement general.

Sortie : `GlobalAnalyticsDTO`.

## 9. Ingestion inter-services (simulation)
Le service expose des endpoints internes :
- `POST /api/internal/analytics/events/enrollment`
- `POST /api/internal/analytics/events/lesson-complete`
- `POST /api/internal/analytics/events/quiz-submit`

Effets metier :
- incrementation des inscriptions et revenus,
- mise a jour de l'activite journaliere apprenant,
- recalcul incremental du taux de reussite global quiz.

## 10. Contrats REST
Tous les endpoints repondent avec le format standard :

```json
{
  "success": true,
  "message": "....",
  "data": { }
}
```

Les erreurs de headers manquants, droits insuffisants, ou ressources introuvables sont gerees via `GlobalExceptionHandler`.

## 11. Gestion des erreurs
Exceptions metier principales :
- `UnauthorizedException` : headers d'authentification absents/invalides.
- `NotFoundException` : donnee analytique non trouvee (ex: quiz).

Le handler global garantit des reponses JSON homogenes pour simplifier les integrations front/back.

## 12. Decisions de conception importantes
1. **Architecture orientee role** (controllers separes learner/teacher/admin) pour clarte fonctionnelle.
2. **Service unique d'agregation** (`AnalyticsService`) pour centraliser les regles KPI.
3. **Schema analytique denormalise** (entites de synthese) pour requetes rapides dashboard.
4. **Stateless security** pour compatibilite microservices et scalabilite horizontale.

## 13. Limites actuelles
- Absence de vrai moteur Big Data/stream (Kafka, Spark, Flink).
- Certaines donnees (ex: top categories) sont simulees.
- Pas encore de cache ni pagination sur certains endpoints.

## 14. Evolutions recommandees (version future)
- Integration event-driven via broker (Kafka/RabbitMQ).
- Historisation temporelle plus fine (time-series DB).
- Ajout de tests d'integration API securises par role.
- Ajout d'un endpoint de sante analytics et metrics Prometheus.
- Mecanismes anti-donnees incoherentes (idempotence events + deduplication).

## 15. Conclusion
La conception actuelle fournit une base solide, modulaire et securisee pour un service analytique e-learning.  
Elle respecte le besoin metier de dashboards multi-acteurs, tout en restant evolutive vers une architecture de traitement massif en temps reel.

---

**Version du document** : 1.0  
**Date** : 2026-05-25  
**Service** : `analytics-service`

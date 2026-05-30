# Fichier de Conception - Quiz Service

## 1. Objectif
Le `quiz-service` gère la creation, la publication, la passation et la correction des quiz pour la plateforme e-learning.

Fonctions principales :
- creer et modifier des quiz
- gerer les questions et les choix de reponse
- lancer une tentative (attempt) par apprenant
- corriger automatiquement
- calculer score, reussite, progression
- exposer les resultats aux autres services

## 2. Perimetre fonctionnel
Inclus :
- CRUD Quiz
- CRUD Questions
- Gestion Tentatives
- Evaluation automatique
- Historique des scores

Exclus (hors service) :
- authentification (geree par `auth-service`)
- paiement/certificat (autres services)
- envoi notifications (gere par `notification-service`)

## 3. Acteurs
- **Formateur** : cree, edite et publie un quiz.
- **Apprenant** : passe un quiz et consulte son resultat.
- **Admin** : modere et controle le contenu.
- **Services externes** : `course-service`, `analytics-service`, `notification-service`.

## 4. Exigences non fonctionnelles
- Disponibilite cible : 99.9%
- Temps de reponse API : < 300 ms (lecture), < 600 ms (soumission)
- Scalabilite horizontale (stateless)
- Idempotence pour soumission de tentative
- Observabilite : logs structures, metrics, traces

## 5. Architecture logique
Architecture en couches :
1. **API Layer** (REST/JSON)
2. **Application Layer** (regles metier)
3. **Domain Layer** (entites quiz, question, tentative)
4. **Persistence Layer** (SQL/NoSQL selon stack)

Dependances :
- `auth-service` (validation token/JWT)
- `course-service` (liaison quiz <-> cours/module)
- `analytics-service` (event `quiz.completed`)
- `notification-service` (event `quiz.passed` / `quiz.failed`)

## 6. Modele de donnees (propose)

### 6.1 Entites
**Quiz**
- id (UUID)
- course_id (UUID)
- title
- description
- difficulty
- passing_score (0-100)
- time_limit_sec (nullable)
- status (`draft`, `published`, `archived`)
- created_at, updated_at

**Question**
- id (UUID)
- quiz_id (UUID)
- type (`single_choice`, `multi_choice`, `true_false`, `short_answer`)
- statement
- points
- order_index

**Choice**
- id (UUID)
- question_id (UUID)
- label
- is_correct

**Attempt**
- id (UUID)
- quiz_id (UUID)
- learner_id (UUID)
- started_at
- submitted_at
- score
- max_score
- passed (bool)
- status (`in_progress`, `submitted`, `evaluated`)

**Answer**
- id (UUID)
- attempt_id (UUID)
- question_id (UUID)
- selected_choice_ids (JSON)
- text_answer (nullable)
- is_correct
- earned_points

## 7. API REST (exemple)

### 7.1 Formateur/Admin
- `POST /api/quizzes` : creer un quiz
- `PUT /api/quizzes/{quizId}` : modifier un quiz
- `POST /api/quizzes/{quizId}/publish` : publier
- `POST /api/quizzes/{quizId}/questions` : ajouter question

### 7.2 Apprenant
- `GET /api/quizzes/{quizId}` : detail quiz publie
- `POST /api/quizzes/{quizId}/attempts` : demarrer tentative
- `POST /api/attempts/{attemptId}/answers` : sauvegarder reponse
- `POST /api/attempts/{attemptId}/submit` : soumettre et corriger
- `GET /api/attempts/{attemptId}/result` : consulter resultat

### 7.3 Integration
- `GET /api/learners/{learnerId}/attempts`
- `GET /api/quizzes/{quizId}/stats`

## 8. Regles metier
- Un quiz `draft` n'est pas visible pour les apprenants.
- Une tentative `submitted` ne peut plus etre modifiee.
- Score = somme(points gagnes) / somme(points totaux) * 100.
- Reussite si `score >= passing_score`.
- Limite configurable du nombre de tentatives par apprenant.
- Verifier la fenetre temporelle si `time_limit_sec` defini.

## 9. Securite
- Auth via JWT (Bearer token)
- RBAC :
  - `ROLE_INSTRUCTOR` / `ROLE_ADMIN` pour CRUD quiz
  - `ROLE_LEARNER` pour passation
- Validation stricte payload (taille texte, types, bornes)
- Protection anti-triche de base :
  - randomisation ordre questions/choix (optionnelle)
  - verrouillage apres soumission

## 10. Evenements et messaging
Evenements emis :
- `quiz.published`
- `quiz.attempt.started`
- `quiz.attempt.submitted`
- `quiz.completed`

Consommateurs potentiels :
- analytics (statistiques)
- notifications (email/push)
- progression utilisateur

## 11. Strategie de tests
- Tests unitaires : calcul score, regles de publication, validation
- Tests integration : API + base + auth mock
- Tests contractuels : schema API avec consommateurs
- Tests de charge : soumission simultanee de tentatives

## 12. Monitoring & exploitation
- Logs structures (correlation_id, user_id, quiz_id)
- Metrics :
  - taux de soumission
  - latence endpoints critiques
  - taux d'erreurs 4xx/5xx
- Alertes :
  - hausse erreurs soumission
  - degradation latence `submit`

## 13. Plan de livraison (MVP -> V2)
**MVP**
- quiz single/multi-choice
- correction automatique
- resultat instantane

**V2**
- banque de questions
- quiz adaptatif
- anti-triche avancee
- analyse pedagogique detaillee

## 14. Risques & mitigations
- **Risque** : surcharge pendant examens.
  - **Mitigation** : autoscaling + cache lecture.
- **Risque** : incoherence tentative/reponses.
  - **Mitigation** : transactions + contraintes FK.
- **Risque** : fraude.
  - **Mitigation** : randomisation + journaux d'audit.

---

Document version : `1.0`  
Date : `2026-05-25`  
Service : `quiz-service`

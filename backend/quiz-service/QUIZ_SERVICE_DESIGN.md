# Conception Détaillée : Quiz Microservice (quiz-service)

## 1. Introduction
Le `quiz-service` est un microservice critique chargé de l'évaluation des étudiants. Il gère la création des questionnaires par les instructeurs, la passation des tests par les étudiants, et le calcul automatisé des résultats.

## 2. Architecture Technique
- **Base de données** : PostgreSQL (Schéma : `quiz_db`)
- **Authentification** : JWT via Cookie (partagé avec `auth-service`)
- **Communication** : Feign Client vers `course-service` et `auth-service`
- **Validation** : Spring Boot Starter Validation

## 3. Modèle de Domaine (Entités JPA)

### Quiz (Principal)
- `id` : UUID (PK)
- `courseId` : String (FK vers course-service)
- `title` : String
- `description` : String
- `passingScore` : Double (ex: 70.0)
- `timeLimit` : Integer (en minutes)
- `questions` : List<Question> (OneToMany)

### Question
- `id` : UUID (PK)
- `content` : Text (L'énoncé)
- `points` : Double (Poids de la question)
- `type` : Enum (MULTIPLE_CHOICE, SINGLE_CHOICE, TRUE_FALSE)
- `quiz` : Quiz (ManyToOne)
- `choices` : List<Choice> (OneToMany)

### Choice (Options de réponse)
- `id` : UUID (PK)
- `content` : String
- `isCorrect` : Boolean
- `question` : Question (ManyToOne)

### QuizAttempt (Tentatives)
- `id` : UUID (PK)
- `quizId` : String
- `studentId` : String (Extrait du JWT)
- `score` : Double
- `startedAt` : LocalDateTime
- `completedAt` : LocalDateTime
- `status` : Enum (PASSED, FAILED, IN_PROGRESS)

## 4. Spécifications des APIs (REST)

### A. Endpoints Instructeur (ROLE_INSTRUCTOR)
| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/api/quizzes` | Créer un quiz avec questions et choix |
| PUT | `/api/quizzes/{id}` | Modifier la structure d'un quiz |
| DELETE | `/api/quizzes/{id}` | Supprimer un quiz et ses données |
| GET | `/api/quizzes/instructor/me` | Liste les quiz créés par l'instructeur |

### B. Endpoints Étudiant (ROLE_STUDENT)
| Méthode | Route | Description |
|---------|-------|-------------|
| GET | `/api/quizzes/course/{courseId}` | Voir les quiz disponibles pour un cours |
| GET | `/api/quizzes/{id}/play` | Récupérer le quiz (SANS les réponses correctes) |
| POST | `/api/quizzes/{id}/submit` | Envoyer les réponses et recevoir le score |
| GET | `/api/quizzes/attempts/me` | Historique personnel des scores |

## 5. Logique de Calcul du Score
L'algorithme de correction suit ces règles :
1. Pour chaque question, on compare les `choiceId` envoyés par l'étudiant avec ceux marqués `isCorrect: true` en base.
2. Si la correspondance est exacte, l'étudiant gagne les `points` de la question.
3. Le score final est : `(Points_Obtenus / Total_Points_Possible) * 100`.
4. Si `Score >= passingScore`, le statut est `PASSED`.

## 6. Sécurité et Contraintes
- **Isolement** : Un étudiant ne peut pas voir le champ `isCorrect` via l'API avant d'avoir soumis sa tentative.
- **Validation** : 
    - Un quiz doit avoir au moins 1 question.
    - Une question doit avoir au moins 2 choix.
    - Une question doit avoir au moins 1 réponse correcte.
- **Transactions** : La soumission d'un quiz est transactionnelle (`@Transactional`).

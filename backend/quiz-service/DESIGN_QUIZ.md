# Conception Détaillée : Quiz Microservice (quiz-service)

## 1. Vue d'Ensemble
Le `quiz-service` est responsable de la création, de la gestion et de la passation des quiz au sein de la plateforme e-learning. Il permet aux instructeurs de tester les connaissances des étudiants et fournit un feedback immédiat.

## 2. Architecture Technique
- **Framework** : Spring Boot 3.x
- **Langage** : Java 21
- **Base de données** : PostgreSQL (ou MySQL)
- **Sécurité** : JWT via Cookie (intégré avec auth-service)
- **Communication Inter-services** : OpenFeign (pour vérifier l'existence des cours)

## 3. Modèle de Données (Entities)

### A. Quiz
Représente un ensemble de questions lié à un cours.
- `id` (UUID) : Identifiant unique.
- `title` (String) : Titre du quiz.
- `description` (Text) : Description du quiz.
- `courseId` (String) : ID du cours associé (référence vers course-service).
- `timeLimit` (Integer) : Limite de temps en minutes (0 si aucune).
- `passingScore` (Double) : Score minimum pour réussir.
- `createdAt/updatedAt` : Timestamps d'audit.

### B. Question
- `id` (UUID)
- `content` (Text) : L'énoncé de la question.
- `type` (Enum) : `MULTIPLE_CHOICE`, `SINGLE_CHOICE`, `TRUE_FALSE`.
- `points` (Double) : Points attribués à cette question.
- `quizId` (FK) : Lien vers le quiz.

### C. Choice (Option de réponse)
- `id` (UUID)
- `content` (String)
- `isCorrect` (Boolean) : Indique si c'est la bonne réponse.
- `questionId` (FK) : Lien vers la question.

### D. QuizAttempt (Tentative de l'étudiant)
- `id` (UUID)
- `quizId` (String)
- `studentId` (String) : ID de l'étudiant (extrait du JWT).
- `score` (Double) : Score obtenu.
- `status` (Enum) : `IN_PROGRESS`, `COMPLETED`, `FAILED`, `PASSED`.
- `startedAt/completedAt` : Timestamps.

## 4. Endpoints API (REST)

### Gestion des Quiz (Instructeur/Admin)
- `POST /api/quizzes` : Créer un nouveau quiz.
- `PUT /api/quizzes/{id}` : Modifier un quiz.
- `DELETE /api/quizzes/{id}` : Supprimer un quiz.
- `GET /api/quizzes/course/{courseId}` : Lister les quiz d'un cours.

### Passation de Quiz (Étudiant)
- `GET /api/quizzes/{id}` : Récupérer les détails d'un quiz (sans les bonnes réponses).
- `POST /api/quizzes/{id}/attempt` : Démarrer une tentative.
- `POST /api/quizzes/attempts/{attemptId}/submit` : Soumettre les réponses et calculer le score.
- `GET /api/quizzes/attempts/me` : Historique des tentatives de l'étudiant.

## 5. Logique Métier Critique
1. **Calcul du Score** : 
   - Pour chaque question, vérifier les choix sélectionnés par l'étudiant.
   - Comparer avec les `isCorrect = true`.
   - Calculer le ratio de points gagnés.
2. **Sécurité** :
   - Seuls les instructeurs peuvent créer/modifier des quiz.
   - Les étudiants ne voient jamais le champ `isCorrect` avant la soumission.
3. **Validation** :
   - Un quiz doit avoir au moins une question.
   - Chaque question doit avoir au moins une réponse correcte.

## 6. Flux de Travail (Workflow)
1. L'instructeur crée le Quiz -> Questions -> Choices.
2. L'étudiant accède au Quiz via le module de cours.
3. L'étudiant soumet ses réponses (JSON listant questionId et choiceIds).
4. Le service calcule le score, met à jour la tentative et renvoie le résultat.

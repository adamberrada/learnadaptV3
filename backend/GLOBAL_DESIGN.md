# Architecture Globale du Système E-Learning

## 1. Architecture des Microservices
Le système est décomposé en services autonomes communiquant via des APIs REST et potentiellement un bus de messages (RabbitMQ/Kafka).

### Liste des Services
1.  **Auth-Service** : Gestion des utilisateurs, rôles, authentification JWT (HttpOnly Cookies).
2.  **Course-Service** : Gestion du catalogue de cours, chapitres, et contenus (vidéos, PDFs).
3.  **Quiz-Service** : Évaluations, questions, scoring et tentatives des étudiants.
4.  **Platform-Service** : Gestion des inscriptions (enrollments) et progression globale.
5.  **Notification-Service** : Envoi d'emails et notifications en temps réel (WebSockets).
6.  **Analytics-Service** : Rapports de performance et statistiques d'utilisation.
7.  **AI-Service** : Recommandations de cours et assistant de tutorat intelligent.

## 2. Flux de Sécurité (Centralisé)
L'authentification est gérée par le `auth-service`.

- **Login** : L'utilisateur envoie ses credentials -> `auth-service` valide -> Retourne un **Set-Cookie (HttpOnly, Secure)** contenant le JWT.
- **Validation** : Chaque microservice possède un `JwtAuthenticationFilter` qui extrait le JWT du cookie pour authentifier l'utilisateur localement sans appel constant à la DB.

## 3. Communication Inter-services (Exemple : Quiz -> Course)
Lorsqu'un Quiz est créé, le `quiz-service` doit s'assurer que le `courseId` fourni existe.
- **Outil** : Spring Cloud OpenFeign.
- **Flux** : `QuizController` -> `CourseClient (Feign)` -> `course-service/api/courses/{id}`.

## 4. Structure de Données Globale (Entités Clés)

### Auth-Service
- `User` (id, email, password, role)

### Course-Service
- `Course` (id, title, instructorId, price)
- `Module` (id, courseId, title)
- `Lesson` (id, moduleId, contentUrl)

### Quiz-Service
- `Quiz` (id, courseId, passingScore)
- `Question` (id, quizId, content, points)
- `Choice` (id, questionId, content, isCorrect)
- `QuizAttempt` (id, studentId, score, status)

### Platform-Service
- `Enrollment` (id, studentId, courseId, progressPercentage)

## 5. Patterns de Design utilisés
- **API Gateway** (Optionnel, recommandé) : Point d'entrée unique (Spring Cloud Gateway).
- **Service Discovery** : Netflix Eureka (pour que les services se trouvent mutuellement).
- **Config Server** : Centralisation des fichiers `application.properties`.
- **Database per Service** : Chaque service a sa propre base pour garantir l'indépendance.

## 6. Stack Technologique
- **Backend** : Java 21, Spring Boot 3.4+, Hibernate.
- **Security** : Spring Security 6, JJWT 0.12.5.
- **Communication** : OpenFeign, RestTemplate.
- **Base de données** : PostgreSQL (Relationnel) / MongoDB (pour le contenu riche).
- **Build** : Maven.

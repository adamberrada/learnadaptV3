# Course Service - Gestion des cours

Microservice Spring Boot responsable de la gestion des cours d'une plateforme d'E-learning adaptive et collaborative.

Il couvre les cas d'utilisation des trois acteurs principaux:

- Enseignant: creation, modification, organisation et soumission des cours.
- Apprenant: consultation, progression, favoris, avis et desinscription.
- Administrateur: moderation des cours et gestion du catalogue de categories.

## Stack technique

- Java 21
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Jakarta Validation
- H2 Database en developpement
- Lombok
- Maven Wrapper

## Lancement

```powershell
cd C:\E-learning\course-service
.\mvnw.cmd spring-boot:run
```

Le service demarre sur:

```text
http://localhost:8082
```

Console H2:

```text
http://localhost:8082/h2-console
```

Configuration H2:

```text
JDBC URL: jdbc:h2:mem:course_db
User: sa
Password:
```

## Verification

```powershell
.\mvnw.cmd test
```

## Architecture

```text
src/main/java/com/anouar/elearning/course
├── config       # Configuration Spring Security
├── controller   # APIs REST par acteur: public, teacher, learner, admin
├── dto          # Objets d'entree/sortie REST
├── entity       # Entites JPA du domaine cours
├── exception    # Exceptions et gestion globale des erreurs
├── repository   # Repositories Spring Data JPA
├── security     # Authentification stateless par headers
└── service      # Logique metier
```

## Authentification et RBAC

Les routes protegees utilisent une authentification stateless simplifiee par headers. Dans une integration finale, ces headers doivent etre produits par l'API Gateway ou l'auth-service apres validation du JWT.

Headers requis:

```http
X-User-Id: teacher-1
X-User-Role: TEACHER
```

Roles acceptes:

```text
TEACHER, ENSEIGNANT, INSTRUCTOR
LEARNER, APPRENANT, STUDENT
ADMIN, ADMINISTRATEUR
```

Les routes publiques ne demandent pas d'authentification:

```text
/api/public/**
/h2-console/**
```

## Modele de donnees

### Course

Represente un cours.

Champs principaux:

- `id`
- `title`
- `description`
- `category`
- `subCategory`
- `instructorId`
- `thumbnailUrl`
- `level`
- `status`
- `price`
- `durationInMinutes`
- `tags`
- `chapters`
- `createdAt`
- `updatedAt`

Statuts:

```text
DRAFT
SUBMITTED
PUBLISHED
ARCHIVED
REJECTED
```

Niveaux:

```text
BEGINNER
INTERMEDIATE
ADVANCED
```

### Category et SubCategory

Permettent de naviguer dans l'arborescence du catalogue.

Une sous-categorie appartient obligatoirement a une categorie.

### Chapter

Represente un chapitre d'un cours.

Champs principaux:

- `id`
- `title`
- `description`
- `orderIndex`
- `course`
- `lessons`

### Lesson

Represente une lecon dans un chapitre.

Types:

```text
VIDEO
EXTERNAL_LINK
TEXT
```

Regles:

- `VIDEO` exige `videoUrl`
- `EXTERNAL_LINK` exige `externalUrl`
- `TEXT` exige `textContent`

### CourseFavorite

Associe un apprenant a un cours favori.

Contrainte unique:

```text
learnerId + courseId
```

### CourseReview

Permet a un apprenant de noter un cours.

Regles:

- Note entre `1` et `5`
- Un seul avis par apprenant et par cours

### CourseEnrollment

Trace l'inscription ou la desinscription d'un apprenant a un cours.

### LessonProgress

Trace la progression d'un apprenant sur une lecon.

Contrainte unique:

```text
learnerId + lessonId
```

## APIs publiques

### Lister et rechercher les cours publies

```http
GET /api/public/courses
```

Filtres optionnels:

```text
keyword
tag
categoryId
subCategoryId
```

Exemple:

```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/public/courses?keyword=spring&tag=microservice"
```

### Consulter un cours publie

```http
GET /api/public/courses/{courseId}
```

### Consulter le plan d'un cours

```http
GET /api/public/courses/{courseId}/outline
```

### Lister les categories

```http
GET /api/public/categories
```

### Lister les sous-categories

```http
GET /api/public/sub-categories?categoryId={categoryId}
```

## APIs Enseignant

Toutes les routes exigent:

```http
X-User-Id: teacher-1
X-User-Role: TEACHER
```

### Creer un cours

```http
POST /api/teacher/courses
```

Body:

```json
{
  "title": "Architecture Spring Boot",
  "description": "Concevoir un microservice propre",
  "categoryId": "category-id",
  "subCategoryId": "sub-category-id",
  "thumbnailUrl": "https://example.com/image.png",
  "level": "BEGINNER",
  "price": 0,
  "durationInMinutes": 90,
  "tags": ["spring", "microservice"]
}
```

Le cours est cree avec le statut `DRAFT`.

### Consulter ses propres cours

```http
GET /api/teacher/courses
```

### Modifier un cours

```http
PUT /api/teacher/courses/{courseId}
```

Regle: l'enseignant ne peut modifier que ses propres cours.

### Supprimer un cours

```http
DELETE /api/teacher/courses/{courseId}
```

### Soumettre un cours pour validation

```http
POST /api/teacher/courses/{courseId}/submit
```

Regles:

- Le cours doit appartenir a l'enseignant.
- Le cours doit contenir au moins un chapitre.
- Le cours doit contenir au moins une lecon.
- Le statut devient `SUBMITTED`.

### Archiver un cours

```http
POST /api/teacher/courses/{courseId}/archive
```

Le statut devient `ARCHIVED`.

### Creer un chapitre

```http
POST /api/teacher/courses/{courseId}/chapters
```

Body:

```json
{
  "title": "Introduction",
  "description": "Bases du cours",
  "orderIndex": 0
}
```

### Modifier un chapitre

```http
PUT /api/teacher/courses/{courseId}/chapters/{chapterId}
```

### Supprimer un chapitre

```http
DELETE /api/teacher/courses/{courseId}/chapters/{chapterId}
```

### Creer une lecon

```http
POST /api/teacher/courses/{courseId}/chapters/{chapterId}/lessons
```

Lecon texte:

```json
{
  "title": "Lecon 1",
  "type": "TEXT",
  "textContent": "Contenu textuel de la lecon",
  "orderIndex": 0
}
```

Lecon video:

```json
{
  "title": "Video d'introduction",
  "type": "VIDEO",
  "videoUrl": "https://cdn.example.com/video.mp4",
  "orderIndex": 1
}
```

Lien externe:

```json
{
  "title": "Documentation officielle",
  "type": "EXTERNAL_LINK",
  "externalUrl": "https://spring.io/projects/spring-boot",
  "orderIndex": 2
}
```

### Modifier une lecon

```http
PUT /api/teacher/courses/{courseId}/chapters/{chapterId}/lessons/{lessonId}
```

### Supprimer une lecon

```http
DELETE /api/teacher/courses/{courseId}/chapters/{chapterId}/lessons/{lessonId}
```

### Reordonner les lecons

```http
PUT /api/teacher/courses/{courseId}/chapters/{chapterId}/lessons/order
```

Body:

```json
{
  "lessons": [
    {
      "lessonId": "lesson-id-1",
      "orderIndex": 0
    },
    {
      "lessonId": "lesson-id-2",
      "orderIndex": 1
    }
  ]
}
```

## APIs Apprenant

Toutes les routes exigent:

```http
X-User-Id: learner-1
X-User-Role: LEARNER
```

### Marquer une lecon comme terminee

```http
POST /api/learner/lessons/{lessonId}/complete
```

Regles:

- Le cours de la lecon doit etre `PUBLISHED`.
- Une inscription active est creee automatiquement si elle n'existe pas.
- La progression du cours est recalculee.

### Ajouter un cours aux favoris

```http
POST /api/learner/courses/{courseId}/favorites
```

Regle: le cours doit etre publie.

### Noter un cours

```http
POST /api/learner/courses/{courseId}/reviews
```

Body:

```json
{
  "rating": 5,
  "comment": "Excellent cours"
}
```

Regles:

- Note entre `1` et `5`.
- Un avis existant du meme apprenant sur le meme cours est mis a jour.

### Se desinscrire d'un cours

```http
DELETE /api/learner/courses/{courseId}/enrollment
```

## APIs Administrateur

Toutes les routes exigent:

```http
X-User-Id: admin-1
X-User-Role: ADMIN
```

### Lister tous les cours

```http
GET /api/admin/courses
```

Filtres optionnels:

```text
keyword
tag
```

### Consulter un cours

```http
GET /api/admin/courses/{courseId}
```

### Valider un cours soumis

```http
POST /api/admin/courses/{courseId}/approve
```

Regle: seul un cours `SUBMITTED` peut etre approuve. Le statut devient `PUBLISHED`.

### Supprimer un cours non conforme

```http
DELETE /api/admin/courses/{courseId}
```

### Creer une categorie

```http
POST /api/admin/categories
```

Body:

```json
{
  "name": "Backend",
  "description": "Cours backend et microservices"
}
```

### Modifier une categorie

```http
PUT /api/admin/categories/{categoryId}
```

### Supprimer une categorie

```http
DELETE /api/admin/categories/{categoryId}
```

### Chercher ou lister les categories

```http
GET /api/admin/categories?search=back
```

### Creer une sous-categorie

```http
POST /api/admin/sub-categories
```

Body:

```json
{
  "name": "Spring Boot",
  "description": "Cours Spring Boot",
  "categoryId": "category-id"
}
```

### Modifier une sous-categorie

```http
PUT /api/admin/sub-categories/{subCategoryId}
```

### Supprimer une sous-categorie

```http
DELETE /api/admin/sub-categories/{subCategoryId}
```

### Chercher ou lister les sous-categories

```http
GET /api/admin/sub-categories?categoryId={categoryId}
GET /api/admin/sub-categories?search=spring
```

## Format standard des reponses

Succes:

```json
{
  "success": true,
  "message": "Course created successfully!",
  "data": {}
}
```

Erreur:

```json
{
  "timestamp": "2026-05-25T19:40:00",
  "status": 400,
  "message": "Validation failed",
  "path": "/api/teacher/courses",
  "errors": {
    "title": "Title is required"
  }
}
```

## Exemple de scenario complet

### 1. Admin cree une categorie

```powershell
$adminHeaders = @{ "X-User-Id"="admin-1"; "X-User-Role"="ADMIN" }
$body = @{ name="Backend"; description="Cours backend" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8082/api/admin/categories" -Method Post -Headers $adminHeaders -ContentType "application/json" -Body $body
```

### 2. Enseignant cree un cours

```powershell
$teacherHeaders = @{ "X-User-Id"="teacher-1"; "X-User-Role"="TEACHER" }
$body = @{
  title="Architecture Spring Boot"
  description="Concevoir un microservice propre"
  categoryId="category-id"
  level="BEGINNER"
  price=0
  durationInMinutes=90
  tags=@("spring", "microservice")
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/teacher/courses" -Method Post -Headers $teacherHeaders -ContentType "application/json" -Body $body
```

### 3. Enseignant ajoute chapitre et lecon

```powershell
$chapterBody = @{ title="Introduction"; description="Bases"; orderIndex=0 } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8082/api/teacher/courses/{courseId}/chapters" -Method Post -Headers $teacherHeaders -ContentType "application/json" -Body $chapterBody
```

```powershell
$lessonBody = @{
  title="Lecon 1"
  type="TEXT"
  textContent="Contenu textuel"
  orderIndex=0
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/teacher/courses/{courseId}/chapters/{chapterId}/lessons" -Method Post -Headers $teacherHeaders -ContentType "application/json" -Body $lessonBody
```

### 4. Enseignant soumet le cours

```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/teacher/courses/{courseId}/submit" -Method Post -Headers $teacherHeaders
```

### 5. Admin approuve le cours

```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/admin/courses/{courseId}/approve" -Method Post -Headers $adminHeaders
```

### 6. Apprenant suit une lecon

```powershell
$learnerHeaders = @{ "X-User-Id"="learner-1"; "X-User-Role"="LEARNER" }
Invoke-RestMethod -Uri "http://localhost:8082/api/learner/lessons/{lessonId}/complete" -Method Post -Headers $learnerHeaders
```

## Notes d'integration

La securite actuelle est volontairement simple pour faciliter le developpement local. En production, il faut remplacer `HeaderAuthenticationFilter` par une validation JWT reelle, ou faire valider le JWT par l'API Gateway puis transmettre l'identite utilisateur via des headers internes signes.

Pour une base persistante, remplacer la configuration H2 dans `application.properties` par PostgreSQL ou MySQL.

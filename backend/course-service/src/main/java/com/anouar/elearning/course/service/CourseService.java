package com.anouar.elearning.course.service;

import com.anouar.elearning.course.dto.*;
import com.anouar.elearning.course.entity.*;
import com.anouar.elearning.course.exception.BusinessException;
import com.anouar.elearning.course.exception.ForbiddenException;
import com.anouar.elearning.course.exception.ResourceNotFoundException;
import com.anouar.elearning.course.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final CourseFavoriteRepository favoriteRepository;
    private final CourseReviewRepository reviewRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository progressRepository;
    private final CourseMapper mapper;

    @Transactional
    public CourseResponse createCourse(String instructorId, CourseRequest request) {
        Category category = findCategory(request.getCategoryId());
        SubCategory subCategory = findSubCategoryOrNull(request.getSubCategoryId(), category.getId());

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .subCategory(subCategory)
                .instructorId(instructorId)
                .thumbnailUrl(request.getThumbnailUrl())
                .level(request.getLevel())
                .status(CourseStatus.DRAFT)
                .price(request.getPrice())
                .durationInMinutes(request.getDurationInMinutes())
                .tags(request.getTags() == null ? new HashSet<>() : request.getTags())
                .build();

        return mapper.toCourseResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse updateCourse(String instructorId, String courseId, CourseRequest request) {
        Course course = findOwnedCourse(instructorId, courseId);
        if (course.getStatus() == CourseStatus.PUBLISHED) {
            throw new BusinessException("A published course must be archived before structural modification");
        }

        Category category = findCategory(request.getCategoryId());
        SubCategory subCategory = findSubCategoryOrNull(request.getSubCategoryId(), category.getId());
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(category);
        course.setSubCategory(subCategory);
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setLevel(request.getLevel());
        course.setPrice(request.getPrice());
        course.setDurationInMinutes(request.getDurationInMinutes());
        course.setTags(request.getTags() == null ? new HashSet<>() : request.getTags());

        return mapper.toCourseResponse(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourseByTeacher(String instructorId, String courseId) {
        Course course = findOwnedCourse(instructorId, courseId);
        courseRepository.delete(course);
    }

    @Transactional
    public void deleteCourseByAdmin(String courseId) {
        courseRepository.delete(findCourse(courseId));
    }

    @Transactional
    public CourseResponse submitCourse(String instructorId, String courseId) {
        Course course = findOwnedCourse(instructorId, courseId);
        if (course.getChapters().isEmpty()) {
            throw new BusinessException("A course must contain at least one chapter before submission");
        }
        boolean hasLesson = course.getChapters().stream().anyMatch(chapter -> !chapter.getLessons().isEmpty());
        if (!hasLesson) {
            throw new BusinessException("A course must contain at least one lesson before submission");
        }
        course.setStatus(CourseStatus.SUBMITTED);
        return mapper.toCourseResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse archiveCourse(String instructorId, String courseId) {
        Course course = findOwnedCourse(instructorId, courseId);
        course.setStatus(CourseStatus.ARCHIVED);
        return mapper.toCourseResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse approveCourse(String courseId) {
        Course course = findCourse(courseId);
        if (course.getStatus() != CourseStatus.SUBMITTED) {
            throw new BusinessException("Only submitted courses can be approved");
        }
        course.setStatus(CourseStatus.PUBLISHED);
        return mapper.toCourseResponse(courseRepository.save(course));
    }

    public List<CourseResponse> getTeacherCourses(String instructorId) {
        return mapper.toCourseResponses(courseRepository.findByInstructorId(instructorId));
    }

    public List<CourseResponse> getAllCoursesForAdmin(String keyword, String tag) {
        return mapper.toCourseResponses(courseRepository.search(blankToNull(keyword), blankToNull(tag), null));
    }

    public List<CourseResponse> browsePublishedCourses(String categoryId, String subCategoryId, String keyword, String tag) {
        if (subCategoryId != null && !subCategoryId.isBlank()) {
            return mapper.toCourseResponses(courseRepository.findBySubCategoryIdAndStatus(subCategoryId, CourseStatus.PUBLISHED));
        }
        if (categoryId != null && !categoryId.isBlank()) {
            return mapper.toCourseResponses(courseRepository.findByCategoryIdAndStatus(categoryId, CourseStatus.PUBLISHED));
        }
        return mapper.toCourseResponses(courseRepository.search(blankToNull(keyword), blankToNull(tag), CourseStatus.PUBLISHED));
    }

    public List<CourseResponse> getLearnerCourses(String learnerId) {
        List<Course> courses = enrollmentRepository.findByLearnerIdAndActiveTrue(learnerId)
                .stream()
                .map(CourseEnrollment::getCourse)
                .toList();
        return mapper.toCourseResponses(courses);
    }

    public CourseResponse getPublishedCourse(String courseId) {
        Course course = findCourse(courseId);
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Published course not found with id: " + courseId);
        }
        return mapper.toCourseResponse(course);
    }

    public CourseResponse getAnyCourse(String courseId) {
        return mapper.toCourseResponse(findCourse(courseId));
    }

    @Transactional
    public ChapterResponse createChapter(String instructorId, String courseId, ChapterRequest request) {
        Course course = findOwnedCourse(instructorId, courseId);
        Chapter chapter = Chapter.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(request.getOrderIndex())
                .course(course)
                .build();
        return mapper.toChapterResponse(chapterRepository.save(chapter));
    }

    @Transactional
    public ChapterResponse updateChapter(String instructorId, String courseId, String chapterId, ChapterRequest request) {
        Chapter chapter = findOwnedChapter(instructorId, courseId, chapterId);
        chapter.setTitle(request.getTitle());
        chapter.setDescription(request.getDescription());
        chapter.setOrderIndex(request.getOrderIndex());
        return mapper.toChapterResponse(chapterRepository.save(chapter));
    }

    @Transactional
    public void deleteChapter(String instructorId, String courseId, String chapterId) {
        chapterRepository.delete(findOwnedChapter(instructorId, courseId, chapterId));
    }

    @Transactional
    public LessonResponse createLesson(String instructorId, String courseId, String chapterId, LessonRequest request) {
        Chapter chapter = findOwnedChapter(instructorId, courseId, chapterId);
        validateLessonPayload(request);
        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .type(request.getType())
                .videoUrl(request.getVideoUrl())
                .externalUrl(request.getExternalUrl())
                .textContent(request.getTextContent())
                .orderIndex(request.getOrderIndex())
                .chapter(chapter)
                .build();
        return mapper.toLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonResponse updateLesson(String instructorId, String courseId, String chapterId, String lessonId, LessonRequest request) {
        Lesson lesson = findOwnedLesson(instructorId, courseId, chapterId, lessonId);
        validateLessonPayload(request);
        lesson.setTitle(request.getTitle());
        lesson.setType(request.getType());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setExternalUrl(request.getExternalUrl());
        lesson.setTextContent(request.getTextContent());
        lesson.setOrderIndex(request.getOrderIndex());
        return mapper.toLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public void deleteLesson(String instructorId, String courseId, String chapterId, String lessonId) {
        lessonRepository.delete(findOwnedLesson(instructorId, courseId, chapterId, lessonId));
    }

    @Transactional
    public List<LessonResponse> reorderLessons(String instructorId, String courseId, String chapterId, LessonOrderRequest request) {
        findOwnedChapter(instructorId, courseId, chapterId);
        List<Lesson> lessons = lessonRepository.findByChapterIdOrderByOrderIndexAsc(chapterId);
        request.getLessons().forEach(item -> {
            Lesson lesson = lessons.stream()
                    .filter(existing -> existing.getId().equals(item.getLessonId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found in chapter: " + item.getLessonId()));
            lesson.setOrderIndex(item.getOrderIndex());
        });
        return mapper.toLessonResponses(lessonRepository.saveAll(lessons));
    }

    @Transactional
    public ProgressResponse completeLesson(String learnerId, String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
        Course course = lesson.getChapter().getCourse();
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BusinessException("Only published courses can be followed");
        }
        ensureActiveEnrollment(learnerId, course);

        LessonProgress progress = progressRepository.findByLearnerIdAndLessonId(learnerId, lessonId)
                .orElse(LessonProgress.builder().learnerId(learnerId).lesson(lesson).build());
        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        progressRepository.save(progress);

        return buildProgressResponse(learnerId, lesson, progress);
    }

    @Transactional
    public CourseResponse addFavorite(String learnerId, String courseId) {
        Course course = findPublishedCourse(courseId);
        favoriteRepository.findByLearnerIdAndCourseId(learnerId, courseId)
                .orElseGet(() -> favoriteRepository.save(CourseFavorite.builder()
                        .learnerId(learnerId)
                        .course(course)
                        .build()));
        return mapper.toCourseResponse(course);
    }

    @Transactional
    public CourseResponse enrollCourse(String learnerId, String courseId) {
        Course course = findPublishedCourse(courseId);
        ensureActiveEnrollment(learnerId, course);
        return mapper.toCourseResponse(course);
    }

    @Transactional
    public ReviewResponse reviewCourse(String learnerId, String courseId, ReviewRequest request) {
        Course course = findPublishedCourse(courseId);
        ensureActiveEnrollment(learnerId, course);
        CourseReview review = reviewRepository.findByLearnerIdAndCourseId(learnerId, courseId)
                .orElse(CourseReview.builder().learnerId(learnerId).course(course).build());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return mapper.toReviewResponse(reviewRepository.save(review));
    }

    @Transactional
    public void unenroll(String learnerId, String courseId) {
        CourseEnrollment enrollment = enrollmentRepository.findByLearnerIdAndCourseId(learnerId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for course: " + courseId));
        enrollment.setActive(false);
        enrollment.setUnenrolledAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }

    private void ensureActiveEnrollment(String learnerId, Course course) {
        CourseEnrollment enrollment = enrollmentRepository.findByLearnerIdAndCourseId(learnerId, course.getId())
                .orElseGet(() -> enrollmentRepository.save(CourseEnrollment.builder()
                        .learnerId(learnerId)
                        .course(course)
                        .active(true)
                        .build()));
        if (!enrollment.isActive()) {
            enrollment.setActive(true);
            enrollment.setUnenrolledAt(null);
            enrollmentRepository.save(enrollment);
        }
    }

    private ProgressResponse buildProgressResponse(String learnerId, Lesson lesson, LessonProgress progress) {
        Course course = lesson.getChapter().getCourse();
        int totalLessons = course.getChapters().stream().mapToInt(chapter -> chapter.getLessons().size()).sum();
        int completedLessons = progressRepository.countByLearnerIdAndLessonChapterCourseIdAndCompletedTrue(learnerId, course.getId());
        double percentage = totalLessons == 0 ? 0 : (completedLessons * 100.0) / totalLessons;
        return ProgressResponse.builder()
                .learnerId(learnerId)
                .lessonId(lesson.getId())
                .courseId(course.getId())
                .completed(progress.isCompleted())
                .completedLessons(completedLessons)
                .totalLessons(totalLessons)
                .percentage(percentage)
                .completedAt(progress.getCompletedAt())
                .build();
    }

    private void validateLessonPayload(LessonRequest request) {
        switch (request.getType()) {
            case VIDEO -> {
                if (request.getVideoUrl() == null || request.getVideoUrl().isBlank()) {
                    throw new BusinessException("A VIDEO lesson requires videoUrl");
                }
            }
            case EXTERNAL_LINK -> {
                if (request.getExternalUrl() == null || request.getExternalUrl().isBlank()) {
                    throw new BusinessException("An EXTERNAL_LINK lesson requires externalUrl");
                }
            }
            case TEXT -> {
                if (request.getTextContent() == null || request.getTextContent().isBlank()) {
                    throw new BusinessException("A TEXT lesson requires textContent");
                }
            }
        }
    }

    private Course findOwnedCourse(String instructorId, String courseId) {
        Course course = findCourse(courseId);
        if (!course.getInstructorId().equals(instructorId)) {
            throw new ForbiddenException("You can only manage your own courses");
        }
        return course;
    }

    private Chapter findOwnedChapter(String instructorId, String courseId, String chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + chapterId));
        if (!chapter.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Chapter not found in course: " + courseId);
        }
        if (!chapter.getCourse().getInstructorId().equals(instructorId)) {
            throw new ForbiddenException("You can only manage chapters of your own courses");
        }
        return chapter;
    }

    private Lesson findOwnedLesson(String instructorId, String courseId, String chapterId, String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
        Chapter chapter = lesson.getChapter();
        if (!chapter.getId().equals(chapterId) || !chapter.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Lesson not found in chapter: " + chapterId);
        }
        if (!chapter.getCourse().getInstructorId().equals(instructorId)) {
            throw new ForbiddenException("You can only manage lessons of your own courses");
        }
        return lesson;
    }

    private Course findPublishedCourse(String courseId) {
        Course course = findCourse(courseId);
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Published course not found with id: " + courseId);
        }
        return course;
    }

    private Course findCourse(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
    }

    private Category findCategory(String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private SubCategory findSubCategoryOrNull(String subCategoryId, String categoryId) {
        if (subCategoryId == null || subCategoryId.isBlank()) {
            return null;
        }
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-category not found with id: " + subCategoryId));
        if (!subCategory.getCategory().getId().equals(categoryId)) {
            throw new BusinessException("Sub-category does not belong to the selected category");
        }
        return subCategory;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

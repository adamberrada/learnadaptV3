package com.anouar.elearning.course.controller;

import com.anouar.elearning.course.dto.*;
import com.anouar.elearning.course.security.SecurityUtils;
import com.anouar.elearning.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/courses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherCourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse course = courseService.createCourse(SecurityUtils.currentUser().id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Course created successfully!", course));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getMyCourses() {
        return ResponseEntity.ok(ApiResponse.success(
                "Teacher courses retrieved successfully!",
                courseService.getTeacherCourses(SecurityUtils.currentUser().id())
        ));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable String courseId,
            @Valid @RequestBody CourseRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Course updated successfully!",
                courseService.updateCourse(SecurityUtils.currentUser().id(), courseId, request)
        ));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse<String>> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourseByTeacher(SecurityUtils.currentUser().id(), courseId);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully!", courseId));
    }

    @PostMapping("/{courseId}/submit")
    public ResponseEntity<ApiResponse<CourseResponse>> submitCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Course submitted for validation!",
                courseService.submitCourse(SecurityUtils.currentUser().id(), courseId)
        ));
    }

    @PostMapping("/{courseId}/archive")
    public ResponseEntity<ApiResponse<CourseResponse>> archiveCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Course archived successfully!",
                courseService.archiveCourse(SecurityUtils.currentUser().id(), courseId)
        ));
    }

    @PostMapping("/{courseId}/chapters")
    public ResponseEntity<ApiResponse<ChapterResponse>> createChapter(
            @PathVariable String courseId,
            @Valid @RequestBody ChapterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Chapter created successfully!",
                courseService.createChapter(SecurityUtils.currentUser().id(), courseId, request)
        ));
    }

    @PutMapping("/{courseId}/chapters/{chapterId}")
    public ResponseEntity<ApiResponse<ChapterResponse>> updateChapter(
            @PathVariable String courseId,
            @PathVariable String chapterId,
            @Valid @RequestBody ChapterRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Chapter updated successfully!",
                courseService.updateChapter(SecurityUtils.currentUser().id(), courseId, chapterId, request)
        ));
    }

    @DeleteMapping("/{courseId}/chapters/{chapterId}")
    public ResponseEntity<ApiResponse<String>> deleteChapter(@PathVariable String courseId, @PathVariable String chapterId) {
        courseService.deleteChapter(SecurityUtils.currentUser().id(), courseId, chapterId);
        return ResponseEntity.ok(ApiResponse.success("Chapter deleted successfully!", chapterId));
    }

    @PostMapping("/{courseId}/chapters/{chapterId}/lessons")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @PathVariable String courseId,
            @PathVariable String chapterId,
            @Valid @RequestBody LessonRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Lesson created successfully!",
                courseService.createLesson(SecurityUtils.currentUser().id(), courseId, chapterId, request)
        ));
    }

    @PutMapping("/{courseId}/chapters/{chapterId}/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable String courseId,
            @PathVariable String chapterId,
            @PathVariable String lessonId,
            @Valid @RequestBody LessonRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lesson updated successfully!",
                courseService.updateLesson(SecurityUtils.currentUser().id(), courseId, chapterId, lessonId, request)
        ));
    }

    @DeleteMapping("/{courseId}/chapters/{chapterId}/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<String>> deleteLesson(
            @PathVariable String courseId,
            @PathVariable String chapterId,
            @PathVariable String lessonId
    ) {
        courseService.deleteLesson(SecurityUtils.currentUser().id(), courseId, chapterId, lessonId);
        return ResponseEntity.ok(ApiResponse.success("Lesson deleted successfully!", lessonId));
    }

    @PutMapping("/{courseId}/chapters/{chapterId}/lessons/order")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> reorderLessons(
            @PathVariable String courseId,
            @PathVariable String chapterId,
            @Valid @RequestBody LessonOrderRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lessons reordered successfully!",
                courseService.reorderLessons(SecurityUtils.currentUser().id(), courseId, chapterId, request)
        ));
    }
}

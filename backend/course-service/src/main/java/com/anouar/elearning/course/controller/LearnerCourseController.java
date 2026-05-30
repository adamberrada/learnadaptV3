package com.anouar.elearning.course.controller;

import com.anouar.elearning.course.dto.*;
import com.anouar.elearning.course.security.SecurityUtils;
import com.anouar.elearning.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learner")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LEARNER')")
public class LearnerCourseController {

    private final CourseService courseService;

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getMyCourses() {
        return ResponseEntity.ok(ApiResponse.success(
                "Learner courses retrieved successfully!",
                courseService.getLearnerCourses(SecurityUtils.currentUser().id())
        ));
    }

    @PostMapping("/courses/{courseId}/enrollment")
    public ResponseEntity<ApiResponse<CourseResponse>> enroll(@PathVariable String courseId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Learner enrolled successfully!",
                courseService.enrollCourse(SecurityUtils.currentUser().id(), courseId)
        ));
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<ApiResponse<ProgressResponse>> completeLesson(@PathVariable String lessonId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lesson marked as completed!",
                courseService.completeLesson(SecurityUtils.currentUser().id(), lessonId)
        ));
    }

    @PostMapping("/courses/{courseId}/favorites")
    public ResponseEntity<ApiResponse<CourseResponse>> addFavorite(@PathVariable String courseId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Course added to favorites!",
                courseService.addFavorite(SecurityUtils.currentUser().id(), courseId)
        ));
    }

    @PostMapping("/courses/{courseId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> reviewCourse(
            @PathVariable String courseId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Course reviewed successfully!",
                courseService.reviewCourse(SecurityUtils.currentUser().id(), courseId, request)
        ));
    }

    @DeleteMapping("/courses/{courseId}/enrollment")
    public ResponseEntity<ApiResponse<String>> unenroll(@PathVariable String courseId) {
        courseService.unenroll(SecurityUtils.currentUser().id(), courseId);
        return ResponseEntity.ok(ApiResponse.success("Learner unenrolled successfully!", courseId));
    }
}

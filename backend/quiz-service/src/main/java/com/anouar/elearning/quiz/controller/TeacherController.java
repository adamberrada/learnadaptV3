package com.anouar.elearning.quiz.controller;

import com.anouar.elearning.quiz.dto.*;
import com.anouar.elearning.quiz.security.SecurityUtils;
import com.anouar.elearning.quiz.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/quizzes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(@Valid @RequestBody QuizCreateRequest request) {
        QuizResponse quiz = quizService.createQuiz(SecurityUtils.currentUser().id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Quiz created successfully!", quiz));
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(
            @PathVariable String quizId,
            @Valid @RequestBody QuizUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Quiz updated successfully!",
                quizService.updateQuiz(SecurityUtils.currentUser().id(), quizId, request)
        ));
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<ApiResponse<String>> deleteQuiz(@PathVariable String quizId) {
        quizService.deleteQuiz(SecurityUtils.currentUser().id(), quizId);
        return ResponseEntity.ok(ApiResponse.success("Quiz deleted successfully!", quizId));
    }

    @PostMapping("/{quizId}/chrono")
    public ResponseEntity<ApiResponse<QuizResponse>> setChrono(
            @PathVariable String quizId,
            @Valid @RequestBody ChronoRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Quiz chrono updated successfully!",
                quizService.setChrono(SecurityUtils.currentUser().id(), quizId, request)
        ));
    }

    @PostMapping("/{quizId}/bareme")
    public ResponseEntity<ApiResponse<QuizResponse>> setBareme(
            @PathVariable String quizId,
            @Valid @RequestBody BaremeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Quiz passing score updated successfully!",
                quizService.setBareme(SecurityUtils.currentUser().id(), quizId, request)
        ));
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<ApiResponse<QuestionResponse>> addQuestion(
            @PathVariable String quizId,
            @Valid @RequestBody QuestionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Question added successfully!",
                quizService.addQuestion(SecurityUtils.currentUser().id(), quizId, request)
        ));
    }

    @PostMapping("/{quizId}/ai-generate")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> generateQuestions(
            @PathVariable String quizId,
            @Valid @RequestBody AiGenerateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "AI questions generated successfully!",
                quizService.generateQuestions(SecurityUtils.currentUser().id(), quizId, request)
        ));
    }

    @GetMapping("/course/{courseId}/analytics")
    public ResponseEntity<ApiResponse<CourseAnalyticsResponse>> analyzeCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Course quiz analytics retrieved successfully!",
                quizService.analyzeCourseDifficulties(SecurityUtils.currentUser().id(), courseId)
        ));
    }
}

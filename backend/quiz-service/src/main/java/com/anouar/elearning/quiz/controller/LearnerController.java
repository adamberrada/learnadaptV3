package com.anouar.elearning.quiz.controller;

import com.anouar.elearning.quiz.dto.*;
import com.anouar.elearning.quiz.security.SecurityUtils;
import com.anouar.elearning.quiz.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learner/quizzes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LEARNER')")
public class LearnerController {

    private final QuizService quizService;

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizByChapter(@PathVariable String chapterId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Chapter quiz retrieved successfully!",
                quizService.getPublishedQuizByChapter(SecurityUtils.currentUser().id(), chapterId)
        ));
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<ApiResponse<QuizSubmissionResponse>> submitQuiz(
            @PathVariable String quizId,
            @Valid @RequestBody QuizSubmitRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Quiz submitted and corrected successfully!",
                quizService.submitQuiz(SecurityUtils.currentUser().id(), quizId, request)
        ));
    }

    @GetMapping("/{quizId}/results")
    public ResponseEntity<ApiResponse<List<QuizSubmissionResponse>>> getResults(@PathVariable String quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Learner quiz results retrieved successfully!",
                quizService.getLearnerResults(SecurityUtils.currentUser().id(), quizId)
        ));
    }

    @GetMapping("/{quizId}/remediation")
    public ResponseEntity<ApiResponse<List<AIRecommendationResponse>>> getRemediation(@PathVariable String quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                "AI remediation retrieved successfully!",
                quizService.getRemediation(SecurityUtils.currentUser().id(), quizId)
        ));
    }
}

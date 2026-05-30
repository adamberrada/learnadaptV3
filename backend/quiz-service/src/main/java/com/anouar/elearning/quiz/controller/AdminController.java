package com.anouar.elearning.quiz.controller;

import com.anouar.elearning.quiz.dto.ApiResponse;
import com.anouar.elearning.quiz.dto.GlobalResultsResponse;
import com.anouar.elearning.quiz.dto.QuizResponse;
import com.anouar.elearning.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/quizzes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final QuizService quizService;

    @GetMapping("/global-results")
    public ResponseEntity<ApiResponse<GlobalResultsResponse>> getGlobalResults() {
        return ResponseEntity.ok(ApiResponse.success(
                "Global quiz results retrieved successfully!",
                quizService.getGlobalResults()
        ));
    }

    @PostMapping("/{quizId}/validate")
    public ResponseEntity<ApiResponse<QuizResponse>> validateQuiz(@PathVariable String quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Quiz validated successfully!",
                quizService.validateQuiz(quizId)
        ));
    }

    @DeleteMapping("/{quizId}/inappropriate")
    public ResponseEntity<ApiResponse<String>> deleteInappropriateQuiz(@PathVariable String quizId) {
        quizService.deleteInappropriateQuiz(quizId);
        return ResponseEntity.ok(ApiResponse.success("Inappropriate quiz deleted successfully!", quizId));
    }
}

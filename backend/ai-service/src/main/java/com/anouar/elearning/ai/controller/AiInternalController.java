package com.anouar.elearning.ai.controller;

import com.anouar.elearning.ai.dto.AnalyzeFailuresRequest;
import com.anouar.elearning.ai.dto.ApiResponse;
import com.anouar.elearning.ai.dto.FailureAnalysisReport;
import com.anouar.elearning.ai.dto.GenerateQuizRequest;
import com.anouar.elearning.ai.dto.GenerateRemediationRequest;
import com.anouar.elearning.ai.dto.LearnerRemediationPlan;
import com.anouar.elearning.ai.dto.QuizAiTemplate;
import com.anouar.elearning.ai.service.AiIntegrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/ai")
public class AiInternalController {

    private final AiIntegrationService aiIntegrationService;

    public AiInternalController(AiIntegrationService aiIntegrationService) {
        this.aiIntegrationService = aiIntegrationService;
    }

    @PostMapping("/generate-quiz")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SERVICE')")
    public ResponseEntity<ApiResponse<QuizAiTemplate>> generateQuiz(@Valid @RequestBody GenerateQuizRequest request) {
        QuizAiTemplate quiz = aiIntegrationService.generateQuiz(
                request.courseContent(),
                request.questionCount(),
                request.difficulty()
        );
        return ResponseEntity.ok(ApiResponse.success("Quiz genere avec succes par l'IA.", quiz));
    }

    @PostMapping("/analyze-failures")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SERVICE')")
    public ResponseEntity<ApiResponse<FailureAnalysisReport>> analyzeFailures(
            @Valid @RequestBody AnalyzeFailuresRequest request) {
        FailureAnalysisReport report = aiIntegrationService.analyzeFailures(request.failureData());
        return ResponseEntity.ok(ApiResponse.success("Analyse des difficultes generee avec succes par l'IA.", report));
    }

    @PostMapping("/generate-remediation")
    @PreAuthorize("hasAnyRole('LEARNER', 'TEACHER', 'ADMIN', 'SERVICE')")
    public ResponseEntity<ApiResponse<LearnerRemediationPlan>> generateRemediation(
            @Valid @RequestBody GenerateRemediationRequest request) {
        LearnerRemediationPlan plan = aiIntegrationService.generateRemediation(
                request.wrongAnswers(),
                request.learnerContext()
        );
        return ResponseEntity.ok(ApiResponse.success("Plan de remediation genere avec succes par l'IA.", plan));
    }
}

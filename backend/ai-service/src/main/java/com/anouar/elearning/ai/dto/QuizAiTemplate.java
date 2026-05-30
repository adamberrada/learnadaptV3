package com.anouar.elearning.ai.dto;

import java.util.List;

public record QuizAiTemplate(
        List<QuestionAiResponse> questions
) {
}

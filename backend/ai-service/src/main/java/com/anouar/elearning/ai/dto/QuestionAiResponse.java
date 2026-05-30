package com.anouar.elearning.ai.dto;

import java.util.List;

public record QuestionAiResponse(
        String text,
        int points,
        List<OptionAiResponse> options
) {
}

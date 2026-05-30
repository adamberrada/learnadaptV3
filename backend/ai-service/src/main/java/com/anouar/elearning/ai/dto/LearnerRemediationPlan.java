package com.anouar.elearning.ai.dto;

import java.util.List;

public record LearnerRemediationPlan(
        List<String> detectedDifficulties,
        List<String> recommendedActions
) {
}

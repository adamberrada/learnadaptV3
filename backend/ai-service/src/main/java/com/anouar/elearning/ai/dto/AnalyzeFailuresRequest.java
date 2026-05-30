package com.anouar.elearning.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record AnalyzeFailuresRequest(
        @NotBlank(message = "Failure data is required")
        @JsonAlias({"failureLogs", "errorLogs", "compiledExamFailures"})
        String failureData
) {
}

package com.anouar.elearning.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GenerateRemediationRequest(
        @NotEmpty(message = "Wrong answers are required")
        @JsonAlias({"badAnswers", "incorrectAnswers", "learnerErrors"})
        List<@NotBlank(message = "Wrong answer item must not be blank") String> wrongAnswers,

        @NotBlank(message = "Learner context is required")
        @JsonAlias({"context"})
        String learnerContext
) {
}

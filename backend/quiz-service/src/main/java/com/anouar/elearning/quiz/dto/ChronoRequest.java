package com.anouar.elearning.quiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChronoRequest {

    @NotNull(message = "Time limit is required")
    @Min(value = 1, message = "Time limit must be positive")
    private Integer timeLimitInMinutes;
}

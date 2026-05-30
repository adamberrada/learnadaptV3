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
public class BaremeRequest {

    @NotNull(message = "Passing score is required")
    @Min(value = 0, message = "Passing score must be greater than or equal to 0")
    private Integer passingScore;
}

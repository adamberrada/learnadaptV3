package com.anouar.elearning.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequest {

    @NotBlank(message = "Question text is required")
    private String text;

    @NotNull(message = "Points are required")
    @Min(value = 1, message = "Question points must be positive")
    private Integer points;

    @NotNull(message = "Order index is required")
    @Min(value = 0, message = "Order index must be greater than or equal to 0")
    private Integer orderIndex;

    @Valid
    @NotEmpty(message = "At least one option is required")
    private List<OptionRequest> options;
}

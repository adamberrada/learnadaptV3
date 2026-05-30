package com.anouar.elearning.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionRequest {

    @NotBlank(message = "Option text is required")
    private String text;

    private boolean correct;
}

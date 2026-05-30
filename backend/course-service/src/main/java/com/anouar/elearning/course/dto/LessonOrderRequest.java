package com.anouar.elearning.course.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonOrderRequest {

    @Valid
    @NotEmpty(message = "Lessons order is required")
    private List<LessonOrderItem> lessons;
}

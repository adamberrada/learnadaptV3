package com.anouar.elearning.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonOrderItem {

    @NotBlank(message = "Lesson id is required")
    private String lessonId;

    @NotNull(message = "Order index is required")
    @Min(value = 0, message = "Order index must be greater than or equal to 0")
    private Integer orderIndex;
}

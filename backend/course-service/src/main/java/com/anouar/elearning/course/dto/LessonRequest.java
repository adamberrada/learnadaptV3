package com.anouar.elearning.course.dto;

import com.anouar.elearning.course.entity.LessonType;
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
public class LessonRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Lesson type is required")
    private LessonType type;

    private String videoUrl;
    private String externalUrl;
    private String textContent;

    @NotNull(message = "Order index is required")
    @Min(value = 0, message = "Order index must be greater than or equal to 0")
    private Integer orderIndex;
}

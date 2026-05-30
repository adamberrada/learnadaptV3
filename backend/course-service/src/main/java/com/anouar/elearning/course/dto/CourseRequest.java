package com.anouar.elearning.course.dto;

import com.anouar.elearning.course.entity.CourseLevel;
import com.anouar.elearning.course.entity.CourseStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String categoryId;

    private String subCategoryId;

    private String thumbnailUrl;

    @NotNull(message = "Level is required")
    private CourseLevel level;

    private CourseStatus status;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @Min(value = 0, message = "Duration must be greater than or equal to 0")
    private Integer durationInMinutes;

    private java.util.Set<String> tags;
}

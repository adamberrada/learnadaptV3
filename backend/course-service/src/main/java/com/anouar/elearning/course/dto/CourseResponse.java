package com.anouar.elearning.course.dto;

import com.anouar.elearning.course.entity.CourseLevel;
import com.anouar.elearning.course.entity.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private String id;
    private String title;
    private String description;
    private CategoryResponse category;
    private SubCategoryResponse subCategory;
    private String instructorId;
    private String thumbnailUrl;
    private CourseLevel level;
    private CourseStatus status;
    private BigDecimal price;
    private Integer durationInMinutes;
    private java.util.Set<String> tags;
    private java.util.List<ChapterResponse> chapters;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

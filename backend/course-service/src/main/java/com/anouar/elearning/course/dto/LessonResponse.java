package com.anouar.elearning.course.dto;

import com.anouar.elearning.course.entity.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {
    private String id;
    private String title;
    private LessonType type;
    private String videoUrl;
    private String externalUrl;
    private String textContent;
    private Integer orderIndex;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.anouar.elearning.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterResponse {
    private String id;
    private String title;
    private String description;
    private Integer orderIndex;
    private List<LessonResponse> lessons;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

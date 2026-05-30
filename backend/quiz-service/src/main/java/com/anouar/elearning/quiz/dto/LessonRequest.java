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
public class LessonRequest {
    @NotBlank
    private String courseId;
    @NotBlank
    private String chapterId;
    @NotBlank
    private String title;
    private String content;
}

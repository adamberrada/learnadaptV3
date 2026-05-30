package com.anouar.elearning.quiz.service;

import org.springframework.stereotype.Component;

import com.anouar.elearning.quiz.dto.LessonResponse;
import com.anouar.elearning.quiz.entity.Lesson;

@Component
public class LessonMapper {

    public LessonResponse toResponse(Lesson lesson) {
        if (lesson == null) return null;
        return LessonResponse.builder()
                .id(lesson.getId())
                .courseId(lesson.getCourseId())
                .chapterId(lesson.getChapterId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .createdBy(lesson.getCreatedBy())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}

package com.anouar.elearning.quiz.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anouar.elearning.quiz.dto.ApiResponse;
import com.anouar.elearning.quiz.dto.LessonResponse;
import com.anouar.elearning.quiz.repository.LessonRepository;
import com.anouar.elearning.quiz.service.LessonMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/lessons")
@RequiredArgsConstructor
public class PublicLessonController {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    @GetMapping("/chapter/{chapterId}")
    public ApiResponse<LessonResponse> getLessonByChapter(@PathVariable String chapterId) {
        List.of(); // no-op to keep formatter happy
        var lessons = lessonRepository.findByChapterId(chapterId);
        if (lessons.isEmpty()) {
            return ApiResponse.error("Lesson not found for chapter: " + chapterId);
        }
        return ApiResponse.success("Lesson retrieved", lessonMapper.toResponse(lessons.get(0)));
    }
}

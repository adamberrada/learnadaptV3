package com.anouar.elearning.quiz.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anouar.elearning.quiz.dto.ApiResponse;
import com.anouar.elearning.quiz.dto.LessonRequest;
import com.anouar.elearning.quiz.dto.LessonResponse;
import com.anouar.elearning.quiz.entity.Lesson;
import com.anouar.elearning.quiz.repository.LessonRepository;
import com.anouar.elearning.quiz.security.SecurityUtils;
import com.anouar.elearning.quiz.service.LessonMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teacher/lessons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherLessonController {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(@Valid @RequestBody LessonRequest request) {
        Lesson lesson = Lesson.builder()
                .courseId(request.getCourseId())
                .chapterId(request.getChapterId())
                .title(request.getTitle())
                .content(request.getContent())
                .createdBy(SecurityUtils.currentUser().id())
                .build();
        Lesson saved = lessonRepository.save(lesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Lesson created", lessonMapper.toResponse(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(@PathVariable String id, @Valid @RequestBody LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new com.anouar.elearning.quiz.exception.ResourceNotFoundException("Lesson not found: " + id));
        if (!lesson.getCreatedBy().equals(SecurityUtils.currentUser().id())) {
            throw new com.anouar.elearning.quiz.exception.ForbiddenException("You can only edit lessons you created");
        }
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setCourseId(request.getCourseId());
        lesson.setChapterId(request.getChapterId());
        Lesson saved = lessonRepository.save(lesson);
        return ResponseEntity.ok(ApiResponse.success("Lesson updated", lessonMapper.toResponse(saved)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LessonResponse>>> listLessons() {
        List<LessonResponse> list = lessonRepository.findAll().stream().map(lessonMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Lessons retrieved", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLesson(@PathVariable String id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new com.anouar.elearning.quiz.exception.ResourceNotFoundException("Lesson not found: " + id));
        return ResponseEntity.ok(ApiResponse.success("Lesson retrieved", lessonMapper.toResponse(lesson)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteLesson(@PathVariable String id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new com.anouar.elearning.quiz.exception.ResourceNotFoundException("Lesson not found: " + id));
        if (!lesson.getCreatedBy().equals(SecurityUtils.currentUser().id())) {
            throw new com.anouar.elearning.quiz.exception.ForbiddenException("You can only delete lessons you created");
        }
        lessonRepository.delete(lesson);
        return ResponseEntity.ok(ApiResponse.success("Lesson deleted", id));
    }
}

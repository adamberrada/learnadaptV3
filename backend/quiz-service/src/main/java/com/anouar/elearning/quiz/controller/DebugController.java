package com.anouar.elearning.quiz.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anouar.elearning.quiz.dto.QuizResponse;
import com.anouar.elearning.quiz.repository.QuizRepository;
import com.anouar.elearning.quiz.service.QuizMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    @GetMapping("/quizzes")
    public ResponseEntity<List<QuizResponse>> listAllQuizzes() {
        List<QuizResponse> responses = quizRepository.findAll().stream()
                .map(q -> quizMapper.toQuizResponse(q, false))
                .toList();
        return ResponseEntity.ok(responses);
    }
}

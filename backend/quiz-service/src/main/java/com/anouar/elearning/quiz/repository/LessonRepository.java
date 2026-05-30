package com.anouar.elearning.quiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anouar.elearning.quiz.entity.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, String> {
    List<Lesson> findByCourseId(String courseId);
    List<Lesson> findByChapterId(String chapterId);
}

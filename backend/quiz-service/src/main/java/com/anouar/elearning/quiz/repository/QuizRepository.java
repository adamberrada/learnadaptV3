package com.anouar.elearning.quiz.repository;

import com.anouar.elearning.quiz.entity.Quiz;
import com.anouar.elearning.quiz.entity.QuizStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, String> {
    List<Quiz> findByCreatedBy(String createdBy);

    List<Quiz> findByCourseId(String courseId);

    List<Quiz> findByCourseIdAndCreatedBy(String courseId, String createdBy);

    Optional<Quiz> findFirstByChapterIdAndStatus(String chapterId, QuizStatus status);
}

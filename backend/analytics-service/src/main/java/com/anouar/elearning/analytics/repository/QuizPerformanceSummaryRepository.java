package com.anouar.elearning.analytics.repository;

import com.anouar.elearning.analytics.entity.QuizPerformanceSummary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizPerformanceSummaryRepository extends JpaRepository<QuizPerformanceSummary, Long> {
    List<QuizPerformanceSummary> findByCourseId(String courseId);
    Optional<QuizPerformanceSummary> findByQuizId(String quizId);
}

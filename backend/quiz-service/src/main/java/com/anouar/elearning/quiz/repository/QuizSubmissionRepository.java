package com.anouar.elearning.quiz.repository;

import com.anouar.elearning.quiz.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, String> {
    List<QuizSubmission> findByQuizId(String quizId);

    List<QuizSubmission> findByLearnerIdAndQuizIdOrderBySubmittedAtDesc(String learnerId, String quizId);

    List<QuizSubmission> findByQuizCourseId(String courseId);
}

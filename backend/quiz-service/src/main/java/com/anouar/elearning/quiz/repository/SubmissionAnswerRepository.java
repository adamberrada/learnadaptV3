package com.anouar.elearning.quiz.repository;

import com.anouar.elearning.quiz.entity.SubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, String> {
    List<SubmissionAnswer> findBySubmissionQuizCourseId(String courseId);

    List<SubmissionAnswer> findBySubmissionQuizId(String quizId);
}

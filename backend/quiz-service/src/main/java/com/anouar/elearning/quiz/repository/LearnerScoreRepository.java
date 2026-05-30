package com.anouar.elearning.quiz.repository;

import com.anouar.elearning.quiz.entity.LearnerScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearnerScoreRepository extends JpaRepository<LearnerScore, String> {
    Optional<LearnerScore> findByLearnerIdAndQuizId(String learnerId, String quizId);
}

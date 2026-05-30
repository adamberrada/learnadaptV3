package com.anouar.elearning.quiz.repository;

import com.anouar.elearning.quiz.entity.AIRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, String> {
    List<AIRecommendation> findByLearnerIdAndQuizIdOrderByCreatedAtDesc(String learnerId, String quizId);
}

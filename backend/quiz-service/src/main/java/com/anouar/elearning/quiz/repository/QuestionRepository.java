package com.anouar.elearning.quiz.repository;

import com.anouar.elearning.quiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    List<Question> findByQuizIdOrderByOrderIndexAsc(String quizId);
}

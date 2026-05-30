package com.anouar.elearning.quiz.service;

import com.anouar.elearning.quiz.dto.*;

import java.util.List;

public interface QuizService {
    QuizResponse createQuiz(String teacherId, QuizCreateRequest request);

    QuizResponse updateQuiz(String teacherId, String quizId, QuizUpdateRequest request);

    void deleteQuiz(String teacherId, String quizId);

    QuizResponse setChrono(String teacherId, String quizId, ChronoRequest request);

    QuizResponse setBareme(String teacherId, String quizId, BaremeRequest request);

    QuestionResponse addQuestion(String teacherId, String quizId, QuestionRequest request);

    List<QuestionResponse> generateQuestions(String teacherId, String quizId, AiGenerateRequest request);

    CourseAnalyticsResponse analyzeCourseDifficulties(String teacherId, String courseId);

    QuizResponse getPublishedQuizByChapter(String learnerId, String chapterId);

    QuizSubmissionResponse submitQuiz(String learnerId, String quizId, QuizSubmitRequest request);

    List<QuizSubmissionResponse> getLearnerResults(String learnerId, String quizId);

    List<AIRecommendationResponse> getRemediation(String learnerId, String quizId);

    GlobalResultsResponse getGlobalResults();

    QuizResponse validateQuiz(String quizId);

    void deleteInappropriateQuiz(String quizId);
}

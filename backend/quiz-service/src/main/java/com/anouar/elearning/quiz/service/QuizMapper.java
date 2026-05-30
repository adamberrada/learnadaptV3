package com.anouar.elearning.quiz.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.anouar.elearning.quiz.dto.AIRecommendationResponse;
import com.anouar.elearning.quiz.dto.OptionResponse;
import com.anouar.elearning.quiz.dto.QuestionResponse;
import com.anouar.elearning.quiz.dto.QuizResponse;
import com.anouar.elearning.quiz.dto.QuizSubmissionResponse;
import com.anouar.elearning.quiz.dto.SubmissionAnswerResponse;
import com.anouar.elearning.quiz.entity.AIRecommendation;
import com.anouar.elearning.quiz.entity.AnswerOption;
import com.anouar.elearning.quiz.entity.Question;
import com.anouar.elearning.quiz.entity.Quiz;
import com.anouar.elearning.quiz.entity.QuizSubmission;
import com.anouar.elearning.quiz.entity.SubmissionAnswer;

@Component
public class QuizMapper {

    @org.springframework.beans.factory.annotation.Autowired
    private LessonMapper lessonMapper;

    public QuizResponse toQuizResponse(Quiz quiz, boolean includeCorrectAnswers) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .courseId(quiz.getCourseId())
                .chapterId(quiz.getChapterId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimitInMinutes(quiz.getTimeLimitInMinutes())
                .passingScore(quiz.getPassingScore())
                .totalPoints(totalPoints(quiz))
                .status(quiz.getStatus())
                .createdBy(quiz.getCreatedBy())
                .questions(toQuestionResponses(quiz.getQuestions(), includeCorrectAnswers))
                .lesson(lessonMapper.toResponse(quiz.getLesson()))
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }

    public QuestionResponse toQuestionResponse(Question question, boolean includeCorrectAnswers) {
        return QuestionResponse.builder()
                .id(question.getId())
                .text(question.getText())
                .points(question.getPoints())
                .orderIndex(question.getOrderIndex())
                .options(toOptionResponses(question.getOptions(), includeCorrectAnswers))
                .build();
    }

    public OptionResponse toOptionResponse(AnswerOption option, boolean includeCorrectAnswers) {
        return OptionResponse.builder()
                .id(option.getId())
                .text(option.getText())
                .correct(includeCorrectAnswers ? option.isCorrect() : null)
                .build();
    }

    public QuizSubmissionResponse toSubmissionResponse(QuizSubmission submission) {
        return QuizSubmissionResponse.builder()
                .id(submission.getId())
                .quizId(submission.getQuiz().getId())
                .learnerId(submission.getLearnerId())
                .scoreObtained(submission.getScoreObtained())
                .maxScore(submission.getMaxScore())
                .passingScore(submission.getQuiz().getPassingScore())
                .passed(submission.isPassed())
                .answers(toSubmissionAnswerResponses(submission.getAnswers()))
                .submittedAt(submission.getSubmittedAt())
                .build();
    }

    public AIRecommendationResponse toRecommendationResponse(AIRecommendation recommendation) {
        return AIRecommendationResponse.builder()
                .id(recommendation.getId())
                .learnerId(recommendation.getLearnerId())
                .quizId(recommendation.getQuizId())
                .detectedDifficulties(recommendation.getDetectedDifficulties())
                .recommendedLessons(new ArrayList<>(recommendation.getRecommendedLessons()))
                .createdAt(recommendation.getCreatedAt())
                .build();
    }

    public List<QuestionResponse> toQuestionResponses(List<Question> questions, boolean includeCorrectAnswers) {
        return questions.stream().map(question -> toQuestionResponse(question, includeCorrectAnswers)).toList();
    }

    public List<OptionResponse> toOptionResponses(List<AnswerOption> options, boolean includeCorrectAnswers) {
        return options.stream().map(option -> toOptionResponse(option, includeCorrectAnswers)).toList();
    }

    public List<SubmissionAnswerResponse> toSubmissionAnswerResponses(List<SubmissionAnswer> answers) {
        return answers.stream()
                .map(answer -> SubmissionAnswerResponse.builder()
                        .questionId(answer.getQuestion().getId())
                        .questionText(answer.getQuestion().getText())
                        .selectedOptionId(answer.getSelectedOption().getId())
                        .selectedOptionText(answer.getSelectedOption().getText())
                        .correct(answer.isCorrect())
                        .pointsAwarded(answer.getPointsAwarded())
                        .questionPoints(answer.getQuestion().getPoints())
                        .build())
                .toList();
    }

    public int totalPoints(Quiz quiz) {
        return quiz.getQuestions().stream().mapToInt(Question::getPoints).sum();
    }
}

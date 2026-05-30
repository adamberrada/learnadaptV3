package com.anouar.elearning.quiz.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anouar.elearning.quiz.dto.AIRecommendationResponse;
import com.anouar.elearning.quiz.dto.AiGenerateRequest;
import com.anouar.elearning.quiz.dto.AnswerRequest;
import com.anouar.elearning.quiz.dto.BaremeRequest;
import com.anouar.elearning.quiz.dto.ChronoRequest;
import com.anouar.elearning.quiz.dto.CourseAnalyticsResponse;
import com.anouar.elearning.quiz.dto.GlobalResultsResponse;
import com.anouar.elearning.quiz.dto.OptionRequest;
import com.anouar.elearning.quiz.dto.QuestionDifficultyResponse;
import com.anouar.elearning.quiz.dto.QuestionRequest;
import com.anouar.elearning.quiz.dto.QuestionResponse;
import com.anouar.elearning.quiz.dto.QuizCreateRequest;
import com.anouar.elearning.quiz.dto.QuizResponse;
import com.anouar.elearning.quiz.dto.QuizSubmissionResponse;
import com.anouar.elearning.quiz.dto.QuizSubmitRequest;
import com.anouar.elearning.quiz.dto.QuizUpdateRequest;
import com.anouar.elearning.quiz.entity.AIRecommendation;
import com.anouar.elearning.quiz.entity.AnswerOption;
import com.anouar.elearning.quiz.entity.LearnerScore;
import com.anouar.elearning.quiz.entity.Lesson;
import com.anouar.elearning.quiz.entity.Question;
import com.anouar.elearning.quiz.entity.Quiz;
import com.anouar.elearning.quiz.entity.QuizStatus;
import com.anouar.elearning.quiz.entity.QuizSubmission;
import com.anouar.elearning.quiz.entity.SubmissionAnswer;
import com.anouar.elearning.quiz.exception.BusinessException;
import com.anouar.elearning.quiz.exception.ForbiddenException;
import com.anouar.elearning.quiz.exception.ResourceNotFoundException;
import com.anouar.elearning.quiz.repository.AIRecommendationRepository;
import com.anouar.elearning.quiz.repository.AnswerOptionRepository;
import com.anouar.elearning.quiz.repository.LearnerScoreRepository;
import com.anouar.elearning.quiz.repository.LessonRepository;
import com.anouar.elearning.quiz.repository.QuestionRepository;
import com.anouar.elearning.quiz.repository.QuizRepository;
import com.anouar.elearning.quiz.repository.QuizSubmissionRepository;
import com.anouar.elearning.quiz.repository.SubmissionAnswerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final LessonRepository lessonRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository optionRepository;
    private final QuizSubmissionRepository submissionRepository;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    private final LearnerScoreRepository learnerScoreRepository;
    private final AIRecommendationRepository recommendationRepository;
    private final QuizMapper mapper;

    @Override
    @Transactional
    public QuizResponse createQuiz(String teacherId, QuizCreateRequest request) {
        Quiz quiz = Quiz.builder()
                .courseId(request.getCourseId())
                .chapterId(request.getChapterId())
                .title(request.getTitle())
                .description(request.getDescription())
                .timeLimitInMinutes(request.getTimeLimitInMinutes())
                .passingScore(request.getPassingScore())
                .status(QuizStatus.DRAFT)
                .createdBy(teacherId)
                .build();
        if (request.getLessonId() != null && !request.getLessonId().isBlank()) {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + request.getLessonId()));
            quiz.setLesson(lesson);
        }
        return mapper.toQuizResponse(quizRepository.save(quiz), true);
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(String teacherId, String quizId, QuizUpdateRequest request) {
        Quiz quiz = findOwnedQuiz(teacherId, quizId);
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        if (request.getLessonId() != null) {
            if (request.getLessonId().isBlank()) {
                quiz.setLesson(null);
            } else {
                Lesson lesson = lessonRepository.findById(request.getLessonId())
                        .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + request.getLessonId()));
                quiz.setLesson(lesson);
            }
        }
        return mapper.toQuizResponse(quizRepository.save(quiz), true);
    }

    @Override
    @Transactional
    public void deleteQuiz(String teacherId, String quizId) {
        quizRepository.delete(findOwnedQuiz(teacherId, quizId));
    }

    @Override
    @Transactional
    public QuizResponse setChrono(String teacherId, String quizId, ChronoRequest request) {
        Quiz quiz = findOwnedQuiz(teacherId, quizId);
        quiz.setTimeLimitInMinutes(request.getTimeLimitInMinutes());
        return mapper.toQuizResponse(quizRepository.save(quiz), true);
    }

    @Override
    @Transactional
    public QuizResponse setBareme(String teacherId, String quizId, BaremeRequest request) {
        Quiz quiz = findOwnedQuiz(teacherId, quizId);
        validatePassingScore(request.getPassingScore(), mapper.totalPoints(quiz));
        quiz.setPassingScore(request.getPassingScore());
        return mapper.toQuizResponse(quizRepository.save(quiz), true);
    }

    @Override
    @Transactional
    public QuestionResponse addQuestion(String teacherId, String quizId, QuestionRequest request) {
        Quiz quiz = findOwnedQuiz(teacherId, quizId);
        validateQuestionRequest(request);

        Question question = Question.builder()
                .quiz(quiz)
                .text(request.getText())
                .points(request.getPoints())
                .orderIndex(request.getOrderIndex())
                .build();

        Question savedQuestion = questionRepository.save(question);
        List<AnswerOption> options = request.getOptions().stream()
                .map(option -> AnswerOption.builder()
                        .question(savedQuestion)
                        .text(option.getText())
                        .correct(option.isCorrect())
                        .build())
                .toList();
        savedQuestion.setOptions(optionRepository.saveAll(options));
        quiz.getQuestions().add(savedQuestion);
        validatePassingScore(quiz.getPassingScore(), mapper.totalPoints(quiz));
        return mapper.toQuestionResponse(savedQuestion, true);
    }

    @Override
    @Transactional
    public List<QuestionResponse> generateQuestions(String teacherId, String quizId, AiGenerateRequest request) {
        Quiz quiz = findOwnedQuiz(teacherId, quizId);
        int points = request.getPointsPerQuestion() == null ? 1 : request.getPointsPerQuestion();
        int startIndex = quiz.getQuestions().size();
        List<QuestionResponse> generated = new ArrayList<>();

        for (int i = 0; i < request.getQuestionCount(); i++) {
            QuestionRequest questionRequest = QuestionRequest.builder()
                    .text("Question IA " + (i + 1) + " - " + summarizeContext(request.getCourseContext()))
                    .points(points)
                    .orderIndex(startIndex + i)
                    .options(List.of(
                            OptionRequest.builder().text("Bonne reponse generee").correct(true).build(),
                            OptionRequest.builder().text("Distracteur A").correct(false).build(),
                            OptionRequest.builder().text("Distracteur B").correct(false).build(),
                            OptionRequest.builder().text("Distracteur C").correct(false).build()
                    ))
                    .build();
            generated.add(addQuestion(teacherId, quizId, questionRequest));
        }

        return generated;
    }

    @Override
    public CourseAnalyticsResponse analyzeCourseDifficulties(String teacherId, String courseId) {
        List<Quiz> quizzes = quizRepository.findByCourseIdAndCreatedBy(courseId, teacherId);
        Set<String> quizIds = quizzes.stream().map(Quiz::getId).collect(Collectors.toSet());
        List<QuizSubmission> submissions = submissionRepository.findByQuizCourseId(courseId).stream()
                .filter(submission -> quizIds.contains(submission.getQuiz().getId()))
                .toList();
        List<SubmissionAnswer> answers = submissionAnswerRepository.findBySubmissionQuizCourseId(courseId).stream()
                .filter(answer -> quizIds.contains(answer.getSubmission().getQuiz().getId()))
                .toList();

        return buildCourseAnalytics(courseId, quizzes.size(), submissions, answers);
    }

    @Override
    public QuizResponse getPublishedQuizByChapter(String learnerId, String chapterId) {
        Quiz quiz = quizRepository.findFirstByChapterIdAndStatus(chapterId, QuizStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Published quiz not found for chapter: " + chapterId));
        return mapper.toQuizResponse(quiz, false);
    }

    @Override
    @Transactional
    public QuizSubmissionResponse submitQuiz(String learnerId, String quizId, QuizSubmitRequest request) {
        Quiz quiz = findQuiz(quizId);
        if (quiz.getStatus() != QuizStatus.PUBLISHED) {
            throw new BusinessException("Only published quizzes can be submitted");
        }

        Map<String, AnswerRequest> answersByQuestion = request.getAnswers().stream()
                .collect(Collectors.toMap(AnswerRequest::getQuestionId, Function.identity(), (first, second) -> second));

        QuizSubmission submission = QuizSubmission.builder()
                .quiz(quiz)
                .learnerId(learnerId)
                .scoreObtained(0)
                .maxScore(mapper.totalPoints(quiz))
                .passed(false)
                .build();
        QuizSubmission savedSubmission = submissionRepository.save(submission);

        int score = 0;
        List<SubmissionAnswer> submissionAnswers = new ArrayList<>();
        List<Question> missedQuestions = new ArrayList<>();

        for (Question question : quiz.getQuestions()) {
            AnswerRequest answerRequest = answersByQuestion.get(question.getId());
            if (answerRequest == null) {
                missedQuestions.add(question);
                continue;
            }

            AnswerOption selectedOption = question.getOptions().stream()
                    .filter(option -> option.getId().equals(answerRequest.getOptionId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("Selected option does not belong to question: " + question.getId()));

            boolean correct = selectedOption.isCorrect();
            int pointsAwarded = correct ? question.getPoints() : 0;
            if (correct) {
                score += question.getPoints();
            } else {
                missedQuestions.add(question);
            }

            submissionAnswers.add(SubmissionAnswer.builder()
                    .submission(savedSubmission)
                    .question(question)
                    .selectedOption(selectedOption)
                    .correct(correct)
                    .pointsAwarded(pointsAwarded)
                    .build());
        }

        savedSubmission.setScoreObtained(score);
        savedSubmission.setPassed(score >= quiz.getPassingScore());
        savedSubmission.setAnswers(submissionAnswerRepository.saveAll(submissionAnswers));
        QuizSubmission finalSubmission = submissionRepository.save(savedSubmission);
        updateLearnerScore(learnerId, quiz, finalSubmission);

        if (!missedQuestions.isEmpty() || !finalSubmission.isPassed()) {
            generateRemediation(learnerId, quiz, missedQuestions);
        }

        return mapper.toSubmissionResponse(finalSubmission);
    }

    @Override
    public List<QuizSubmissionResponse> getLearnerResults(String learnerId, String quizId) {
        return submissionRepository.findByLearnerIdAndQuizIdOrderBySubmittedAtDesc(learnerId, quizId)
                .stream()
                .map(mapper::toSubmissionResponse)
                .toList();
    }

    @Override
    public List<AIRecommendationResponse> getRemediation(String learnerId, String quizId) {
        return recommendationRepository.findByLearnerIdAndQuizIdOrderByCreatedAtDesc(learnerId, quizId)
                .stream()
                .map(mapper::toRecommendationResponse)
                .toList();
    }

    @Override
    public GlobalResultsResponse getGlobalResults() {
        List<Quiz> quizzes = quizRepository.findAll();
        List<QuizSubmission> submissions = submissionRepository.findAll();
        double averageScore = submissions.stream().mapToInt(QuizSubmission::getScoreObtained).average().orElse(0);
        double passRate = submissions.isEmpty() ? 0 : submissions.stream().filter(QuizSubmission::isPassed).count() * 100.0 / submissions.size();
        return GlobalResultsResponse.builder()
                .quizCount(quizzes.size())
                .submissionCount(submissions.size())
                .averageScore(averageScore)
                .passRate(passRate)
                .build();
    }

    @Override
    @Transactional
    public QuizResponse validateQuiz(String quizId) {
        Quiz quiz = findQuiz(quizId);
        if (quiz.getQuestions().isEmpty()) {
            throw new BusinessException("A quiz must contain at least one question before validation");
        }
        validatePassingScore(quiz.getPassingScore(), mapper.totalPoints(quiz));
        quiz.setStatus(QuizStatus.PUBLISHED);
        return mapper.toQuizResponse(quizRepository.save(quiz), true);
    }

    @Override
    @Transactional
    public void deleteInappropriateQuiz(String quizId) {
        quizRepository.delete(findQuiz(quizId));
    }

    private void updateLearnerScore(String learnerId, Quiz quiz, QuizSubmission submission) {
        LearnerScore score = learnerScoreRepository.findByLearnerIdAndQuizId(learnerId, quiz.getId())
                .orElse(LearnerScore.builder()
                        .learnerId(learnerId)
                        .quiz(quiz)
                        .bestScore(0)
                        .attempts(0)
                        .passed(false)
                        .build());
        score.setAttempts(score.getAttempts() + 1);
        score.setBestScore(Math.max(score.getBestScore(), submission.getScoreObtained()));
        score.setPassed(score.isPassed() || submission.isPassed());
        score.setLastSubmittedAt(LocalDateTime.now());
        learnerScoreRepository.save(score);
    }

    private void generateRemediation(String learnerId, Quiz quiz, List<Question> missedQuestions) {
        String difficulties = missedQuestions.isEmpty()
                ? "Score below passing threshold despite no directly missed question."
                : missedQuestions.stream().map(Question::getText).collect(Collectors.joining(" | "));
        List<String> recommendations = missedQuestions.stream()
                .map(question -> "Reviser la notion liee a: " + question.getText())
                .distinct()
                .toList();

        recommendationRepository.save(AIRecommendation.builder()
                .learnerId(learnerId)
                .quizId(quiz.getId())
                .detectedDifficulties(difficulties)
                .recommendedLessons(recommendations.isEmpty() ? List.of("Revoir le chapitre associe au quiz") : recommendations)
                .build());
    }

    private CourseAnalyticsResponse buildCourseAnalytics(
            String courseId,
            long quizCount,
            List<QuizSubmission> submissions,
            List<SubmissionAnswer> answers
    ) {
        double averageScore = submissions.stream().mapToInt(QuizSubmission::getScoreObtained).average().orElse(0);
        double passRate = submissions.isEmpty() ? 0 : submissions.stream().filter(QuizSubmission::isPassed).count() * 100.0 / submissions.size();

        Map<String, List<SubmissionAnswer>> byQuestion = answers.stream()
                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getId()));
        List<QuestionDifficultyResponse> difficultQuestions = byQuestion.values().stream()
                .map(questionAnswers -> {
                    SubmissionAnswer first = questionAnswers.getFirst();
                    long total = questionAnswers.size();
                    long failed = questionAnswers.stream().filter(answer -> !answer.isCorrect()).count();
                    return QuestionDifficultyResponse.builder()
                            .questionId(first.getQuestion().getId())
                            .questionText(first.getQuestion().getText())
                            .totalAttempts(total)
                            .failedAttempts(failed)
                            .failureRate(total == 0 ? 0 : failed * 100.0 / total)
                            .build();
                })
                .sorted(Comparator.comparingDouble(QuestionDifficultyResponse::getFailureRate).reversed())
                .toList();

        return CourseAnalyticsResponse.builder()
                .courseId(courseId)
                .quizCount(quizCount)
                .submissionCount(submissions.size())
                .averageScore(averageScore)
                .passRate(passRate)
                .difficultQuestions(difficultQuestions)
                .build();
    }

    private void validateQuestionRequest(QuestionRequest request) {
        long correctOptions = request.getOptions().stream().filter(OptionRequest::isCorrect).count();
        if (correctOptions != 1) {
            throw new BusinessException("A question must have exactly one correct option");
        }
    }

    private void validatePassingScore(Integer passingScore, Integer totalPoints) {
        if (passingScore < 0) {
            throw new BusinessException("Passing score must be greater than or equal to 0");
        }
        if (totalPoints > 0 && passingScore > totalPoints) {
            throw new BusinessException("Passing score cannot exceed total quiz points: " + totalPoints);
        }
    }

    private Quiz findOwnedQuiz(String teacherId, String quizId) {
        Quiz quiz = findQuiz(quizId);
        if (!quiz.getCreatedBy().equals(teacherId)) {
            throw new ForbiddenException("You can only manage quizzes you created");
        }
        return quiz;
    }

    private Quiz findQuiz(String quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
    }

    private String summarizeContext(String context) {
        String value = context.trim();
        return value.length() <= 80 ? value : value.substring(0, 80);
    }
}

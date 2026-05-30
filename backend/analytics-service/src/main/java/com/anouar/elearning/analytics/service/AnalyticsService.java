package com.anouar.elearning.analytics.service;

import com.anouar.elearning.analytics.dto.*;
import com.anouar.elearning.analytics.entity.CourseMetric;
import com.anouar.elearning.analytics.entity.LearnerDailyActivity;
import com.anouar.elearning.analytics.entity.QuizPerformanceSummary;
import com.anouar.elearning.analytics.exception.NotFoundException;
import com.anouar.elearning.analytics.repository.CourseMetricRepository;
import com.anouar.elearning.analytics.repository.LearnerDailyActivityRepository;
import com.anouar.elearning.analytics.repository.QuizPerformanceSummaryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final CourseMetricRepository courseRepo;
    private final LearnerDailyActivityRepository learnerRepo;
    private final QuizPerformanceSummaryRepository quizRepo;

    public LearnerDashboardDTO learnerDashboard(String learnerId) {
        List<LearnerDailyActivity> acts = learnerRepo.findByLearnerIdOrderByActivityDateDesc(learnerId);
        long totalTime = acts.stream().mapToLong(LearnerDailyActivity::getTimeSpentInMinutes).sum();
        int lessons = acts.stream().mapToInt(LearnerDailyActivity::getLessonsCompletedCount).sum();
        long days = acts.stream().map(LearnerDailyActivity::getActivityDate).distinct().count();
        double engagement = days == 0 ? 0.0 : (double) lessons / days;
        List<String> recentQuiz = acts.stream().limit(5).map(a -> "quizzesTaken=" + a.getQuizTakenCount() + "@" + a.getActivityDate()).toList();
        return new LearnerDashboardDTO(learnerId, totalTime, lessons, recentQuiz, engagement, List.of("Revoir les quiz en echec", "Completer 2 lecons cette semaine"));
    }

    public TeacherDashboardDTO teacherDashboard(String teacherId) {
        List<CourseMetric> courses = courseRepo.findByTeacherId(teacherId);
        BigDecimal totalRevenue = courses.stream().map(CourseMetric::getTotalRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalEnrollments = courses.stream().mapToLong(CourseMetric::getTotalEnrollments).sum();
        String mostPopular = courses.stream().max(Comparator.comparingLong(CourseMetric::getTotalEnrollments)).map(CourseMetric::getTitle).orElse("N/A");
        Set<String> courseIds = new HashSet<>(courses.stream().map(CourseMetric::getCourseId).toList());
        double avgQuiz = quizRepo.findAll().stream().filter(q -> courseIds.contains(q.getCourseId())).mapToDouble(QuizPerformanceSummary::getGlobalSuccessRate).average().orElse(0.0);
        List<String> atRisk = learnerRepo.findAll().stream()
                .collect(HashMap<String, LocalDate>::new, (m, a) -> m.merge(a.getLearnerId(), a.getActivityDate(), (d1, d2) -> d1.isAfter(d2) ? d1 : d2), HashMap::putAll)
                .entrySet().stream().filter(e -> e.getValue().isBefore(LocalDate.now().minusDays(14))).map(Map.Entry::getKey).toList();
        return new TeacherDashboardDTO(teacherId, totalRevenue, totalEnrollments, mostPopular, avgQuiz, atRisk);
    }

    public Map<String, Object> studentsByCourse(String courseId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> items = learnerRepo.findAll().stream().collect(HashMap<String, List<LearnerDailyActivity>>::new,
                        (m, a) -> m.computeIfAbsent(a.getLearnerId(), k -> new ArrayList<>()).add(a), HashMap::putAll)
                .entrySet().stream().map(e -> {
                    int lessons = e.getValue().stream().mapToInt(LearnerDailyActivity::getLessonsCompletedCount).sum();
                    LocalDate last = e.getValue().stream().map(LearnerDailyActivity::getActivityDate).max(LocalDate::compareTo).orElse(LocalDate.MIN);
                    boolean atRisk = last.isBefore(LocalDate.now().minusDays(14));
                    return Map.<String, Object>of("learnerId", e.getKey(), "progressLessons", lessons, "lastActivity", last.toString(), "alert", atRisk ? "AT_RISK" : "OK");
                }).toList();
        result.put("courseId", courseId);
        result.put("students", items);
        return result;
    }

    public Map<String, Object> quizDifficulties(String quizId) {
        QuizPerformanceSummary s = quizRepo.findByQuizId(quizId).orElseThrow(() -> new NotFoundException("Quiz summary not found"));
        return Map.of("quizId", s.getQuizId(), "globalSuccessRate", s.getGlobalSuccessRate(), "hardestQuestionId", s.getHardestQuestionId(), "totalAttempts", s.getTotalAttempts());
    }

    public GlobalAnalyticsDTO adminSummary() {
        long activeUsers = learnerRepo.countByActivityDateAfter(LocalDate.now().minusDays(30));
        long enrollments = courseRepo.findAll().stream().mapToLong(CourseMetric::getTotalEnrollments).sum();
        BigDecimal revenue = courseRepo.findAll().stream().map(CourseMetric::getTotalRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Long> curve = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            long c = learnerRepo.findAll().stream().filter(a -> a.getActivityDate().equals(d)).count();
            curve.put(d.toString(), c);
        }
        return new GlobalAnalyticsDTO(activeUsers, enrollments, revenue, List.of("Programming", "Data Science", "DevOps", "AI", "Soft Skills"), curve, activeUsers == 0 ? 0.0 : (double) enrollments / activeUsers);
    }

    @Transactional
    public CourseMetric ingestEnrollment(EnrollmentEventRequest req) {
        CourseMetric metric = courseRepo.findByCourseId(req.courseId()).orElse(CourseMetric.builder()
                .courseId(req.courseId()).teacherId(req.teacherId()).title(req.courseTitle()).totalRevenue(BigDecimal.ZERO).updatedAt(Instant.now()).build());
        metric.setTeacherId(req.teacherId());
        metric.setTitle(req.courseTitle());
        metric.setTotalEnrollments(metric.getTotalEnrollments() + 1);
        metric.setTotalRevenue(metric.getTotalRevenue().add(req.amount()));
        metric.setUpdatedAt(Instant.now());
        return courseRepo.save(metric);
    }

    @Transactional
    public LearnerDailyActivity ingestLessonComplete(LessonCompleteEventRequest req) {
        LocalDate date = req.activityDate() == null ? LocalDate.now() : req.activityDate();
        LearnerDailyActivity act = learnerRepo.findByLearnerIdAndActivityDate(req.learnerId(), date)
                .orElse(LearnerDailyActivity.builder().learnerId(req.learnerId()).activityDate(date).build());
        act.setTimeSpentInMinutes(act.getTimeSpentInMinutes() + req.minutesSpent());
        act.setLessonsCompletedCount(act.getLessonsCompletedCount() + req.lessonsCompleted());
        return learnerRepo.save(act);
    }

    @Transactional
    public QuizPerformanceSummary ingestQuizSubmit(QuizSubmitEventRequest req) {
        QuizPerformanceSummary q = quizRepo.findByQuizId(req.quizId()).orElse(QuizPerformanceSummary.builder()
                .quizId(req.quizId()).courseId(req.courseId()).totalAttempts(0).globalSuccessRate(0).build());
        double newRate = ((q.getGlobalSuccessRate() * q.getTotalAttempts()) + req.scorePercent()) / (q.getTotalAttempts() + 1);
        q.setTotalAttempts(q.getTotalAttempts() + 1);
        q.setGlobalSuccessRate(newRate);
        q.setHardestQuestionId(req.hardestQuestionId());
        quizRepo.save(q);
        LearnerDailyActivity act = learnerRepo.findByLearnerIdAndActivityDate(req.learnerId(), LocalDate.now())
                .orElse(LearnerDailyActivity.builder().learnerId(req.learnerId()).activityDate(LocalDate.now()).build());
        act.setQuizTakenCount(act.getQuizTakenCount() + 1);
        learnerRepo.save(act);
        return q;
    }
}

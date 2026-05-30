package com.anouar.elearning.analytics.repository;

import com.anouar.elearning.analytics.entity.LearnerDailyActivity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerDailyActivityRepository extends JpaRepository<LearnerDailyActivity, Long> {
    List<LearnerDailyActivity> findByLearnerIdOrderByActivityDateDesc(String learnerId);
    Optional<LearnerDailyActivity> findByLearnerIdAndActivityDate(String learnerId, LocalDate activityDate);
    long countByActivityDateAfter(LocalDate date);
}


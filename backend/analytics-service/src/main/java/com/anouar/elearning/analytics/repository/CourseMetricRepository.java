package com.anouar.elearning.analytics.repository;

import com.anouar.elearning.analytics.entity.CourseMetric;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseMetricRepository extends JpaRepository<CourseMetric, Long> {
    List<CourseMetric> findByTeacherId(String teacherId);
    Optional<CourseMetric> findByCourseId(String courseId);
}


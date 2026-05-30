package com.anouar.elearning.course.repository;

import com.anouar.elearning.course.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, String> {
    Optional<CourseReview> findByLearnerIdAndCourseId(String learnerId, String courseId);

    List<CourseReview> findByCourseId(String courseId);
}

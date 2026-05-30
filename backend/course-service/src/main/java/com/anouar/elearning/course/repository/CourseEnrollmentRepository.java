package com.anouar.elearning.course.repository;

import com.anouar.elearning.course.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, String> {
    Optional<CourseEnrollment> findByLearnerIdAndCourseId(String learnerId, String courseId);
    List<CourseEnrollment> findByLearnerIdAndActiveTrue(String learnerId);
}

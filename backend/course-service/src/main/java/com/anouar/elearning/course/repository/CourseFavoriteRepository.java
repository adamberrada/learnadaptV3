package com.anouar.elearning.course.repository;

import com.anouar.elearning.course.entity.CourseFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseFavoriteRepository extends JpaRepository<CourseFavorite, String> {
    Optional<CourseFavorite> findByLearnerIdAndCourseId(String learnerId, String courseId);

    List<CourseFavorite> findByLearnerId(String learnerId);
}

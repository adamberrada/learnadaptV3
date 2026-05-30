package com.anouar.elearning.course.repository;

import com.anouar.elearning.course.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, String> {
    Optional<LessonProgress> findByLearnerIdAndLessonId(String learnerId, String lessonId);

    List<LessonProgress> findByLearnerIdAndLessonChapterCourseId(String learnerId, String courseId);

    int countByLearnerIdAndLessonChapterCourseIdAndCompletedTrue(String learnerId, String courseId);
}

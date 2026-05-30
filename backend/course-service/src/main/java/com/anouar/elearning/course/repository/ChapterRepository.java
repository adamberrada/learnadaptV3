package com.anouar.elearning.course.repository;

import com.anouar.elearning.course.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    List<Chapter> findByCourseIdOrderByOrderIndexAsc(String courseId);
}

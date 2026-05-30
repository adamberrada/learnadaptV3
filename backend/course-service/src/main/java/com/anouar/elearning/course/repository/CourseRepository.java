package com.anouar.elearning.course.repository;

import com.anouar.elearning.course.entity.Course;
import com.anouar.elearning.course.entity.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByInstructorId(String instructorId);

    List<Course> findByStatus(CourseStatus status);

    List<Course> findByCategoryIdAndStatus(String categoryId, CourseStatus status);

    List<Course> findBySubCategoryIdAndStatus(String subCategoryId, CourseStatus status);

    @Query("""
            select distinct c from Course c
            left join c.tags t
            where (:status is null or c.status = :status)
              and (:keyword is null or lower(c.title) like lower(concat('%', :keyword, '%'))
                   or lower(c.description) like lower(concat('%', :keyword, '%')))
              and (:tag is null or lower(t) = lower(:tag))
            """)
    List<Course> search(String keyword, String tag, CourseStatus status);
}

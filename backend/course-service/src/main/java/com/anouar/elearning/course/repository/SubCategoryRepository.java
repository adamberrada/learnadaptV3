package com.anouar.elearning.course.repository;

import com.anouar.elearning.course.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {
    Optional<SubCategory> findByNameIgnoreCaseAndCategoryId(String name, String categoryId);

    List<SubCategory> findByCategoryId(String categoryId);

    List<SubCategory> findByNameContainingIgnoreCase(String name);
}

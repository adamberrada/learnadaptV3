package com.anouar.elearning.course.repository;

import com.anouar.elearning.course.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByNameIgnoreCase(String name);

    List<Category> findByNameContainingIgnoreCase(String name);
}

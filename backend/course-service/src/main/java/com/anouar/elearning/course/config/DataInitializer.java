package com.anouar.elearning.course.config;

import com.anouar.elearning.course.entity.Category;
import com.anouar.elearning.course.entity.Course;
import com.anouar.elearning.course.entity.CourseLevel;
import com.anouar.elearning.course.entity.CourseStatus;
import com.anouar.elearning.course.repository.CategoryRepository;
import com.anouar.elearning.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    @Override
    public void run(String... args) {
        if (!courseRepository.findByStatus(CourseStatus.PUBLISHED).isEmpty()) {
            return;
        }

        Category data = category("Data", "Analytics, statistics, and machine learning.");
        Category science = category("Science", "Cognitive science and learning foundations.");
        Category tech = category("Tech", "Software engineering and cybersecurity.");
        Category design = category("Design", "Design systems and product practice.");
        Category business = category("Business", "Marketing, communication, and business skills.");

        course("Statistics for Decision Making", data, CourseLevel.INTERMEDIATE, 14, Set.of("Popular", "Data"));
        course("Cognitive Science Foundations", science, CourseLevel.BEGINNER, 10, Set.of("New", "Science"));
        course("Full-Stack Web Engineering", tech, CourseLevel.ADVANCED, 32, Set.of("Trending", "Tech"));
        course("Design Systems in Practice", design, CourseLevel.INTERMEDIATE, 8, Set.of("Featured", "Design"));
        course("Marketing Analytics 101", business, CourseLevel.BEGINNER, 11, Set.of("Popular", "Business"));
        course("Applied Machine Learning", data, CourseLevel.ADVANCED, 26, Set.of("Pro", "Data"));
    }

    private Category category(String name, String description) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(name)
                        .description(description)
                        .build()));
    }

    private void course(String title, Category category, CourseLevel level, int hours, Set<String> tags) {
        Course course = Course.builder()
                .title(title)
                .description("Adaptive course aligned with LearnAdapt's frontend catalogue.")
                .category(category)
                .instructorId("seed-instructor")
                .thumbnailUrl("")
                .level(level)
                .status(CourseStatus.PUBLISHED)
                .price(BigDecimal.ZERO)
                .durationInMinutes(hours * 60)
                .tags(new HashSet<>(tags))
                .build();
        courseRepository.save(course);
    }
}

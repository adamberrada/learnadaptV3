package com.anouar.elearning.course.service;

import com.anouar.elearning.course.dto.*;
import com.anouar.elearning.course.entity.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class CourseMapper {

    public CourseResponse toCourseResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(toCategoryResponse(course.getCategory()))
                .subCategory(toSubCategoryResponse(course.getSubCategory()))
                .instructorId(course.getInstructorId())
                .thumbnailUrl(course.getThumbnailUrl())
                .level(course.getLevel())
                .status(course.getStatus())
                .price(course.getPrice())
                .durationInMinutes(course.getDurationInMinutes())
                .tags(new HashSet<>(course.getTags()))
                .chapters(toChapterResponses(course.getChapters()))
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    public CategoryResponse toCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public SubCategoryResponse toSubCategoryResponse(SubCategory subCategory) {
        if (subCategory == null) {
            return null;
        }
        return SubCategoryResponse.builder()
                .id(subCategory.getId())
                .name(subCategory.getName())
                .description(subCategory.getDescription())
                .categoryId(subCategory.getCategory().getId())
                .categoryName(subCategory.getCategory().getName())
                .createdAt(subCategory.getCreatedAt())
                .updatedAt(subCategory.getUpdatedAt())
                .build();
    }

    public ChapterResponse toChapterResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .orderIndex(chapter.getOrderIndex())
                .lessons(toLessonResponses(chapter.getLessons()))
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .build();
    }

    public LessonResponse toLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .type(lesson.getType())
                .videoUrl(lesson.getVideoUrl())
                .externalUrl(lesson.getExternalUrl())
                .textContent(lesson.getTextContent())
                .orderIndex(lesson.getOrderIndex())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }

    public ReviewResponse toReviewResponse(CourseReview review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .learnerId(review.getLearnerId())
                .courseId(review.getCourse().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public List<CourseResponse> toCourseResponses(List<Course> courses) {
        return courses.stream().map(this::toCourseResponse).toList();
    }

    public List<ChapterResponse> toChapterResponses(List<Chapter> chapters) {
        return chapters.stream().map(this::toChapterResponse).toList();
    }

    public List<LessonResponse> toLessonResponses(List<Lesson> lessons) {
        return lessons.stream().map(this::toLessonResponse).toList();
    }
}

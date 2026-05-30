package com.anouar.elearning.course.controller;

import com.anouar.elearning.course.dto.ApiResponse;
import com.anouar.elearning.course.dto.CategoryResponse;
import com.anouar.elearning.course.dto.CourseResponse;
import com.anouar.elearning.course.dto.SubCategoryResponse;
import com.anouar.elearning.course.service.CategoryService;
import com.anouar.elearning.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicCourseController {

    private final CourseService courseService;
    private final CategoryService categoryService;

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> browseCourses(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String subCategoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Published courses retrieved successfully!",
                courseService.browsePublishedCourses(categoryId, subCategoryId, keyword, tag)
        ));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Course retrieved successfully!",
                courseService.getPublishedCourse(courseId)
        ));
    }

    @GetMapping("/courses/{courseId}/outline")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseOutline(@PathVariable String courseId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Course outline retrieved successfully!",
                courseService.getPublishedCourse(courseId)
        ));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> listCategories(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(
                "Categories retrieved successfully!",
                categoryService.listCategories(search)
        ));
    }

    @GetMapping("/sub-categories")
    public ResponseEntity<ApiResponse<List<SubCategoryResponse>>> listSubCategories(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Sub-categories retrieved successfully!",
                categoryService.listSubCategories(categoryId, search)
        ));
    }
}

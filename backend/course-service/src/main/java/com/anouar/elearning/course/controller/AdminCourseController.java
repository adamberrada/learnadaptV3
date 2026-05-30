package com.anouar.elearning.course.controller;

import com.anouar.elearning.course.dto.*;
import com.anouar.elearning.course.service.CategoryService;
import com.anouar.elearning.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {

    private final CourseService courseService;
    private final CategoryService categoryService;

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> listCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Courses retrieved successfully!",
                courseService.getAllCoursesForAdmin(keyword, tag)
        ));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(ApiResponse.success("Course retrieved successfully!", courseService.getAnyCourse(courseId)));
    }

    @PostMapping("/courses/{courseId}/approve")
    public ResponseEntity<ApiResponse<CourseResponse>> approveCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(ApiResponse.success("Course approved successfully!", courseService.approveCourse(courseId)));
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<String>> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourseByAdmin(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course removed by administrator!", courseId));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully!", categoryService.createCategory(request)));
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable String categoryId,
            @Valid @RequestBody CategoryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Category updated successfully!",
                categoryService.updateCategory(categoryId, request)
        ));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully!", categoryId));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> listCategories(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully!", categoryService.listCategories(search)));
    }

    @PostMapping("/sub-categories")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> createSubCategory(@Valid @RequestBody SubCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sub-category created successfully!", categoryService.createSubCategory(request)));
    }

    @PutMapping("/sub-categories/{subCategoryId}")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> updateSubCategory(
            @PathVariable String subCategoryId,
            @Valid @RequestBody SubCategoryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Sub-category updated successfully!",
                categoryService.updateSubCategory(subCategoryId, request)
        ));
    }

    @DeleteMapping("/sub-categories/{subCategoryId}")
    public ResponseEntity<ApiResponse<String>> deleteSubCategory(@PathVariable String subCategoryId) {
        categoryService.deleteSubCategory(subCategoryId);
        return ResponseEntity.ok(ApiResponse.success("Sub-category deleted successfully!", subCategoryId));
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

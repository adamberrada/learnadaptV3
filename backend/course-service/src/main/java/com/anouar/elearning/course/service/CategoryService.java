package com.anouar.elearning.course.service;

import com.anouar.elearning.course.dto.CategoryRequest;
import com.anouar.elearning.course.dto.CategoryResponse;
import com.anouar.elearning.course.dto.SubCategoryRequest;
import com.anouar.elearning.course.dto.SubCategoryResponse;
import com.anouar.elearning.course.entity.Category;
import com.anouar.elearning.course.entity.SubCategory;
import com.anouar.elearning.course.exception.BusinessException;
import com.anouar.elearning.course.exception.ResourceNotFoundException;
import com.anouar.elearning.course.repository.CategoryRepository;
import com.anouar.elearning.course.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CourseMapper mapper;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        categoryRepository.findByNameIgnoreCase(request.getName()).ifPresent(category -> {
            throw new BusinessException("Category already exists: " + request.getName());
        });
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return mapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category category = findCategory(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return mapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(String id) {
        categoryRepository.delete(findCategory(id));
    }

    public List<CategoryResponse> listCategories(String search) {
        List<Category> categories = search == null || search.isBlank()
                ? categoryRepository.findAll()
                : categoryRepository.findByNameContainingIgnoreCase(search);
        return categories.stream().map(mapper::toCategoryResponse).toList();
    }

    @Transactional
    public SubCategoryResponse createSubCategory(SubCategoryRequest request) {
        Category category = findCategory(request.getCategoryId());
        subCategoryRepository.findByNameIgnoreCaseAndCategoryId(request.getName(), category.getId()).ifPresent(existing -> {
            throw new BusinessException("Sub-category already exists in this category: " + request.getName());
        });
        SubCategory subCategory = SubCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .build();
        return mapper.toSubCategoryResponse(subCategoryRepository.save(subCategory));
    }

    @Transactional
    public SubCategoryResponse updateSubCategory(String id, SubCategoryRequest request) {
        SubCategory subCategory = findSubCategory(id);
        subCategory.setName(request.getName());
        subCategory.setDescription(request.getDescription());
        subCategory.setCategory(findCategory(request.getCategoryId()));
        return mapper.toSubCategoryResponse(subCategoryRepository.save(subCategory));
    }

    @Transactional
    public void deleteSubCategory(String id) {
        subCategoryRepository.delete(findSubCategory(id));
    }

    public List<SubCategoryResponse> listSubCategories(String categoryId, String search) {
        List<SubCategory> subCategories;
        if (categoryId != null && !categoryId.isBlank()) {
            subCategories = subCategoryRepository.findByCategoryId(categoryId);
        } else if (search != null && !search.isBlank()) {
            subCategories = subCategoryRepository.findByNameContainingIgnoreCase(search);
        } else {
            subCategories = subCategoryRepository.findAll();
        }
        return subCategories.stream().map(mapper::toSubCategoryResponse).toList();
    }

    private Category findCategory(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private SubCategory findSubCategory(String id) {
        return subCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-category not found with id: " + id));
    }
}

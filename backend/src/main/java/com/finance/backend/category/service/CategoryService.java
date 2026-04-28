package com.finance.backend.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.category.dto.CategoryRequest;
import com.finance.backend.category.dto.CategoryResponse;
import com.finance.backend.category.model.Category;
import com.finance.backend.category.repository.CategoryRepository;
import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String normalizedName = request.name().trim();
        boolean alreadyExists = categoryRepository.existsByNameIgnoreCaseAndTypeAndUserId(
                normalizedName,
                request.type(),
                request.userId());
        if (alreadyExists || categoryRepository.existsByNameIgnoreCaseAndTypeAndDefaultCategoryTrue(normalizedName, request.type())) {
            throw new BadRequestException("Category already exists for the requested type");
        }

        Category category = Category.builder()
                .name(normalizedName)
                .type(request.type())
                .userId(request.userId())
                .defaultCategory(false)
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(Long userId) {
        return categoryRepository.findByDefaultCategoryTrueOrUserIdOrderByNameAsc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Category getAccessibleCategory(Long categoryId, Long userId) {
        return categoryRepository.findByIdAndDefaultCategoryTrueOrIdAndUserId(categoryId, categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for user"));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .userId(category.getUserId())
                .defaultCategory(category.isDefaultCategory())
                .build();
    }
}

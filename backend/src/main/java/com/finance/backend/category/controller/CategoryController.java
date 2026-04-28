package com.finance.backend.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.auth.security.AuthenticatedUserPrincipal;
import com.finance.backend.category.dto.CategoryRequest;
import com.finance.backend.category.dto.CategoryResponse;
import com.finance.backend.category.service.CategoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/categories", "/api/v1/categories"})
@Validated
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.createCategory(currentUser.getId(), request);
    }

    @GetMapping
    public List<CategoryResponse> getCategories(@AuthenticationPrincipal AuthenticatedUserPrincipal currentUser) {
        return categoryService.getCategories(currentUser.getId());
    }

    @PutMapping("/{categoryId}")
    public CategoryResponse updateCategory(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @PathVariable @Positive Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(currentUser.getId(), categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @PathVariable @Positive Long categoryId) {
        categoryService.deleteCategory(currentUser.getId(), categoryId);
    }
}

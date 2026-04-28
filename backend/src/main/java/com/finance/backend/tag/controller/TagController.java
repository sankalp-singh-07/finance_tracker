package com.finance.backend.tag.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
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
import com.finance.backend.tag.dto.TagRequest;
import com.finance.backend.tag.dto.TagResponse;
import com.finance.backend.tag.service.TagService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/tags", "/api/v1/tags"})
@Validated
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponse createTag(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @Valid @RequestBody TagRequest request) {
        return tagService.createTag(currentUser.getId(), request);
    }

    @GetMapping
    public List<TagResponse> getTags(@AuthenticationPrincipal AuthenticatedUserPrincipal currentUser) {
        return tagService.getTags(currentUser.getId());
    }

    @PutMapping("/{tagId}")
    public TagResponse updateTag(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @PathVariable @Positive Long tagId,
            @Valid @RequestBody TagRequest request) {
        return tagService.updateTag(currentUser.getId(), tagId, request);
    }

    @DeleteMapping("/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @PathVariable @Positive Long tagId) {
        tagService.deleteTag(currentUser.getId(), tagId);
    }
}

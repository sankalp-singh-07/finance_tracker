package com.finance.backend.tag.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.auth.model.User;
import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.common.exception.ResourceNotFoundException;
import com.finance.backend.tag.dto.TagRequest;
import com.finance.backend.tag.dto.TagResponse;
import com.finance.backend.tag.model.Tag;
import com.finance.backend.tag.repository.TagRepository;
import com.finance.backend.transaction.repository.FinanceTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final FinanceTransactionRepository transactionRepository;

    @Transactional
    public TagResponse createTag(Long userId, TagRequest request) {
        String normalizedName = request.name().trim();
        if (tagRepository.existsByUser_IdAndNameIgnoreCase(userId, normalizedName)) {
            throw new BadRequestException("Tag already exists for user");
        }

        Tag tag = Tag.builder()
                .name(normalizedName)
                .user(User.builder().id(userId).build())
                .build();

        return mapToResponse(tagRepository.save(tag));
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getTags(Long userId) {
        return tagRepository.findByUser_IdOrderByNameAsc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public TagResponse updateTag(Long userId, Long tagId, TagRequest request) {
        Tag tag = tagRepository.findByIdAndUser_Id(tagId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found for user"));

        String normalizedName = request.name().trim();
        if (tagRepository.existsByUser_IdAndNameIgnoreCaseAndIdNot(userId, normalizedName, tagId)) {
            throw new BadRequestException("Tag already exists for user");
        }

        tag.setName(normalizedName);
        return mapToResponse(tagRepository.save(tag));
    }

    @Transactional
    public void deleteTag(Long userId, Long tagId) {
        Tag tag = tagRepository.findByIdAndUser_Id(tagId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found for user"));

        if (transactionRepository.existsByUser_IdAndTags_Id(userId, tagId)) {
            throw new BadRequestException("Tag is used in transactions and cannot be deleted");
        }

        tagRepository.delete(tag);
    }

    @Transactional(readOnly = true)
    public List<Tag> getTagsForUser(Long userId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Tag> tags = tagRepository.findByUser_IdAndIdIn(userId, tagIds);
        if (tags.size() != tagIds.size()) {
            throw new ResourceNotFoundException("One or more tags do not belong to the user");
        }
        return tags;
    }

    private TagResponse mapToResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .userId(tag.getUser().getId())
                .build();
    }
}

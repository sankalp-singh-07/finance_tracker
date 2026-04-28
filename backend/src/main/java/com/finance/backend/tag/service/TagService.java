package com.finance.backend.tag.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.common.exception.ResourceNotFoundException;
import com.finance.backend.tag.dto.TagRequest;
import com.finance.backend.tag.dto.TagResponse;
import com.finance.backend.tag.model.Tag;
import com.finance.backend.tag.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public TagResponse createTag(TagRequest request) {
        String normalizedName = request.name().trim();
        if (tagRepository.existsByUserIdAndNameIgnoreCase(request.userId(), normalizedName)) {
            throw new BadRequestException("Tag already exists for user");
        }

        Tag tag = Tag.builder()
                .name(normalizedName)
                .userId(request.userId())
                .build();

        return mapToResponse(tagRepository.save(tag));
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getTags(Long userId) {
        return tagRepository.findByUserIdOrderByNameAsc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Tag> getTagsForUser(Long userId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Tag> tags = tagRepository.findByUserIdAndIdIn(userId, tagIds);
        if (tags.size() != tagIds.size()) {
            throw new ResourceNotFoundException("One or more tags do not belong to the user");
        }
        return tags;
    }

    private TagResponse mapToResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .userId(tag.getUserId())
                .build();
    }
}

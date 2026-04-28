package com.finance.backend.tag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finance.backend.auth.model.User;
import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.tag.dto.TagRequest;
import com.finance.backend.tag.dto.TagResponse;
import com.finance.backend.tag.model.Tag;
import com.finance.backend.tag.repository.TagRepository;
import com.finance.backend.transaction.repository.FinanceTransactionRepository;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private FinanceTransactionRepository transactionRepository;

    @InjectMocks
    private TagService tagService;

    @Test
    void updateTagShouldPersistTrimmedName() {
        Long userId = 1L;
        Long tagId = 20L;
        Tag tag = Tag.builder().id(tagId).name("old").user(User.builder().id(userId).build()).build();

        when(tagRepository.findByIdAndUser_Id(tagId, userId)).thenReturn(Optional.of(tag));
        when(tagRepository.existsByUser_IdAndNameIgnoreCaseAndIdNot(userId, "Travel", tagId)).thenReturn(false);
        when(tagRepository.save(tag)).thenReturn(tag);

        TagResponse response = tagService.updateTag(userId, tagId, new TagRequest("  Travel  "));

        assertEquals("Travel", response.name());
        verify(tagRepository).save(tag);
    }

    @Test
    void deleteTagShouldFailWhenTagIsUsedInTransactions() {
        Long userId = 1L;
        Long tagId = 20L;
        Tag tag = Tag.builder().id(tagId).name("Groceries").user(User.builder().id(userId).build()).build();

        when(tagRepository.findByIdAndUser_Id(tagId, userId)).thenReturn(Optional.of(tag));
        when(transactionRepository.existsByUser_IdAndTags_Id(userId, tagId)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> tagService.deleteTag(userId, tagId));
        verify(tagRepository, never()).delete(tag);
    }
}

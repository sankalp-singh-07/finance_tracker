package com.finance.backend.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.backend.tag.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByUser_IdOrderByNameAsc(Long userId);

    List<Tag> findByUser_IdAndIdIn(Long userId, List<Long> ids);

    boolean existsByUser_IdAndNameIgnoreCase(Long userId, String name);
}

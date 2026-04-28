package com.finance.backend.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.backend.tag.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByUserIdOrderByNameAsc(Long userId);

    List<Tag> findByUserIdAndIdIn(Long userId, List<Long> ids);

    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
}

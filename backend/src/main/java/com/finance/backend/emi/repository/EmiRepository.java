package com.finance.backend.emi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.backend.emi.model.Emi;

public interface EmiRepository extends JpaRepository<Emi, Long> {

    List<Emi> findByUserIdOrderByStartDateAsc(Long userId);

    Optional<Emi> findByIdAndUserId(Long id, Long userId);
}

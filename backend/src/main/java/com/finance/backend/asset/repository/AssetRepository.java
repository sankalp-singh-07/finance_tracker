package com.finance.backend.asset.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.backend.asset.model.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByUserIdOrderByLastUpdatedDesc(Long userId);

    Optional<Asset> findByIdAndUserId(Long id, Long userId);
}

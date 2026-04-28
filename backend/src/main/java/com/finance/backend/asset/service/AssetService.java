package com.finance.backend.asset.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.asset.dto.AssetRequest;
import com.finance.backend.asset.dto.AssetResponse;
import com.finance.backend.asset.dto.AssetUpdateRequest;
import com.finance.backend.asset.model.Asset;
import com.finance.backend.asset.repository.AssetRepository;
import com.finance.backend.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;

    @Transactional
    public AssetResponse createAsset(AssetRequest request) {
        Asset asset = Asset.builder()
                .name(request.name().trim())
                .type(request.type())
                .value(request.value())
                .lastUpdated(LocalDateTime.now())
                .userId(request.userId())
                .build();

        return mapToResponse(assetRepository.save(asset));
    }

    @Transactional
    public AssetResponse updateAssetValue(Long assetId, AssetUpdateRequest request) {
        Asset asset = assetRepository.findByIdAndUserId(assetId, request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found for user"));

        asset.setValue(request.value());
        asset.setLastUpdated(LocalDateTime.now());
        return mapToResponse(assetRepository.save(asset));
    }

    @Transactional(readOnly = true)
    public List<AssetResponse> getAssets(Long userId) {
        return assetRepository.findByUserIdOrderByLastUpdatedDesc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AssetResponse mapToResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .name(asset.getName())
                .type(asset.getType())
                .value(asset.getValue())
                .lastUpdated(asset.getLastUpdated())
                .userId(asset.getUserId())
                .build();
    }
}

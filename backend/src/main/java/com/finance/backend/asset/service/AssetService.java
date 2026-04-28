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
    public AssetResponse createAsset(Long userId, AssetRequest request) {
        Asset asset = Asset.builder()
                .name(request.name().trim())
                .type(request.type())
                .value(request.value())
                .lastUpdated(LocalDateTime.now())
                .userId(userId)
                .build();

        return mapToResponse(assetRepository.save(asset));
    }

    @Transactional
    public AssetResponse updateAssetValue(Long userId, Long assetId, AssetUpdateRequest request) {
        Asset asset = assetRepository.findByIdAndUserId(assetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found for user"));

        asset.setValue(request.value());
        asset.setLastUpdated(LocalDateTime.now());
        return mapToResponse(assetRepository.save(asset));
    }

    @Transactional
    public AssetResponse updateAsset(Long userId, Long assetId, AssetRequest request) {
        Asset asset = assetRepository.findByIdAndUserId(assetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found for user"));

        asset.setName(request.name().trim());
        asset.setType(request.type());
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

    @Transactional(readOnly = true)
    public AssetResponse getAsset(Long userId, Long assetId) {
        Asset asset = assetRepository.findByIdAndUserId(assetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found for user"));
        return mapToResponse(asset);
    }

    @Transactional
    public void deleteAsset(Long userId, Long assetId) {
        Asset asset = assetRepository.findByIdAndUserId(assetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found for user"));
        assetRepository.delete(asset);
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

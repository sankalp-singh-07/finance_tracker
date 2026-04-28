package com.finance.backend.asset.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.auth.security.AuthenticatedUserPrincipal;
import com.finance.backend.asset.dto.AssetRequest;
import com.finance.backend.asset.dto.AssetResponse;
import com.finance.backend.asset.dto.AssetUpdateRequest;
import com.finance.backend.asset.service.AssetService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/assets", "/api/v1/assets"})
@Validated
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssetResponse createAsset(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @Valid @RequestBody AssetRequest request) {
        return assetService.createAsset(currentUser.getId(), request);
    }

    @PatchMapping("/{assetId}/value")
    public AssetResponse updateAssetValue(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @PathVariable @Positive Long assetId,
            @Valid @RequestBody AssetUpdateRequest request) {
        return assetService.updateAssetValue(currentUser.getId(), assetId, request);
    }

    @GetMapping
    public List<AssetResponse> getAssets(@AuthenticationPrincipal AuthenticatedUserPrincipal currentUser) {
        return assetService.getAssets(currentUser.getId());
    }
}

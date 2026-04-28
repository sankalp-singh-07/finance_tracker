package com.finance.backend.alert.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.alert.dto.AlertsResponse;
import com.finance.backend.alert.service.AlertService;
import com.finance.backend.auth.security.AuthenticatedUserPrincipal;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/alerts", "/api/v1/alerts"})
@Validated
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public AlertsResponse getAlerts(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @RequestParam(required = false) @Positive Integer daysAhead) {
        return alertService.getAlerts(currentUser.getId(), daysAhead);
    }
}

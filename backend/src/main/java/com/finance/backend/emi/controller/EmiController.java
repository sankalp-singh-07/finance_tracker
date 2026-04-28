package com.finance.backend.emi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.emi.dto.EmiPaymentRequest;
import com.finance.backend.emi.dto.EmiRequest;
import com.finance.backend.emi.dto.EmiResponse;
import com.finance.backend.emi.service.EmiService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/emis")
@Validated
@RequiredArgsConstructor
public class EmiController {

    private final EmiService emiService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmiResponse createEmi(@Valid @RequestBody EmiRequest request) {
        return emiService.createEmi(request);
    }

    @PatchMapping("/{emiId}/payment")
    public EmiResponse applyPayment(
            @PathVariable @Positive Long emiId,
            @Valid @RequestBody EmiPaymentRequest request) {
        return emiService.applyPayment(emiId, request);
    }

    @GetMapping
    public List<EmiResponse> getEmis(@RequestParam @Positive Long userId) {
        return emiService.getEmis(userId);
    }
}

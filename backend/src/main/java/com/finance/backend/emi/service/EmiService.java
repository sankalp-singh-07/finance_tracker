package com.finance.backend.emi.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.common.exception.ResourceNotFoundException;
import com.finance.backend.emi.dto.EmiPaymentRequest;
import com.finance.backend.emi.dto.EmiRequest;
import com.finance.backend.emi.dto.EmiResponse;
import com.finance.backend.emi.model.Emi;
import com.finance.backend.emi.repository.EmiRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmiService {

    private final EmiRepository emiRepository;

    @Transactional
    public EmiResponse createEmi(EmiRequest request) {
        if (request.remainingAmount().compareTo(request.totalAmount()) > 0) {
            throw new BadRequestException("Remaining amount cannot exceed total amount");
        }

        Emi emi = Emi.builder()
                .name(request.name().trim())
                .totalAmount(request.totalAmount())
                .monthlyEmi(request.monthlyEmi())
                .remainingAmount(request.remainingAmount())
                .interestRate(request.interestRate())
                .startDate(request.startDate())
                .userId(request.userId())
                .build();

        return mapToResponse(emiRepository.save(emi));
    }

    @Transactional
    public EmiResponse applyPayment(Long emiId, EmiPaymentRequest request) {
        Emi emi = emiRepository.findByIdAndUserId(emiId, request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("EMI not found for user"));

        BigDecimal updatedAmount = emi.getRemainingAmount().subtract(request.paymentAmount());
        if (updatedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Payment amount cannot exceed remaining amount");
        }

        emi.setRemainingAmount(updatedAmount);
        return mapToResponse(emiRepository.save(emi));
    }

    @Transactional(readOnly = true)
    public List<EmiResponse> getEmis(Long userId) {
        return emiRepository.findByUserIdOrderByStartDateAsc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private EmiResponse mapToResponse(Emi emi) {
        return EmiResponse.builder()
                .id(emi.getId())
                .name(emi.getName())
                .totalAmount(emi.getTotalAmount())
                .monthlyEmi(emi.getMonthlyEmi())
                .remainingAmount(emi.getRemainingAmount())
                .interestRate(emi.getInterestRate())
                .startDate(emi.getStartDate())
                .userId(emi.getUserId())
                .build();
    }
}

package com.finance.backend.emi.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.auth.model.User;
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
    public EmiResponse createEmi(Long userId, EmiRequest request) {
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
                .user(User.builder().id(userId).build())
                .build();

        return mapToResponse(emiRepository.save(emi));
    }

    @Transactional
    public EmiResponse applyPayment(Long userId, Long emiId, EmiPaymentRequest request) {
        Emi emi = emiRepository.findByIdAndUser_Id(emiId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI not found for user"));

        BigDecimal updatedAmount = emi.getRemainingAmount().subtract(request.paymentAmount());
        if (updatedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Payment amount cannot exceed remaining amount");
        }

        emi.setRemainingAmount(updatedAmount);
        return mapToResponse(emiRepository.save(emi));
    }

    @Transactional
    public EmiResponse updateEmi(Long userId, Long emiId, EmiRequest request) {
        Emi emi = emiRepository.findByIdAndUser_Id(emiId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI not found for user"));
        if (request.remainingAmount().compareTo(request.totalAmount()) > 0) {
            throw new BadRequestException("Remaining amount cannot exceed total amount");
        }

        emi.setName(request.name().trim());
        emi.setTotalAmount(request.totalAmount());
        emi.setMonthlyEmi(request.monthlyEmi());
        emi.setRemainingAmount(request.remainingAmount());
        emi.setInterestRate(request.interestRate());
        emi.setStartDate(request.startDate());
        return mapToResponse(emiRepository.save(emi));
    }

    @Transactional(readOnly = true)
    public List<EmiResponse> getEmis(Long userId) {
        return emiRepository.findByUser_IdOrderByStartDateAsc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmiResponse getEmi(Long userId, Long emiId) {
        Emi emi = emiRepository.findByIdAndUser_Id(emiId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI not found for user"));
        return mapToResponse(emi);
    }

    @Transactional
    public void deleteEmi(Long userId, Long emiId) {
        Emi emi = emiRepository.findByIdAndUser_Id(emiId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI not found for user"));
        emiRepository.delete(emi);
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
                .userId(emi.getUser().getId())
                .build();
    }
}

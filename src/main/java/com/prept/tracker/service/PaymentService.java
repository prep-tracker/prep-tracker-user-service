package com.prept.tracker.service;

import com.prept.tracker.domain.entity.Payment;
import com.prept.tracker.dto.response.PaymentResponse;
import com.prept.tracker.mapper.PaymentMapper;
import com.prept.tracker.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return payments.stream().map(paymentMapper::toResponse).toList();
    }
}

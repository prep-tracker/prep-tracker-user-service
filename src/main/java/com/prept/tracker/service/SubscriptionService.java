package com.prept.tracker.service;

import com.prept.tracker.domain.entity.Payment;
import com.prept.tracker.domain.entity.Subscription;
import com.prept.tracker.domain.entity.User;
import com.prept.tracker.domain.enums.PlanType;
import com.prept.tracker.domain.enums.SubscriptionStatus;
import com.prept.tracker.dto.request.UpgradeRequest;
import com.prept.tracker.dto.response.SubscriptionResponse;
import com.prept.tracker.exception.EntityNotFoundException;
import com.prept.tracker.mapper.SubscriptionMapper;
import com.prept.tracker.repository.SubscriptionRepository;
import com.prept.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public SubscriptionResponse getSubscriptionByUserId(Long userId) {
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found"));
                    return createDefaultSubscription(user);
                });
        return subscriptionMapper.toResponse(subscription);
    }

    @Transactional
    public Subscription createDefaultSubscription(User user) {
        Subscription sub = Subscription.builder()
                .user(user)
                .planType(PlanType.FREE)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(Instant.now())
                .build();
        return subscriptionRepository.save(sub);
    }

    @Transactional
    public SubscriptionResponse upgradeSubscription(Long userId, UpgradeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSubscription(user));

        PlanType targetPlan = request.getPlanType();
        if (targetPlan == PlanType.FREE) {
            throw new IllegalArgumentException("Cannot upgrade to FREE plan. Use cancel endpoint instead.");
        }

        // Simulate payment billing amount
        BigDecimal amount = targetPlan == PlanType.PRO ? new BigDecimal("19.99") : new BigDecimal("9.99");
        
        // Record payment transaction
        Payment payment = Payment.builder()
                .user(user)
                .amount(amount)
                .currency("USD")
                .paymentStatus("COMPLETED")
                .paymentMethod("MOCK")
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .planType(targetPlan)
                .build();
        paymentService.savePayment(payment);

        // Update subscription
        subscription.setPlanType(targetPlan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(Instant.now());
        subscription.setEndDate(Instant.now().plus(30, ChronoUnit.DAYS)); // 30 days billing period
        Subscription saved = subscriptionRepository.save(subscription);

        return subscriptionMapper.toResponse(saved);
    }

    @Transactional
    public SubscriptionResponse cancelSubscription(Long userId) {
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));

        // Revert to Free subscription
        subscription.setPlanType(PlanType.FREE);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(Instant.now());
        subscription.setEndDate(null);
        Subscription saved = subscriptionRepository.save(subscription);

        return subscriptionMapper.toResponse(saved);
    }
}

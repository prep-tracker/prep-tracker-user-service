package com.prept.tracker.controller;

import com.prept.tracker.dto.request.UpgradeRequest;
import com.prept.tracker.dto.response.PaymentResponse;
import com.prept.tracker.dto.response.SubscriptionResponse;
import com.prept.tracker.security.service.AuthorizationService;
import com.prept.tracker.service.PaymentService;
import com.prept.tracker.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@Tag(name = "Subscriptions", description = "Subscription and payment billing management endpoints")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final AuthorizationService authorizationService;

    @GetMapping("/me")
    @Operation(summary = "Get user subscription status", description = "Retrieve subscription plan details for currently authenticated user")
    public ResponseEntity<SubscriptionResponse> getSubscription() {
        Long userId = authorizationService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(subscriptionService.getSubscriptionByUserId(userId));
    }

    @PostMapping("/upgrade")
    @Operation(summary = "Upgrade plan level", description = "Upgrade plan level using mock card credentials")
    public ResponseEntity<SubscriptionResponse> upgradeSubscription(@Valid @RequestBody UpgradeRequest request) {
        Long userId = authorizationService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(subscriptionService.upgradeSubscription(userId, request));
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel plan level", description = "Revert back to FREE plan level")
    public ResponseEntity<SubscriptionResponse> cancelSubscription() {
        Long userId = authorizationService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(subscriptionService.cancelSubscription(userId));
    }

    @GetMapping("/payments")
    @Operation(summary = "Get user billing invoices history", description = "Get past payment transaction logs for currently authenticated user")
    public ResponseEntity<List<PaymentResponse>> getPaymentHistory() {
        Long userId = authorizationService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }
}

package com.prept.tracker.dto.response;

import com.prept.tracker.domain.enums.PlanType;
import com.prept.tracker.domain.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private PlanType planType;
    private SubscriptionStatus status;
    private Instant startDate;
    private Instant endDate;
}

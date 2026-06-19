package com.prept.tracker.dto.request;

import com.prept.tracker.domain.enums.PlanType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeRequest {

    @NotNull(message = "Plan type is required")
    private PlanType planType;

    private String cardholderName;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
}

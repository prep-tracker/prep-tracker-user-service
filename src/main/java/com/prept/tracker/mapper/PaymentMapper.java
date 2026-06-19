package com.prept.tracker.mapper;

import com.prept.tracker.domain.entity.Payment;
import com.prept.tracker.dto.response.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toResponse(Payment payment);
}

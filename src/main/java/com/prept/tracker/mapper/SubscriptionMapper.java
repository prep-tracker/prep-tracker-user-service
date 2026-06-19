package com.prept.tracker.mapper;

import com.prept.tracker.domain.entity.Subscription;
import com.prept.tracker.dto.response.SubscriptionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionResponse toResponse(Subscription subscription);
}

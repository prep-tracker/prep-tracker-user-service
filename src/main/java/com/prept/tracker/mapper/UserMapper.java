package com.prept.tracker.mapper;

import com.prept.tracker.domain.entity.User;
import com.prept.tracker.dto.request.RegisterRequest;
import com.prept.tracker.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {SubscriptionMapper.class})
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toResponse(User user);

    User toEntity(RegisterRequest request);

    @Named("rolesToStrings")
    default List<String> rolesToStrings(Set<com.prept.tracker.domain.entity.Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
    }
}

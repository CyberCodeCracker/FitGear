package com.amouri_coding.FitGear.user.admin;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AdminUserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String userType,
        List<String> roles,
        boolean accountEnabled,
        boolean accountLocked,
        LocalDateTime createdAt
) {}

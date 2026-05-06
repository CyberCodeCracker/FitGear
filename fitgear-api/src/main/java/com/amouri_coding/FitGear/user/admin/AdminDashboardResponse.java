package com.amouri_coding.FitGear.user.admin;

import lombok.Builder;

@Builder
public record AdminDashboardResponse(
        long totalClients,
        long totalCoaches,
        long clientsWithCoach,
        long clientsWithTrainingProgram,
        long clientsWithDietProgram,
        long totalTrainingPrograms,
        long totalDietPrograms
) {}

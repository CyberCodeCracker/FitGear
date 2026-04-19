package com.amouri_coding.FitGear.user.me;

import com.amouri_coding.FitGear.user.User;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.coach.Coach;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/me")
@Tag(name = "Me")
public class MeController {

    @GetMapping
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("Authentication required");
        }

        User user = (User) authentication.getPrincipal();
        List<String> roles = user.getAuthorities().stream().map(a -> a.getAuthority()).toList();

        CoachSummaryResponse coachSummary = null;
        String userType = "UNKNOWN";

        if (user instanceof Client client) {
            userType = "CLIENT";
            Coach coach = client.getCoach();
            if (coach != null) {
                coachSummary = CoachSummaryResponse.builder()
                        .id(coach.getId())
                        .fullName(coach.getName())
                        .monthlyRate(coach.getMonthlyRate())
                        .rating(coach.getRating())
                        .profilePicture(coach.getProfilePicture())
                        .build();
            }
            return ResponseEntity.ok(MeResponse.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .roles(roles)
                    .userType(userType)
                    .coach(coachSummary)
                    .height(client.getHeight())
                    .weight(client.getWeight())
                    .bodyFatPercentage(client.getBodyFatPercentage())
                    .build());
        } else if (user instanceof Coach coach) {
            userType = "COACH";
            return ResponseEntity.ok(MeResponse.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .roles(roles)
                    .userType(userType)
                    .rating(coach.getRating())
                    .monthlyRate(coach.getMonthlyRate())
                    .build());
        }

        return ResponseEntity.ok(MeResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(roles)
                .userType(userType)
                .coach(coachSummary)
                .build());
    }
}


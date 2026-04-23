package com.amouri_coding.FitGear.user.me;

import com.amouri_coding.FitGear.user.User;
import com.amouri_coding.FitGear.user.UserRepository;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import com.amouri_coding.FitGear.user.coach.CoachRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MeService {

    private final ClientRepository clientRepository;
    private final CoachRepository coachRepository;

    public MeResponse updateProfile(
            UpdateProfileRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        // Common fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        if (user instanceof Client client) {
            if (request.getHeight() != null)
                client.setHeight(request.getHeight());
            if (request.getWeight() != null)
                client.setWeight(request.getWeight());
            if (request.getBodyFatPercentage() != null)
                client.setBodyFatPercentage(request.getBodyFatPercentage());
            clientRepository.save(client);
            return buildClientResponse(client);

        } else if (user instanceof Coach coach) {
            if (request.getDescription() != null)
                coach.setDescription(request.getDescription());
            if (request.getPhoneNumber() != null)
                coach.setPhoneNumber(request.getPhoneNumber());
            if (request.getYearsOfExperience() != null)
                coach.setYearsOfExperience(request.getYearsOfExperience());
            if (request.getMonthlyRate() != null)
                coach.setMonthlyRate(request.getMonthlyRate());
            if (request.getIsAvailable() != null)
                coach.setAvailable(request.getIsAvailable());
            coachRepository.save(coach);
            return buildCoachResponse(coach);
        }

        throw new IllegalStateException("Unknown user type");
    }

    private MeResponse buildClientResponse(Client client) {
        var roles = client.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();

        CoachSummaryResponse coachSummary = null;
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
        return MeResponse.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .roles(roles)
                .userType("CLIENT")
                .coach(coachSummary)
                .height(client.getHeight())
                .weight(client.getWeight())
                .bodyFatPercentage(client.getBodyFatPercentage())
                .build();
    }

    private MeResponse buildCoachResponse(Coach coach) {
        var roles = coach.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();
        return MeResponse.builder()
                .id(coach.getId())
                .firstName(coach.getFirstName())
                .lastName(coach.getLastName())
                .email(coach.getEmail())
                .roles(roles)
                .userType("COACH")
                .rating(coach.getRating())
                .monthlyRate(coach.getMonthlyRate())
                .description(coach.getDescription())
                .phoneNumber(coach.getPhoneNumber())
                .yearsOfExperience(coach.getYearsOfExperience())
                .isAvailable(coach.isAvailable())
                .build();
    }
}

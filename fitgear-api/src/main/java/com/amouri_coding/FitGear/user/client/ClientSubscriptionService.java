package com.amouri_coding.FitGear.user.client;

import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.user.coach.Coach;
import com.amouri_coding.FitGear.user.coach.CoachRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientSubscriptionService {

    private final ClientRepository clientRepository;
    private final CoachRepository coachRepository;

    public ClientCoachResponse subscribe(Long coachId, Authentication authentication) {

        Client connectedClient = SecurityUtils.getAuthenticatedClient(authentication);
        Client client = clientRepository.findById(connectedClient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));

        if (!coach.isVerified() || !coach.isAvailable()) {
            throw new AccessDeniedException("Coach is not available");
        }

        client.setCoach(coach);
        clientRepository.save(client);

        return toClientCoachResponse(coach);
    }

    public ClientCoachResponse getMyCoach(Authentication authentication) {
        Client connectedClient = SecurityUtils.getAuthenticatedClient(authentication);
        Client client = clientRepository.findById(connectedClient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        Coach coach = client.getCoach();
        if (coach == null) {
            return null;
        }
        return toClientCoachResponse(coach);
    }

    public void unsubscribe(Authentication authentication) {
        Client connectedClient = SecurityUtils.getAuthenticatedClient(authentication);
        Client client = clientRepository.findById(connectedClient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        client.setCoach(null);
        clientRepository.save(client);
    }

    private ClientCoachResponse toClientCoachResponse(Coach coach) {
        return ClientCoachResponse.builder()
                .id(coach.getId())
                .fullName(coach.getName())
                .description(coach.getDescription())
                .monthlyRate(coach.getMonthlyRate())
                .rating(coach.getRating())
                .profilePicture(coach.getProfilePicture())
                .build();
    }
}


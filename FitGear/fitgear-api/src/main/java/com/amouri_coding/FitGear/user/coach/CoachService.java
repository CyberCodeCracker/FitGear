package com.amouri_coding.FitGear.user.coach;

import com.amouri_coding.FitGear.common.PageResponse;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientMapper;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.client.ClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoachService {

    private final CoachRepository coachRepository;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public PageResponse<ClientResponse> showAllClients(int page, int size, Authentication authentication) {

        if (authentication == null) {
            log.error("No authentication found");
            throw new AccessDeniedException("Authentication required");
        }
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COACH"))) {
            throw new AccessDeniedException("You are not a coach. Illegal operation");
        }

        Object principal = authentication.getPrincipal();
        Coach coach = ((Coach) principal);

        if (coach == null) {
            throw new IllegalStateException("No coach found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").descending());
        Page<Client> clients = clientRepository.findAllClientsByCoachId(pageable, coach.getId());
        List<ClientResponse> clientResponses = clients.stream()
                .map(clientMapper::toClientResponse)
                .toList()
                ;
        return new PageResponse<>(
                clientResponses,
                clients.getNumber(),
                clients.getSize(),
                clients.getTotalElements(),
                clients.getTotalPages(),
                clients.isFirst(),
                clients.isLast()
        );
    }

    public PageResponse<ClientResponse> showClientsByName(String clientName, int page, int size, Authentication authentication) {

        if (authentication == null) {
            log.error("No authentication found");
            throw new AccessDeniedException("Authentication required");
        }
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COACH"))) {
            throw new AccessDeniedException("You are not a coach. Illegal operation");
        }

        Object principal = authentication.getPrincipal();
        Coach coach = ((Coach) principal);

        if (coach == null) {
            throw new IllegalStateException("No coach found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").descending());
        Page<Client> clients = clientRepository.findAllClientsByName(clientName, pageable, coach.getId());
        List<ClientResponse> clientResponses = clients.stream()
                .map(clientMapper::toClientResponse)
                .toList()
                ;
        return new PageResponse<>(
                clientResponses,
                clients.getNumber(),
                clients.getSize(),
                clients.getTotalElements(),
                clients.getTotalPages(),
                clients.isFirst(),
                clients.isLast()
        );
    }
}

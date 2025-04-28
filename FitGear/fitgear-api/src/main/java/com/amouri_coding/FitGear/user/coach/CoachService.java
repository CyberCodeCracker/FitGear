package com.amouri_coding.FitGear.user.coach;

import com.amouri_coding.FitGear.common.PageResponse;
import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientMapper;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.client.ClientResponse;
import jakarta.persistence.EntityNotFoundException;
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

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public PageResponse<ClientResponse> showAllClients(int page, int size, Authentication authentication) {

        Coach connectedCoach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").descending());
        Page<Client> clients = clientRepository.findAllClientsByCoachId(pageable, connectedCoach.getId());
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

        Coach connectedCoach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").descending());
        Page<Client> clients = clientRepository.findAllClientsByName(clientName, pageable, connectedCoach.getId());
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

    public ClientResponse getClientID(Long clientId, Authentication authentication) {

        Coach connectedCoach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (!client.getCoach().equals(connectedCoach)) {
            throw new IllegalStateException("This isn't your client");
        }

        return clientMapper.toClientResponse(client);
    }
}

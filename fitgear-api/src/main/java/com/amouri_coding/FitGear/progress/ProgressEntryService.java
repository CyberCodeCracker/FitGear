package com.amouri_coding.FitGear.progress;

import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProgressEntryService {

    private final ProgressEntryRepository progressRepository;
    private final ClientRepository clientRepository;
    private final ProgressEntryMapper mapper;

    public List<ProgressEntryResponse> getAllEntries(Authentication authentication) {
        Client client = SecurityUtils.getAuthenticatedClient(authentication);
        return progressRepository.findAllByClientIdOrderByEntryDateAsc(client.getId())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ProgressEntryResponse createEntry(ProgressEntryRequest request, Authentication authentication) {
        Client client = SecurityUtils.getAuthenticatedClient(authentication);

        // Reload to get the managed entity
        Client managedClient = clientRepository.findById(client.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        ProgressEntry entry = ProgressEntry.builder()
                .client(managedClient)
                .entryDate(LocalDate.parse(request.getEntryDate()))
                .weight(request.getWeight())
                .bodyFat(request.getBodyFat())
                .muscleMass(request.getMuscleMass())
                .notes(request.getNotes())
                .build();

        ProgressEntry saved = progressRepository.save(entry);

        // Also update the client's current weight/bodyFat to the latest entry
        managedClient.setWeight(request.getWeight());
        managedClient.setBodyFatPercentage(request.getBodyFat());
        clientRepository.save(managedClient);

        log.info("Progress entry created for client {} on {}", client.getId(), request.getEntryDate());
        return mapper.toResponse(saved);
    }

    public void deleteEntry(Long entryId, Authentication authentication) {
        Client client = SecurityUtils.getAuthenticatedClient(authentication);

        ProgressEntry entry = progressRepository.findByIdAndClientId(entryId, client.getId())
                .orElseThrow(() -> new EntityNotFoundException("Progress entry not found"));

        progressRepository.delete(entry);
        log.info("Progress entry {} deleted for client {}", entryId, client.getId());
    }
}

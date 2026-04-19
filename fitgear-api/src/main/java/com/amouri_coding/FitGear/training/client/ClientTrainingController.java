package com.amouri_coding.FitGear.training.client;

import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.training.training_program.TrainingProgram;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramMapper;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramResponse;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clients/me/training")
@RequiredArgsConstructor
@Tag(name = "Client Training")
public class ClientTrainingController {

    private final ClientRepository clientRepository;
    private final TrainingProgramMapper trainingProgramMapper;

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @GetMapping("/program")
    public ResponseEntity<TrainingProgramResponse> myTrainingProgram(Authentication authentication) {
        Client connectedClient = SecurityUtils.getAuthenticatedClient(authentication);
        Client client = clientRepository.findWithTrainingProgramById(connectedClient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        TrainingProgram program = client.getTrainingProgram();
        if (program == null) {
            return ResponseEntity.ok(TrainingProgramResponse.builder().trainingDays(List.of()).build());
        }

        return ResponseEntity.ok(trainingProgramMapper.toTrainingProgramResponse(program));
    }
}


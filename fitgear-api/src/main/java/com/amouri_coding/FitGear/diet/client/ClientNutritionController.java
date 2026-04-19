package com.amouri_coding.FitGear.diet.client;

import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.diet.diet_program.DietProgram;
import com.amouri_coding.FitGear.diet.diet_program.DietProgramMapper;
import com.amouri_coding.FitGear.diet.diet_program.DietProgramResponse;
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

@RestController
@RequestMapping("/clients/me/nutrition")
@RequiredArgsConstructor
@Tag(name = "Client Nutrition")
public class ClientNutritionController {

    private final ClientRepository clientRepository;
    private final DietProgramMapper dietProgramMapper;

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @GetMapping("/program")
    public ResponseEntity<DietProgramResponse> myDietProgram(Authentication authentication) {
        Client connectedClient = SecurityUtils.getAuthenticatedClient(authentication);
        Client client = clientRepository.findById(connectedClient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        DietProgram program = client.getDietProgram();
        if (program == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dietProgramMapper.toDietProgramResponse(program));
    }
}

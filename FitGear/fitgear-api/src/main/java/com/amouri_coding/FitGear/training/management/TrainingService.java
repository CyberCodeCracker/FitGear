package com.amouri_coding.FitGear.training.management;

import com.amouri_coding.FitGear.training.training_program.TrainingProgram;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramMapper;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramRepository;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramRequest;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {

    private final ClientRepository clientRepository;
    private final TrainingProgramMapper trainingProgramMapper;
    private final TrainingProgramRepository trainingProgramRepository;

    public void assignProgram(Long clientId, @Valid TrainingProgramRequest request, Authentication authentication, HttpServletResponse response) {

        if (authentication == null) {
            log.error("No authentication found");
            throw new AccessDeniedException("Authentication required");
        }

        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COACH"))) {
            throw new AccessDeniedException("You are not a coach");
        }

        Object principal = authentication.getPrincipal();
        Coach coach = ((Coach) principal);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (!client.getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        TrainingProgram trainingProgram = trainingProgramMapper.toTrainingProgram(request, client, coach);
        trainingProgramRepository.save(trainingProgram);
    }
}

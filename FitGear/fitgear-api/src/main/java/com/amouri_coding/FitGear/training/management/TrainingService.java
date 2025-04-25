package com.amouri_coding.FitGear.training.management;

import com.amouri_coding.FitGear.training.exercise.Exercise;
import com.amouri_coding.FitGear.training.exercise.ExerciseRepository;
import com.amouri_coding.FitGear.training.training_day.TrainingDay;
import com.amouri_coding.FitGear.training.training_day.TrainingDayRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {

    private final ClientRepository clientRepository;
    private final TrainingProgramMapper trainingProgramMapper;
    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingDayRepository trainingDayRepository;
    private final ExerciseRepository exerciseRepository;

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

        try {
            TrainingProgram trainingProgram = trainingProgramMapper.toTrainingProgram(request, client, coach);

            List<TrainingDay> trainingDays = trainingProgram.getTrainingDays();
            List<Exercise> exercises = trainingDays.stream()
                    .flatMap(day -> day.getExercises().stream())
                    .collect(Collectors.toList());

            trainingProgramRepository.save(trainingProgram);
            trainingDayRepository.saveAll(trainingDays);
            exerciseRepository.saveAll(exercises);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }


    }
}

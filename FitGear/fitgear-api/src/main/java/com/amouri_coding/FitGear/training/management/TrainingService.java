package com.amouri_coding.FitGear.training.management;

import com.amouri_coding.FitGear.training.exercise.Exercise;
import com.amouri_coding.FitGear.training.exercise.ExerciseMapper;
import com.amouri_coding.FitGear.training.exercise.ExerciseRepository;
import com.amouri_coding.FitGear.training.training_day.*;
import com.amouri_coding.FitGear.training.training_program.*;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import com.amouri_coding.FitGear.user.coach.CoachRepository;
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
    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingDayRepository trainingDayRepository;
    private final ExerciseRepository exerciseRepository;

    private final TrainingProgramMapper trainingProgramMapper;
    private final TrainingDayMapper trainingDayMapper;
    private final ExerciseMapper exerciseMapper;
    private final CoachRepository coachRepository;

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
            throw new IllegalStateException("This client isn't yours");
        }

        try {
            TrainingProgram trainingProgram = trainingProgramMapper.toTrainingProgram(request, client, coach);

            List<TrainingDay> trainingDays = trainingProgram.getTrainingDays();
            List<Exercise> exercises = trainingDays.stream()
                    .flatMap(day -> day.getExercises().stream())
                    .collect(Collectors.toList())
                    ;

            client.setTrainingProgram(trainingProgram);

            trainingProgramRepository.save(trainingProgram);
            trainingDayRepository.saveAll(trainingDays);
            exerciseRepository.saveAll(exercises);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }


    }

    public TrainingProgramResponse getProgramOfClient(Long clientId, Long programId, Authentication authentication) {

        if (authentication == null) {
            log.error("No authentication found");
            throw new AccessDeniedException("Authentication required");
        }

        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COACH"))) {
            log.error("You are not a coach");
            throw new AccessDeniedException("You are not a coach");
        }

        Object principal = authentication.getPrincipal();
        Coach coach = ((Coach) principal);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        TrainingProgram trainingProgram = trainingProgramRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Training program not found"));

        if (!client.getCoach().getId().equals(coach.getId())) {
            throw new IllegalStateException("This client isn't yours");
        }

        if (client.getTrainingProgram() == null) {
            throw new IllegalStateException("Client has no training program assigned.");
        }
        if (!client.getTrainingProgram().getId().equals(trainingProgram.getId())) {
            throw new IllegalStateException("This training program doesn't belong to this client");
        }

        TrainingProgramResponse trainingProgramResponse = trainingProgramMapper.toTrainingProgramResponse(trainingProgram);
        return trainingProgramResponse;

    }

    public TrainingDayResponse editTrainingDay(Long dayId, Long clientId, Long coachId, Long programId, TrainingDayRequest request, Authentication authentication) {

        if (authentication == null) {
            log.error("No authentication found");
            throw new AccessDeniedException("Authentication required");
        }

        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COACH"))) {
            log.error("You are not a coach");
            throw new AccessDeniedException("You are not a coach");
        }

        Object principal = authentication.getPrincipal();
        Coach coach = ((Coach) principal);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (!client.getCoach().getId().equals(coach.getId())) {
            throw new IllegalStateException("This client isn't yours");
        }

        TrainingProgram trainingProgram = trainingProgramRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Training program not found"));

        if (!client.getTrainingProgram().getId().equals(trainingProgram.getId())) {
            throw new IllegalStateException("This training program doesn't belong to this client");
        }

        TrainingDay trainingDay = trainingDayRepository.findById(dayId)
                .orElseThrow(() -> new EntityNotFoundException("Training Day not found"));

        if (!trainingDay.getTrainingProgram().getId().equals(trainingProgram.getId())) {
            throw new IllegalStateException("This training day isn't part of this training program");
        }

        trainingDay.setTitle(request.getTitle());
        trainingDay.setDayOfWeek(request.getDay());
        trainingDay.setEstimatedBurnedCalories(request.getEstimatedBurnedCalories());

        trainingDayRepository.save(trainingDay);
        return trainingDayMapper.toTrainingDayResponse(trainingDay);
    }

    public void deleteExercise(Long exerciseId, Long trainingDayId, Long coachId) {

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        TrainingDay trainingDay = trainingDayRepository.findById(trainingDayId)
                .orElseThrow(() -> new EntityNotFoundException("Training Day not found"));

        if (!exercise.getTrainingDay().getId().equals(trainingDay.getId())) {
            throw new IllegalStateException("This exercise doesn't belong to this training day");
        }

        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingDay.getTrainingProgram().getId())
                .orElseThrow(() -> new EntityNotFoundException("Training Program not found"));

        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));

        if (!trainingProgram.getCoach().getId().equals(coach.getId())) {
            throw new IllegalStateException("This training program doesn't belong to this coach");
        }

        exerciseRepository.delete(exercise);
    }

    public void deleteTrainingDay(Long trainingDayId, Long coachId) {

        TrainingDay trainingDay = trainingDayRepository.findById(trainingDayId)
                .orElseThrow(() -> new EntityNotFoundException("Training Day not found"));

        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));

        TrainingProgram trainingProgram = trainingDay.getTrainingProgram();

        if (!trainingProgram.getCoach().getId().equals(coach.getId())) {
            throw new IllegalStateException("This training program doesn't belong to this coach");
        }

        trainingDayRepository.delete(trainingDay);
    }

    public void deleteTrainingProgram(Long programId, Long coachId) {

        TrainingProgram trainingProgram = trainingProgramRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Training Program not found"));

        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));

        if (!trainingProgram.getCoach().getId().equals(coach.getId())) {
            throw new IllegalStateException("This training program doesn't belong to this coach");
        }

        trainingProgramRepository.delete(trainingProgram);
    }
}

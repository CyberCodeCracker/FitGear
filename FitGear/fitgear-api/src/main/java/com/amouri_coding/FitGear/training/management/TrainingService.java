package com.amouri_coding.FitGear.training.management;

import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.training.exercise.*;
import com.amouri_coding.FitGear.training.training_day.*;
import com.amouri_coding.FitGear.training.training_program.*;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public void assignProgram(Long clientId, @Valid TrainingProgramRequest request, Authentication authentication) {
        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = getClient(clientId);

        if (!client.getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("You can't assign a program to a client you don't coach");
        }

        TrainingProgram program = trainingProgramMapper.toTrainingProgram(request, client, coach);
        List<TrainingDay> days = program.getTrainingDays();
        List<Exercise> exercises = days.stream()
                .flatMap(day -> day.getExercises().stream())
                .collect(Collectors.toList());

        client.setTrainingProgram(program);

        try {
            trainingProgramRepository.save(program);
            trainingDayRepository.saveAll(days);
            exerciseRepository.saveAll(exercises);
        } catch (Exception e) {
            log.error("Failed to assign program: {}", e.getMessage());
            throw new RuntimeException("Something went wrong while saving the program");
        }
    }

    public void addTrainingDay(Long programId, Long clientId, @Valid TrainingDayRequest request, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = getClient(clientId);
        TrainingProgram program = client.getTrainingProgram();

        if (!client.getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (program == null || !program.getId().equals(programId)) {
            throw new IllegalStateException("Program not associated with client");
        }

        if (request.getDay().equals(program.getTrainingDays().stream().peek(trainingDay -> trainingDay.getDayOfWeek()))) {
            throw new IllegalArgumentException("This training day already exists");
        }

        TrainingDay trainingDay = trainingDayMapper.toTrainingDay(request);

        program.getTrainingDays().add(trainingDay);

        program.getTrainingDays().sort((d1, d2) -> d1.getDayOfWeek().compareTo(d2.getDayOfWeek()));

        trainingDayRepository.save(trainingDay);
        exerciseRepository.saveAll(trainingDay.getExercises());
    }

    public void addExercise(Long dayId, Long clientId,@Valid ExerciseRequest request , Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = getClient(clientId);
        TrainingDay trainingDay = getTrainingDay(dayId);
        TrainingProgram program = client.getTrainingProgram();

        if (!client.getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!trainingDay.getTrainingProgram().getId().equals(program.getId())) {
            throw new IllegalStateException("This training day isn't associated with program");
        }

        Exercise exercise = exerciseMapper.toExercise(request);
        int exercisePosition = exercise.getNumber();

        List<Exercise> exercisesFromTrainingDay = trainingDay.getExercises();
        if (exercisesFromTrainingDay.isEmpty()) {
            exercisesFromTrainingDay = new ArrayList<>();
            trainingDay.setExercises(exercisesFromTrainingDay);
        }

        if (exercisePosition < 0) {
            throw new IllegalArgumentException("Invalid exercise position");
        }

        if (exercisesFromTrainingDay.isEmpty() || exercisePosition >= exercisesFromTrainingDay.size()) {
            exercisePosition = exercisesFromTrainingDay.size();
        }

        exercisesFromTrainingDay.add(exercisePosition, exercise);

        exercise.setTrainingDay(trainingDay);
        exerciseRepository.save(exercise);
    }

    public TrainingProgramResponse getProgramOfClient(Long clientId, Long programId, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = getClient(clientId);
        TrainingProgram program = client.getTrainingProgram();

        if (!client.getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (client.getTrainingProgram() == null || !program.getId().equals(programId)) {
            throw new IllegalStateException("This program is not assigned to this client");
        }

        return trainingProgramMapper.toTrainingProgramResponse(program);
    }

    public TrainingDayResponse getDayOfClient(Long dayId, Long clientId, Long programId,  Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = getClient(clientId);
        TrainingProgram trainingProgram = client.getTrainingProgram();
        TrainingDay trainingDay = getTrainingDay(dayId);

        if (!client.getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!trainingProgram.getId().equals(trainingDay.getTrainingProgram().getId()) || !trainingProgram.getId().equals(programId)) {
            throw new IllegalStateException("This training day isn't associated with program");
        }

        TrainingDayResponse trainingDayResponse = trainingDayMapper.toTrainingDayResponse(trainingDay);
        return trainingDayResponse;
    }

    public ExerciseResponse getExerciseOfClient(Long exerciseId, Long dayId, Long clientId, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = getClient(clientId);
        TrainingProgram trainingProgram = client.getTrainingProgram();
        TrainingDay trainingDay = getTrainingDay(dayId);
        Exercise exercise = getExercise(exerciseId);

        if (!client.getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!clientId.equals(exercise.getTrainingDay().getTrainingProgram().getClient().getId())) {
            throw new IllegalStateException("This client isn't associated with exercise");
        }

        if (!trainingProgram.getId().equals(trainingDay.getTrainingProgram().getId())) {
            throw new IllegalStateException("This training day isn't associated with program");
        }

        if (!exercise.getTrainingDay().getId().equals(dayId) || ! exercise.getTrainingDay().getTrainingProgram().getId().equals(trainingProgram.getId())) {
            throw new IllegalStateException("This exercise is not associated with selected day");
        }

        ExerciseResponse exerciseResponse = exerciseMapper.toExerciseResponse(exercise);
        return exerciseResponse;
    }

    public TrainingDayResponse editTrainingDay(Long dayId, Long clientId, Long programId, @Valid TrainingDayRequest request, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = getClient(clientId);
        TrainingProgram program = getProgram(programId);
        TrainingDay day = getTrainingDay(dayId);

        if (!client.getCoach().getId().equals(coach.getId()) || !client.getTrainingProgram().getId().equals(program.getId()) || !day.getTrainingProgram().getId().equals(program.getId())) {
            throw new AccessDeniedException("You don't have permission to edit this day");
        }

        day.setTitle(request.getTitle());
        day.setDayOfWeek(request.getDay());
        day.setEstimatedBurnedCalories(request.getEstimatedBurnedCalories());

        List<Exercise> exercises = request.getExercises().stream()
                .map(ex -> exerciseMapper.toExercise(ex))
                .peek(ex -> ex.setTrainingDay(day))
                .toList()
                ;

        day.getExercises().clear();
        day.getExercises().addAll(exercises);

        trainingDayRepository.save(day);
        return trainingDayMapper.toTrainingDayResponse(day);
    }

    public ExerciseResponse editExercise(Long exerciseId, Long clientId, Long dayId, @Valid ExerciseRequest request, Authentication authentication) {
        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Exercise exercise = getExercise(exerciseId);
        TrainingDay day = getTrainingDay(dayId);

        if (!exercise.getTrainingDay().getId().equals(dayId)) {
            throw new IllegalStateException("Exercise doesn't belong to this day");
        }

        if (!day.getTrainingProgram().getClient().getId().equals(clientId) || !day.getTrainingProgram().getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("You dont have permission to edit this exercise");
        }

        exercise.setTitle(request.getTitle());
        exercise.setNumber(exercise.getNumber());
        exercise.setExerciseUrl(request.getExerciseUrl());
        exercise.setNumberOfSets(request.getNumberOfSets());
        exercise.setNumberOfReps(request.getNumberOfReps());
        exercise.setRestTime(request.getRestTime());

        exerciseRepository.save(exercise);
        return exerciseMapper.toExerciseResponse(exercise);
    }

    public void deleteExercise(Long exerciseId, Long dayId, Authentication authentication) {
        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Exercise exercise = getExercise(exerciseId);
        TrainingDay day = getTrainingDay(dayId);

        if (!exercise.getTrainingDay().getId().equals(dayId)) {
            throw new IllegalStateException("Exercise doesn't belong to this day");
        }

        if (!day.getTrainingProgram().getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("You can't delete exercises from a client that isn't yours");
        }

        exerciseRepository.delete(exercise);
    }

    public void deleteTrainingDay(Long dayId, Authentication authentication) {
        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        TrainingDay day = getTrainingDay(dayId);

        if (!day.getTrainingProgram().getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("You can't delete training days from a client that isn't yours");
        }

        trainingDayRepository.delete(day);
    }

    public void deleteTrainingProgram(Long programId, Authentication authentication) {
        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        TrainingProgram program = getProgram(programId);

        if (!program.getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("You can't delete a program you didn't create");
        }

        trainingProgramRepository.delete(program);
    }

    private Client getClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }

    private TrainingProgram getProgram(Long programId) {
        return trainingProgramRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Training Program not found"));
    }

    private TrainingDay getTrainingDay(Long dayId) {
        return trainingDayRepository.findById(dayId)
                .orElseThrow(() -> new EntityNotFoundException("Training Day not found"));
    }

    private Exercise getExercise(Long exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));
    }

}

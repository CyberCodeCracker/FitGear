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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
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

        if (!findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("You can't assign a program to a client you don't coach");
        }

        TrainingProgram program = trainingProgramMapper.toTrainingProgram(request, client, coach);
        program.setCreatedAt(LocalDateTime.now());
        List<TrainingDay> days = program.getTrainingDays();
        List<Exercise> exercises = days.stream()
                .flatMap(day -> day.getExercises().stream())
                .collect(Collectors.toList())
                ;

        for (TrainingDay day : days) {
            day.setCreatedAt(LocalDateTime.now());
            for (Exercise exercise : day.getExercises()) {
                exercise.setTrainingDay(day);
                exercise.setCreatedAt(LocalDateTime.now());
            }
            log.info("TrainingDays list class: {}", program.getTrainingDays().getClass().getName());
            log.info("Exercises list class: {}", day.getExercises().getClass().getName());
        }

        client.setTrainingProgram(program);

        try {
            trainingProgramRepository.save(program);
        } catch (Exception e) {
            log.error("Failed to assign program: {}", e);
            throw new RuntimeException("Something went wrong while saving the program");
        }
    }

    public void addTrainingDay(Long programId, Long clientId, @Valid TrainingDayRequest request, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = getClient(clientId);
        TrainingProgram program = client.getTrainingProgram();

        if (!findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (program == null || !program.getId().equals(programId)) {
            throw new IllegalStateException("Program not associated with client");
        }

        if (program.getTrainingDays().stream()
                .anyMatch(trainingDay -> trainingDay.getDayOfWeek().equals(request.getDay()))) {
            throw new IllegalStateException("This day is already filled");
        }

        TrainingDay trainingDay = trainingDayMapper.toTrainingDay(request);

        trainingDay.setCreatedAt(LocalDateTime.now());

        program.getTrainingDays().add(trainingDay);

        program.getTrainingDays().sort((d1, d2) -> d1.getDayOfWeek().compareTo(d2.getDayOfWeek()));

        program.setUpdatedAt(LocalDateTime.now());

        trainingDayRepository.save(trainingDay);
        exerciseRepository.saveAll(trainingDay.getExercises());
    }

    public void addExercise(Long programId,
                            Long dayId,
                            Long clientId,
                            @Valid ExerciseRequest request,
                            Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = getClient(clientId);

        if (!findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        TrainingProgram program = client.getTrainingProgram();
        if (program == null || !program.getId().equals(programId)) {
            throw new IllegalStateException("Program not associated with this client");
        }

        TrainingDay trainingDay = getTrainingDay(dayId);
        if (!trainingDay.getTrainingProgram().getId().equals(programId)) {
            throw new IllegalStateException("This training day isn't associated with the specified program");
        }

        Exercise exercise = exerciseMapper.toExercise(request);
        int position = request.getExerciseNumber();
        List<Exercise> exercises = trainingDay.getExercises();
        if (exercises == null) {
            exercises = new ArrayList<>();
            trainingDay.setExercises(exercises);
        }
        if (position < 0) {
            throw new IllegalArgumentException("Invalid exercise position");
        }
        if (position > exercises.size()) {
            position = exercises.size();
        }
        exercises.add(position, exercise);

        LocalDateTime now = LocalDateTime.now();
        exercise.setCreatedAt(now);
        trainingDay.setUpdatedAt(now);
        exercise.setTrainingDay(trainingDay);

        exerciseRepository.save(exercise);
    }

    public TrainingProgramResponse getProgramOfClient(Long clientId, Long programId, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = getClient(clientId);
        TrainingProgram program = client.getTrainingProgram();

        if (!findCoachIdByClientId(clientId).equals(coach.getId())) {
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

        if (!findCoachIdByClientId(clientId).equals(coach.getId())) {
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
        Exercise exercise = getExercise(exerciseId);

        if (!findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!clientId.equals(findClientIdByProgramId(findProgramIdByDayId(findDayIdByExerciseId(exerciseId))))) {
            throw new IllegalStateException("This client isn't associated with exercise");
        }

        if (!trainingProgram.getId().equals(findProgramIdByDayId(dayId))) {
            throw new IllegalStateException("This training day isn't associated with program");
        }

        if (!findDayIdByExerciseId(exerciseId).equals(dayId) || ! exercise.getTrainingDay().getTrainingProgram().getId().equals(trainingProgram.getId())) {
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

        if (!findCoachIdByClientId(clientId).equals(coach.getId()) || !client.getTrainingProgram().getId().equals(program.getId()) || !day.getTrainingProgram().getId().equals(program.getId())) {
            throw new AccessDeniedException("You don't have permission to edit this day");
        }

        day.setTitle(request.getTitle());
        day.setDayOfWeek(request.getDay());
        day.setEstimatedBurnedCalories(request.getEstimatedBurnedCalories());
        day.setUpdatedAt(LocalDateTime.now());
        program.setUpdatedAt(LocalDateTime.now());

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

        if (!findDayIdByExerciseId(exerciseId).equals(dayId)) {
            throw new IllegalStateException("Exercise doesn't belong to this day");
        }

        if (!findClientIdByProgramId(findProgramIdByDayId(dayId)).equals(clientId) || !day.getTrainingProgram().getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("You don't have permission to edit this exercise");
        }

        exercise.setTitle(request.getTitle());
        exercise.setNumber(exercise.getNumber());
        exercise.setExerciseUrl(request.getExerciseUrl());
        exercise.setNumberOfSets(request.getNumberOfSets());
        exercise.setNumberOfReps(request.getNumberOfReps());
        exercise.setRestTime(request.getRestTime());

        day.setUpdatedAt(LocalDateTime.now());

        TrainingProgram program = exercise.getTrainingDay().getTrainingProgram();
        program.setUpdatedAt(LocalDateTime.now());

        exerciseRepository.save(exercise);
        return exerciseMapper.toExerciseResponse(exercise);
    }

    public void deleteExercise(Long exerciseId, Long clientId,Long dayId, Long programId, Authentication authentication) {
        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Exercise exercise = getExercise(exerciseId);
        TrainingDay day = getTrainingDay(dayId);

        if (!findDayIdByExerciseId(exerciseId).equals(dayId)) {
            throw new IllegalStateException("Exercise doesn't belong to this day");
        }

        if (!findProgramIdByDayId(dayId).equals(programId)) {
            throw new IllegalStateException("Exercise doesn't belong to this program");
        }

        if (!getProgram(findProgramIdByDayId(dayId)).getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("You can't delete exercises from a client that isn't yours");
        }

        if (!findClientIdByProgramId(findProgramIdByDayId(dayId)).equals(clientId)) {
            throw new IllegalStateException("Exercise doesn't belong to this client");
        }

        day.setUpdatedAt(LocalDateTime.now());

        TrainingProgram program = day.getTrainingProgram();
        program.setUpdatedAt(LocalDateTime.now());

        exerciseRepository.delete(exercise);
    }

    public void deleteTrainingDay(Long dayId,Long programId, Long clientId , Authentication authentication) {
        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        TrainingDay day = getTrainingDay(dayId);

        if (!day.getTrainingProgram().getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("You can't delete training days from a client that isn't yours");
        }

        if (!findProgramIdByDayId(dayId).equals(programId)) {
            throw new IllegalStateException("Day doesn't belong to this program");
        }

        if (!findClientIdByProgramId(findProgramIdByDayId(dayId)).equals(clientId)) {
            throw new AccessDeniedException("You can't delete a day from a program that isn't yours");
        }

        TrainingProgram program = day.getTrainingProgram();
        program.setUpdatedAt(LocalDateTime.now());

        trainingDayRepository.delete(day);
    }

    public void deleteTrainingProgram(Long programId, Long clientId, Authentication authentication) {
        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        TrainingProgram program = getProgram(programId);

        if (!program.getCoach().getId().equals(coach.getId())) {
            throw new AccessDeniedException("You can't delete a program you didn't create");
        }

        if (!program.getClient().getId().equals(clientId)) {
            throw new AccessDeniedException("this program isn't yours");
        }

        Client client = program.getClient();
        client.setTrainingProgram(null);
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

    private Long findCoachIdByClientId(Long clientId) {
        return clientRepository.findCoachIdByClientId(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client still not associated with coach"));
    }

    private Long findClientIdByProgramId(Long programId) {
        return trainingProgramRepository.findClientIdByProgramId(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not associated with a client"));
    }

    private Long findProgramIdByDayId(Long trainingDayId) {
        return trainingDayRepository.findProgramIdByDayId(trainingDayId)
                .orElseThrow(() -> new EntityNotFoundException("Training day not associated with a program"));
    }

    private Long findDayIdByExerciseId(Long exerciseId) {
        return exerciseRepository.findDayIdByExerciseId(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise not associated with a day"));
    }

}

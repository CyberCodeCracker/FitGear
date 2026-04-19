package com.amouri_coding.FitGear.training.performance;

import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.training.exercise.Exercise;
import com.amouri_coding.FitGear.training.exercise.ExerciseRepository;
import com.amouri_coding.FitGear.user.client.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExercisePerformanceService {

    private final ExercisePerformanceRepository repository;
    private final ExerciseRepository exerciseRepository;
    private final EntityManager entityManager;

    public List<ExercisePerformanceResponse> list(Long exerciseId, Authentication authentication) {
        Client client = SecurityUtils.getAuthenticatedClient(authentication);
        if (!exerciseRepository.existsByExerciseIdAndClientId(exerciseId, client.getId())) {
            throw new AccessDeniedException("Exercise does not belong to the authenticated client");
        }

        return repository.findForClientAndExercise(client.getId(), exerciseId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ExercisePerformanceResponse add(Long exerciseId, ExercisePerformanceRequest request, Authentication authentication) {
        Client client = SecurityUtils.getAuthenticatedClient(authentication);
        if (!exerciseRepository.existsByExerciseIdAndClientId(exerciseId, client.getId())) {
            throw new AccessDeniedException("Exercise does not belong to the authenticated client");
        }

        Exercise exercise = entityManager.getReference(Exercise.class, exerciseId);
        Client clientRef = entityManager.getReference(Client.class, client.getId());

        ExercisePerformance performance = ExercisePerformance.builder()
                .client(clientRef)
                .exercise(exercise)
                .performedAt(request.getPerformedAt() != null ? request.getPerformedAt() : LocalDate.now())
                .numberOfSets(request.getNumberOfSets())
                .numberOfReps(request.getNumberOfReps())
                .weight(request.getWeight())
                .notes(request.getNotes())
                .build();

        ExercisePerformance saved = repository.save(performance);
        return toResponse(saved);
    }

    private ExercisePerformanceResponse toResponse(ExercisePerformance performance) {
        Exercise exercise = performance.getExercise();
        if (exercise == null) {
            throw new EntityNotFoundException("Exercise not found for performance");
        }

        return ExercisePerformanceResponse.builder()
                .id(performance.getId())
                .exerciseId(exercise.getId())
                .exerciseTitle(exercise.getTitle())
                .performedAt(performance.getPerformedAt())
                .numberOfSets(performance.getNumberOfSets())
                .numberOfReps(performance.getNumberOfReps())
                .weight(performance.getWeight())
                .notes(performance.getNotes())
                .build();
    }
}


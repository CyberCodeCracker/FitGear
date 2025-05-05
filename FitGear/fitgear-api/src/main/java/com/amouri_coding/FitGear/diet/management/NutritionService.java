package com.amouri_coding.FitGear.diet.management;

import com.amouri_coding.FitGear.common.EntityUtils;
import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.diet.diet_day.DietDay;
import com.amouri_coding.FitGear.diet.diet_day.DietDayMapper;
import com.amouri_coding.FitGear.diet.diet_day.DietDayRepository;
import com.amouri_coding.FitGear.diet.diet_day.DietDayRequest;
import com.amouri_coding.FitGear.diet.diet_program.*;
import com.amouri_coding.FitGear.diet.meal.Meal;
import com.amouri_coding.FitGear.diet.meal.MealMapper;
import com.amouri_coding.FitGear.diet.meal.MealRepository;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NutritionService {

    private final ClientRepository clientRepository;
    private final DietProgramRepository dietProgramRepository;
    private final DietDayRepository dietDayRepository;
    private final MealRepository mealRepository;

    private final EntityUtils entityUtils;

    private final DietProgramMapper programMapper;
    private final DietDayMapper dietDayMapper;
    private final MealMapper mealMapper;

    public void assignDietProgram(Long clientId, @Valid DietProgramRequest request, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = entityUtils.getClient(clientId);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (client.getDietProgram() != null) {
            DietProgram oldProgram = client.getDietProgram();
            oldProgram.setClient(null);
            client.setDietProgram(null);
            dietProgramRepository.delete(oldProgram);
            dietProgramRepository.flush();
        }

        DietProgram program = programMapper.toDietProgram(request, clientId, coach.getId());
        program.setCreatedAt(LocalDateTime.now());

        List<DietDay> days = program.getDays();

        for (DietDay day : days) {
            day.setCreatedAt(LocalDateTime.now());
            for (Meal meal : day.getMeals()) {
                meal.setCreatedAt(LocalDateTime.now());
            }
        }

        dietProgramRepository.save(program);
    }

    public DietProgramResponse getDietProgram(Long clientId, Long programId, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }

        DietProgram program = entityUtils.getDietProgram(programId);
        DietProgramResponse programResponse = programMapper.toDietProgramResponse(program);
        return programResponse;
    }

    public void editDietProgram(Long clientId, Long programId, DietProgramRequest request, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }



    }

    public void deleteDietProgram(Long clientId, Long programId, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = entityUtils.getClient(clientId);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }

        dietProgramRepository.delete(entityUtils.getDietProgram(programId));
        client.setDietProgram(null);
    }

    public void addDietDay(Long clientId, Long programId, @Valid DietDayRequest request, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }

        DietProgram program = entityUtils.getDietProgram(programId);

        if (program.getDays().stream().peek(dietDay -> dietDay.getDayOfWeek()).equals(request.getDayOfWeek())) {
            throw new IllegalStateException("This day already exists");
        }

        DietDay day = dietDayMapper.toDietDay(request);
        day.setCreatedAt(LocalDateTime.now());
        program.getDays().add(day);
        program.setUpdatedAt(LocalDateTime.now());
        program.getDays().sort((d1,d2) -> d1.getDayOfWeek().compareTo(d2.getDayOfWeek()));
        dietDayRepository.save(day);
        mealRepository.saveAll(day.getMeals());
    }
}

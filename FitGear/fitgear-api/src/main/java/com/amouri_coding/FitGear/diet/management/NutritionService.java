package com.amouri_coding.FitGear.diet.management;

import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.diet.diet_day.DietDay;
import com.amouri_coding.FitGear.diet.diet_day.DietDayMapper;
import com.amouri_coding.FitGear.diet.diet_day.DietDayRepository;
import com.amouri_coding.FitGear.diet.diet_program.DietProgram;
import com.amouri_coding.FitGear.diet.diet_program.DietProgramMapper;
import com.amouri_coding.FitGear.diet.diet_program.DietProgramRepository;
import com.amouri_coding.FitGear.diet.diet_program.DietProgramRequest;
import com.amouri_coding.FitGear.diet.meal.Meal;
import com.amouri_coding.FitGear.diet.meal.MealMapper;
import com.amouri_coding.FitGear.diet.meal.MealRepository;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import jakarta.persistence.EntityNotFoundException;
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

    private final DietProgramMapper programMapper;
    private final DietDayMapper dietDayMapper;
    private final MealMapper mealMapper;

    public void assignDietingProgram(Long clientId, @Valid DietProgramRequest request, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (!findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (client.getDietProgram() != null) {
            DietProgram oldProgram = client.getDietProgram();
            client.setDietProgram(null);
            dietProgramRepository.delete(oldProgram);
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

    private Client getClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }

    private DietProgram getDietProgram(Long dietProgramId) {
        return dietProgramRepository.findById(dietProgramId)
                .orElseThrow(() -> new EntityNotFoundException("Diet program not found"));
    }

    private DietDay getDietDay(Long dietDayId) {
        return  dietDayRepository.findById(dietDayId)
                .orElseThrow(() -> new EntityNotFoundException("Diet day not found"));
    }

    private Long findCoachIdByClientId(Long clientId) {
        return clientRepository.findCoachIdByClientId(clientId)
                .orElseThrow(() -> new EntityNotFoundException("This client has no coach yet"));
    }

    private Long findClientIdByDietProgramId(Long dietProgramId) {
        return dietProgramRepository.findClientIdByDietProgramId(dietProgramId)
                .orElseThrow(() -> new EntityNotFoundException("This diet program isn't associated with a client"));
    }

    private Long findProgramIdByDietDayId(Long dietDayId) {
        return dietDayRepository.findDietProgramIdByDayId(dietDayId)
                .orElseThrow(() -> new EntityNotFoundException("This diet day isn't associated with a dieting program"));
    }

    private Long findClientIdByDietDayId(Long dietDayId) {
        return dietDayRepository.findClientIdByDayId(dietDayId)
                .orElseThrow(() -> new EntityNotFoundException("This diet day isn't associated with a client"));
    }

    private Long findDayIdByMealId(Long mealId) {
        return mealRepository.findDietDayIdFromMealId(mealId)
                .orElseThrow(() -> new EntityNotFoundException("This meal isn't associated with a dieting day"));
    }

    private Long findProgramIdByMealId(Long mealId) {
        return mealRepository.findDietProgramIdFromMealId(mealId)
                .orElseThrow(() -> new EntityNotFoundException("This meal isn't associated with a dieting program"));
    }

    private Long findClientIdByMealId(Long mealId) {
        return mealRepository.findClientIdByMealId(mealId)
                .orElseThrow(() -> new EntityNotFoundException("This meal isn't associated with a client"));
    }
}

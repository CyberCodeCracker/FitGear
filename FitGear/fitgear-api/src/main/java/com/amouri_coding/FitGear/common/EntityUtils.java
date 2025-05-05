package com.amouri_coding.FitGear.common;

import com.amouri_coding.FitGear.diet.diet_day.DietDay;
import com.amouri_coding.FitGear.diet.diet_day.DietDayRepository;
import com.amouri_coding.FitGear.diet.diet_program.DietProgram;
import com.amouri_coding.FitGear.diet.diet_program.DietProgramRepository;
import com.amouri_coding.FitGear.diet.meal.MealRepository;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntityUtils {

    private final ClientRepository clientRepository;
    private final DietProgramRepository dietProgramRepository;
    private final DietDayRepository dietDayRepository;
    private final MealRepository mealRepository;

    public Client getClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }

    public DietProgram getDietProgram(Long dietProgramId) {
        return dietProgramRepository.findById(dietProgramId)
                .orElseThrow(() -> new EntityNotFoundException("Diet program not found"));
    }

    public DietDay getDietDay(Long dietDayId) {
        return  dietDayRepository.findById(dietDayId)
                .orElseThrow(() -> new EntityNotFoundException("Diet day not found"));
    }

    public Long findCoachIdByClientId(Long clientId) {
        return clientRepository.findCoachIdByClientId(clientId)
                .orElseThrow(() -> new EntityNotFoundException("This client has no coach yet"));
    }

    public Long findClientIdByDietProgramId(Long dietProgramId) {
        return dietProgramRepository.findClientIdByDietProgramId(dietProgramId)
                .orElseThrow(() -> new EntityNotFoundException("This diet program isn't associated with a client"));
    }

    public Long findProgramIdByDietDayId(Long dietDayId) {
        return dietDayRepository.findDietProgramIdByDayId(dietDayId)
                .orElseThrow(() -> new EntityNotFoundException("This diet day isn't associated with a dieting program"));
    }

    public Long findClientIdByDietDayId(Long dietDayId) {
        return dietDayRepository.findClientIdByDayId(dietDayId)
                .orElseThrow(() -> new EntityNotFoundException("This diet day isn't associated with a client"));
    }

    public Long findDayIdByMealId(Long mealId) {
        return mealRepository.findDietDayIdFromMealId(mealId)
                .orElseThrow(() -> new EntityNotFoundException("This meal isn't associated with a dieting day"));
    }

    public Long findProgramIdByMealId(Long mealId) {
        return mealRepository.findDietProgramIdFromMealId(mealId)
                .orElseThrow(() -> new EntityNotFoundException("This meal isn't associated with a dieting program"));
    }

    public Long findClientIdByMealId(Long mealId) {
        return mealRepository.findClientIdByMealId(mealId)
                .orElseThrow(() -> new EntityNotFoundException("This meal isn't associated with a client"));
    }
}

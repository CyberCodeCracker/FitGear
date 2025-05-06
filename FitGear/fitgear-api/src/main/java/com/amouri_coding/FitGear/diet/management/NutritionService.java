package com.amouri_coding.FitGear.diet.management;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.common.EntityUtils;
import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.diet.diet_day.*;
import com.amouri_coding.FitGear.diet.diet_program.*;
import com.amouri_coding.FitGear.diet.meal.*;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
        Set<DayOfWeek> seenDays = new HashSet<>();

        for (DietDay day : days) {
            log.info("Calories {}", day.getTotalCaloriesInDay());
            if (!seenDays.add(day.getDayOfWeek())) {
                throw new IllegalArgumentException(
                        "Duplicate day found in diet program: "
                                + day.getDayOfWeek() +
                                " Each day of the week must be unique"
                );
            }
        }

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
        DayOfWeek newDayOfWeek = request.getDayOfWeek();

        boolean dayExists = program.getDays().stream()
                .anyMatch(dietDay -> dietDay.getDayOfWeek() == newDayOfWeek);

        if (dayExists) {
            throw new IllegalStateException("Day " + newDayOfWeek + " already exists in this program");
        }

        DietDay day = dietDayMapper.toDietDay(request);
        day.setCreatedAt(LocalDateTime.now());
        day.setProgram(program);

        program.getDays().add(day);
        program.setUpdatedAt(LocalDateTime.now());

        program.getDays().sort(Comparator.comparing(DietDay::getDayOfWeek));

        dietDayRepository.save(day);
        mealRepository.saveAll(day.getMeals());
    }

    public DietDayResponse getDietDay(Long clientId, Long programId, Long dayId, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findProgramIdByDietDayId(dayId).equals(programId)) {
            throw new IllegalStateException("This day doesn't belong to this program");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }

        if (!entityUtils.findClientIdByDietDayId(dayId).equals(clientId)) {
            throw new IllegalStateException("This day doesn't belong to this client");
        }

        DietDay day = entityUtils.getDietDay(dayId);
        return dietDayMapper.toDietDayResponse(day);
    }

    public void editDietDay(Long clientId, Long programId, Long dayId, DietDayRequest request, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findProgramIdByDietDayId(dayId).equals(programId)) {
            throw new IllegalStateException("This day doesn't belong to this program");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }

        if (!entityUtils.findClientIdByDietDayId(dayId).equals(clientId)) {
            throw new IllegalStateException("This day doesn't belong to this client");
        }

        DietDay oldDay = entityUtils.getDietDay(dayId);
        DietDay newDay = dietDayMapper.toDietDay(request);
        newDay.setId(oldDay.getId());
        newDay.setProgram(oldDay.getProgram());
        newDay.setDayOfWeek(oldDay.getDayOfWeek());
        newDay.setUpdatedAt(LocalDateTime.now());

        newDay.getMeals().forEach(meal -> {
            meal.setDay(newDay);
            meal.setCreatedAt(LocalDateTime.now());
            meal.setUpdatedAt(LocalDateTime.now());
        })
        ;
        dietDayRepository.save(newDay);

        DietProgram program = entityUtils.getDietProgram(programId);
        program.setUpdatedAt(LocalDateTime.now());
    }

    public void deleteDietDay(Long clientId, Long programId, Long dayId, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findProgramIdByDietDayId(dayId).equals(programId)) {
            throw new IllegalStateException("This day doesn't belong to this program");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }

        if (!entityUtils.findClientIdByDietDayId(dayId).equals(clientId)) {
            throw new IllegalStateException("This day doesn't belong to this client");
        }

        DietProgram program = entityUtils.getDietProgram(programId);
        dietDayRepository.deleteById(dayId);
        program.setUpdatedAt(LocalDateTime.now());

    }

    public void addMeal(Long clientId, Long programId, Long dayId, MealRequest request, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findProgramIdByDietDayId(dayId).equals(programId)) {
            throw new IllegalStateException("This day doesn't belong to this program");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }

        if (!entityUtils.findClientIdByDietDayId(dayId).equals(clientId)) {
            throw new IllegalStateException("This day doesn't belong to this client");
        }

        Meal meal = mealMapper.toMeal(request);
        meal.setCreatedAt(LocalDateTime.now());

        DietDay dietDay = entityUtils.getDietDay(dayId);
        dietDay.getMeals().add(meal);
        dietDay.getMeals()
                .sort(Comparator.comparing(Meal::getTimeToEat));
        dietDay.setUpdatedAt(LocalDateTime.now());

        mealRepository.save(meal);

    }

    public MealResponse getMeal(Long clientId, Long programId, Long dayId, Long mealId, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findProgramIdByDietDayId(dayId).equals(programId)) {
            throw new IllegalStateException("This day doesn't belong to this program");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }

        if (!entityUtils.findClientIdByDietDayId(dayId).equals(clientId)) {
            throw new IllegalStateException("This day doesn't belong to this client");
        }

        if (!entityUtils.findProgramIdByMealId(mealId).equals(programId)) {
            throw new IllegalStateException("This meal doesn't belong to this program");
        }

        if (!entityUtils.findClientIdByMealId(mealId).equals(clientId)) {
            throw new IllegalStateException("This meal doesn't belong to this client");
        }

        Meal meal = entityUtils.getMeal(mealId);
        return mealMapper.toMealResponse(meal);

    }


    public void editMeal(Long clientId, Long programId, Long dayId, Long mealId, MealRequest request, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findProgramIdByDietDayId(dayId).equals(programId)) {
            throw new IllegalStateException("This day doesn't belong to this program");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }

        if (!entityUtils.findClientIdByDietDayId(dayId).equals(clientId)) {
            throw new IllegalStateException("This day doesn't belong to this client");
        }

        if (!entityUtils.findProgramIdByMealId(mealId).equals(programId)) {
            throw new IllegalStateException("This meal doesn't belong to this program");
        }

        if (!entityUtils.findClientIdByMealId(mealId).equals(clientId)) {
            throw new IllegalStateException("This meal doesn't belong to this client");
        }

        Meal oldMeal = entityUtils.getMeal(mealId);
        Meal newMeal = mealMapper.toMeal(request);

        newMeal.setId(oldMeal.getId());
        newMeal.setDay(oldMeal.getDay());
        newMeal.setCreatedAt(oldMeal.getCreatedAt());
        newMeal.setUpdatedAt(LocalDateTime.now());

        DietDay dietDay = entityUtils.getDietDay(dayId);
        dietDay.setUpdatedAt(LocalDateTime.now());
        mealRepository.save(newMeal);
    }

    public void deleteMeal(Long clientId, Long programId, Long dayId, Long mealId, Authentication authentication) {

        Coach coach = SecurityUtils.getAuthenticatedAndVerifiedCoach(authentication);

        if (!entityUtils.findCoachIdByClientId(clientId).equals(coach.getId())) {
            throw new AccessDeniedException("This client isn't yours");
        }

        if (!entityUtils.findProgramIdByDietDayId(dayId).equals(programId)) {
            throw new IllegalStateException("This day doesn't belong to this program");
        }

        if (!entityUtils.findClientIdByDietProgramId(programId).equals(clientId)) {
            throw new IllegalStateException("This program doesn't belong to this client");
        }

        if (!entityUtils.findClientIdByDietDayId(dayId).equals(clientId)) {
            throw new IllegalStateException("This day doesn't belong to this client");
        }

        if (!entityUtils.findProgramIdByMealId(mealId).equals(programId)) {
            throw new IllegalStateException("This meal doesn't belong to this program");
        }

        if (!entityUtils.findClientIdByMealId(mealId).equals(clientId)) {
            throw new IllegalStateException("This meal doesn't belong to this client");
        }

        mealRepository.deleteById(mealId);
        DietDay dietDay = entityUtils.getDietDay(dayId);
        dietDay.setUpdatedAt(LocalDateTime.now());
    }
}

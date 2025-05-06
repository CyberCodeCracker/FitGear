package com.amouri_coding.FitGear.diet.management;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.common.EntityUtils;
import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.diet.diet_day.*;
import com.amouri_coding.FitGear.diet.diet_program.*;
import com.amouri_coding.FitGear.diet.meal.Meal;
import com.amouri_coding.FitGear.diet.meal.MealMapper;
import com.amouri_coding.FitGear.diet.meal.MealRepository;
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
import java.util.*;
import java.util.stream.Collectors;

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
}

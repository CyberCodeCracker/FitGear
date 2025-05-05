package com.amouri_coding.FitGear.diet.diet_program;

import com.amouri_coding.FitGear.diet.diet_day.DietDay;
import com.amouri_coding.FitGear.diet.diet_day.DietDayMapper;
import com.amouri_coding.FitGear.diet.diet_day.DietDayResponse;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.coach.Coach;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietProgramMapper {

    private final DietDayMapper dietDayMapper;
    private final EntityManager entityManager;

    public DietProgram toDietProgram(DietProgramRequest request, Long clientId, Long coachId) {

        Coach coach = entityManager.getReference(Coach.class, coachId);
        Client client = entityManager.getReference(Client.class, clientId);

        DietProgram dietProgram = DietProgram.builder()
                .client(client)
                .coach(coach)
                .title(request.getTitle())
                .description(request.getDescription())
                .build()
                ;

        List<DietDay> mappedDietDays = request.getDays()
                .stream()
                .map(day -> dietDayMapper.toDietDay(day))
                .peek(dietDay -> dietDay.setProgram(dietProgram))
                .collect(Collectors.toCollection(ArrayList::new))
                ;

        dietProgram.setDays(mappedDietDays);
        return dietProgram;
    }

    public DietProgramResponse toDietProgramResponse(DietProgram program) {

        List<DietDayResponse> dayResponses = program.getDays()
                .stream()
                .map(dietDay -> dietDayMapper.toDietDayResponse(dietDay))
                .collect(Collectors.toCollection(ArrayList::new))
                ;

        return DietProgramResponse.builder()
                .title(program.getTitle())
                .description(program.getDescription())
                .days(dayResponses)
                .build()
                ;
    }
}

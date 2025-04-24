package com.amouri_coding.FitGear.training.management;

import com.amouri_coding.FitGear.training.training_program.TrainingProgramRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {



    public void assignProgram(@Valid TrainingProgramRequest request, Authentication authentication, HttpServletResponse response) {
    }
}

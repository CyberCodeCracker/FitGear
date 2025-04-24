package com.amouri_coding.FitGear.training.management;

import com.amouri_coding.FitGear.training.training_program.TrainingProgramRequest;
import com.amouri_coding.FitGear.user.client.ClientResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("training")
@Tag(name = "Training")
public class TrainingController {

    private final TrainingService service;

    @PreAuthorize(value = "hasRole('ROLE_COACH')")
    @PostMapping("/assign-program")
    @ResponseStatus(HttpStatus.CREATED)
    public void assignProgram(
            @RequestParam Long clientId,
            @RequestBody @Valid TrainingProgramRequest request,
            Authentication authentication,
            HttpServletResponse response
            ) {
        service.assignProgram(clientId, request, authentication, response);
    }

}

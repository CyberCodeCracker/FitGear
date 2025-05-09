package com.amouri_coding.FitGear.user.coach;

import com.amouri_coding.FitGear.common.PageResponse;
import com.amouri_coding.FitGear.user.client.ClientResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("coach")
@RequiredArgsConstructor
@Tag(name = "Coach")
public class CoachController {

    private final CoachService coachService;
    private final CoachRepository coachRepository;

    @PreAuthorize(value = "hasRole('ROLE_COACH')")
    @GetMapping("/show-clients")
    public ResponseEntity<PageResponse<ClientResponse>> showClients(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(coachService.showAllClients(page, size, authentication));
    }

    @PreAuthorize(value = "hasRole('ROLE_COACH')")
    @GetMapping("/show-client-name/{client-name}")
    public ResponseEntity<PageResponse<ClientResponse>> showClientsByName(
            @PathVariable("client-name") String clientName,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(coachService.showClientsByName(clientName, page, size, authentication));
    }

    @PreAuthorize(value = "hasRole('ROLE_COACH')")
    @GetMapping("/show-client-id/{client-id}")
    public ResponseEntity<ClientResponse> getClientById(
            @PathVariable(value = "client-id") Long clientId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(coachService.getClientByID(clientId, authentication));
    }

}

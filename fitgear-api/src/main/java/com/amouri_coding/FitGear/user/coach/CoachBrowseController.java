package com.amouri_coding.FitGear.user.coach;

import com.amouri_coding.FitGear.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coaches")
@RequiredArgsConstructor
@Tag(name = "Coach Browse")
public class CoachBrowseController {

    private final CoachBrowseService service;

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @GetMapping
    public ResponseEntity<PageResponse<CoachCardResponse>> listCoaches(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "12", required = false) int size,
            @RequestParam(name = "q", required = false) String q
    ) {
        return ResponseEntity.ok(service.listCoaches(page, size, q));
    }

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @GetMapping("/{coachId}")
    public ResponseEntity<CoachDetailResponse> getCoach(
            @PathVariable Long coachId
    ) {
        return ResponseEntity.ok(service.getCoach(coachId));
    }
}


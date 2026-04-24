package com.amouri_coding.FitGear.progress;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/me/progress")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_CLIENT')")
@Tag(name = "Client Progress")
public class ProgressEntryController {

    private final ProgressEntryService progressService;

    @GetMapping
    public ResponseEntity<List<ProgressEntryResponse>> getAll(Authentication authentication) {
        return ResponseEntity.ok(progressService.getAllEntries(authentication));
    }

    @PostMapping
    public ResponseEntity<ProgressEntryResponse> create(
            @RequestBody @Valid ProgressEntryRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progressService.createEntry(request, authentication));
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long entryId,
            Authentication authentication
    ) {
        progressService.deleteEntry(entryId, authentication);
        return ResponseEntity.noContent().build();
    }
}

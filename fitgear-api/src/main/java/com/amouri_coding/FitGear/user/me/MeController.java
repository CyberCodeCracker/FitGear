package com.amouri_coding.FitGear.user.me;

import com.amouri_coding.FitGear.file.FileStorageService;
import com.amouri_coding.FitGear.user.User;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.coach.Coach;
import com.amouri_coding.FitGear.user.coach.CoachRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
@Tag(name = "Me")
public class MeController {

    private final MeService meService;
    private final FileStorageService fileStorageService;
    private final CoachRepository coachRepository;

    @GetMapping
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        if (authentication == null)
            throw new AccessDeniedException("Authentication required");

        User user = (User) authentication.getPrincipal();
        List<String> roles = user.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();

        CoachSummaryResponse coachSummary = null;
        String userType = "UNKNOWN";

        if (user instanceof Client client) {
            userType = "CLIENT";
            Coach coach = client.getCoach();
            if (coach != null) {
                coachSummary = CoachSummaryResponse.builder()
                        .id(coach.getId())
                        .fullName(coach.getName())
                        .monthlyRate(coach.getMonthlyRate())
                        .rating(coach.getRating())
                        .profilePicture(coach.getProfilePicture())
                        .build();
            }
            return ResponseEntity.ok(MeResponse.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .roles(roles)
                    .userType(userType)
                    .coach(coachSummary)
                    .height(client.getHeight())
                    .weight(client.getWeight())
                    .bodyFatPercentage(client.getBodyFatPercentage())
                    .build());
        } else if (user instanceof Coach coach) {
            userType = "COACH";
            return ResponseEntity.ok(MeResponse.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .roles(roles)
                    .userType(userType)
                    .rating(coach.getRating())
                    .monthlyRate(coach.getMonthlyRate())
                    .description(coach.getDescription())
                    .phoneNumber(coach.getPhoneNumber())
                    .yearsOfExperience(coach.getYearsOfExperience())
                    .isAvailable(coach.isAvailable())
                    .profilePicture(coach.getProfilePicture())
                    .build());
        }

        return ResponseEntity.ok(MeResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(roles)
                .userType(userType)
                .build());
    }

    @PutMapping
    public ResponseEntity<MeResponse> updateProfile(
            @RequestBody @Valid UpdateProfileRequest request,
            Authentication authentication
    ) {
        if (authentication == null)
            throw new AccessDeniedException("Authentication required");
        return ResponseEntity.ok(meService.updateProfile(request, authentication));
    }

    @PostMapping(value = "/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        if (authentication == null)
            throw new AccessDeniedException("Authentication required");

        User user = (User) authentication.getPrincipal();
        if (!(user instanceof Coach coach)) {
            throw new IllegalStateException("Only coaches can upload profile pictures");
        }

        // Delete old picture if exists
        if (coach.getProfilePicture() != null && !coach.getProfilePicture().isBlank()) {
            fileStorageService.delete(coach.getProfilePicture());
        }

        String relativePath = fileStorageService.store(file, "profile-pictures");
        coach.setProfilePicture(relativePath);
        coachRepository.save(coach);

        return ResponseEntity.ok(Map.of("profilePicture", relativePath));
    }

    @DeleteMapping("/profile-picture")
    public ResponseEntity<Void> deleteProfilePicture(Authentication authentication) {
        if (authentication == null)
            throw new AccessDeniedException("Authentication required");

        User user = (User) authentication.getPrincipal();
        if (!(user instanceof Coach coach)) {
            throw new IllegalStateException("Only coaches can manage profile pictures");
        }

        if (coach.getProfilePicture() != null) {
            fileStorageService.delete(coach.getProfilePicture());
            coach.setProfilePicture(null);
            coachRepository.save(coach);
        }

        return ResponseEntity.noContent().build();
    }
}

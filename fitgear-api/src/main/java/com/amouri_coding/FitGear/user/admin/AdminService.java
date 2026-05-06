package com.amouri_coding.FitGear.user.admin;

import com.amouri_coding.FitGear.diet.diet_program.DietProgramRepository;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramRepository;
import com.amouri_coding.FitGear.user.User;
import com.amouri_coding.FitGear.user.UserRepository;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import com.amouri_coding.FitGear.user.coach.CoachRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository           userRepository;
    private final ClientRepository         clientRepository;
    private final CoachRepository          coachRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final DietProgramRepository    dietProgramRepository;

    // ── Dashboard ────────────────────────────────────────────────────────────

    public AdminDashboardResponse getDashboard() {
        return AdminDashboardResponse.builder()
                .totalClients(clientRepository.count())
                .totalCoaches(coachRepository.count())
                .clientsWithCoach(clientRepository.countWithCoach())
                .clientsWithTrainingProgram(clientRepository.countWithTrainingProgram())
                .clientsWithDietProgram(clientRepository.countWithDietProgram())
                .totalTrainingPrograms(trainingProgramRepository.count())
                .totalDietPrograms(dietProgramRepository.count())
                .build();
    }

    // ── Users ────────────────────────────────────────────────────────────────

    public Page<AdminUserResponse> getAllUsers(int page, int size, String search, String role) {
        var pageable = PageRequest.of(page, size);
        return userRepository
                .searchUsers(search, role, pageable)
                .map(this::toAdminUserResponse);
    }

    public AdminUserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return toAdminUserResponse(user);
    }

    @Transactional
    public AdminUserResponse toggleLock(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setAccountLocked(!user.isAccountLocked());
        return toAdminUserResponse(userRepository.save(user));
    }

    @Transactional
    public AdminUserResponse toggleEnable(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setAccountEnabled(!user.isAccountEnabled());
        return toAdminUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }


    private AdminUserResponse toAdminUserResponse(User user) {
        String userType;
        if (user instanceof Admin)   userType = "ADMIN";
        else if (user instanceof Coach)  userType = "COACH";
        else if (user instanceof Client) userType = "CLIENT";
        else userType = "UNKNOWN";
        return AdminUserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userType(userType)
                .roles(user.getRoles().stream().map(r -> r.getName()).toList())
                .accountEnabled(user.isAccountEnabled())
                .accountLocked(user.isAccountLocked())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

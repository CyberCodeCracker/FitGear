package com.amouri_coding.FitGear.config;

import com.amouri_coding.FitGear.role.UserRole;
import com.amouri_coding.FitGear.role.UserRoleRepository;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import com.amouri_coding.FitGear.user.coach.CoachRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRoleRepository roleRepository;
    private final CoachRepository    coachRepository;
    private final ClientRepository   clientRepository;
    private final PasswordEncoder    passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedRoles();
        seedCoaches();
        seedClients();
    }

    // ── Roles ────────────────────────────────────────────────────────────────
    private void seedRoles() {
        if (roleRepository.findByName("COACH").isEmpty()) {
            roleRepository.save(UserRole.builder().name("COACH").build());
            log.info("DataSeeder: seeded role COACH");
        }
        if (roleRepository.findByName("CLIENT").isEmpty()) {
            roleRepository.save(UserRole.builder().name("CLIENT").build());
            log.info("DataSeeder: seeded role CLIENT");
        }
    }

    // ── Coaches ──────────────────────────────────────────────────────────────
    private void seedCoaches() {
        UserRole coachRole = roleRepository.findByName("COACH")
                .orElseThrow(() -> new IllegalStateException("COACH role not found"));

        record CoachSeed(String first, String last, String email,
                         String desc, int exp, double rate, String phone) {}

        List<CoachSeed> seeds = List.of(
            new CoachSeed("Alex",   "Morgan", "coach@fitgear.com",
                "Strength & conditioning specialist with 10+ years helping athletes.",
                10, 149.0, "12345678"),
            new CoachSeed("Sara",   "Lee",    "sara@fitgear.com",
                "Holistic fitness coach combining yoga, HIIT and nutrition guidance.",
                7,  99.0,  "22345678"),
            new CoachSeed("Marcus", "Hill",   "marcus@fitgear.com",
                "Former pro athlete. Specialises in hypertrophy and sports performance.",
                12, 199.0, "32345678")
        );

        List<CoachSeed> seeds2 = List.of(
            new CoachSeed("Priya",  "Patel",  "priya@fitgear.com",
                "Nutrition-first approach. Meal planning & flexible dieting expert.",
                5,  79.0,  "42345678"),
            new CoachSeed("Chris",  "Walker", "chris@fitgear.com",
                "Powerlifting coach. Squat, bench, deadlift & OHP programming.",
                8,  129.0, "52345678"),
            new CoachSeed("Nina",   "Torres", "nina@fitgear.com",
                "Weight loss & body recomposition. Evidence-based coaching.",
                6,  89.0,  "62345678")
        );

        List<CoachSeed> allCoaches = new java.util.ArrayList<>(seeds);
        allCoaches.addAll(seeds2);

        for (CoachSeed s : allCoaches) {
            if (coachRepository.findByEmail(s.email()).isEmpty()) {
                Coach coach = Coach.builder()
                        .firstName(s.first())
                        .lastName(s.last())
                        .email(s.email())
                        .password(passwordEncoder.encode("Password1!"))
                        .accountEnabled(true)
                        .accountLocked(false)
                        .createdAt(LocalDateTime.now())
                        .roles(List.of(coachRole))
                        .description(s.desc())
                        .yearsOfExperience(s.exp())
                        .monthlyRate(s.rate())
                        .phoneNumber(s.phone())
                        .isAvailable(true)
                        .isVerified(true)
                        .rating(4.8)
                        .build();
                coachRepository.save(coach);
                log.info("DataSeeder: seeded coach {} {}", s.first(), s.last());
            }
        }
    }

    // ── Clients ──────────────────────────────────────────────────────────────
    private void seedClients() {
        UserRole clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new IllegalStateException("CLIENT role not found"));

        // Assign the primary demo coach to all seeded clients as realistic demo data.
        // coach@fitgear.com is always seeded first, so it will always be present here.
        Coach defaultCoach = coachRepository.findByEmail("coach@fitgear.com")
                .orElse(null);

        record ClientSeed(String first, String last, String email,
                          double height, double weight, double bodyFat) {}

        List<ClientSeed> seeds = List.of(
            new ClientSeed("Jordan", "Smith",   "client@fitgear.com", 178, 85.5, 18.2),
            new ClientSeed("Taylor", "Brown",   "taylor@fitgear.com", 165, 62.0, 22.5),
            new ClientSeed("Morgan", "Davis",   "morgan@fitgear.com", 182, 91.0, 20.1),
            new ClientSeed("Casey",  "Wilson",  "casey@fitgear.com",  170, 70.3, 15.8),
            new ClientSeed("Riley",  "Johnson", "riley@fitgear.com",  175, 78.2, 17.4)
        );

        for (ClientSeed s : seeds) {
            if (clientRepository.findByEmail(s.email()).isEmpty()) {
                Client client = Client.builder()
                        .firstName(s.first())
                        .lastName(s.last())
                        .email(s.email())
                        .password(passwordEncoder.encode("Password1!"))
                        .accountEnabled(true)
                        .accountLocked(false)
                        .createdAt(LocalDateTime.now())
                        .roles(List.of(clientRole))
                        .height(s.height())
                        .weight(s.weight())
                        .bodyFatPercentage(s.bodyFat())
                        .coach(defaultCoach)
                        .build();
                clientRepository.save(client);
                log.info("DataSeeder: seeded client {} {} (coach: {})",
                        s.first(), s.last(),
                        defaultCoach != null ? defaultCoach.getEmail() : "none");
            }
        }
    }
}

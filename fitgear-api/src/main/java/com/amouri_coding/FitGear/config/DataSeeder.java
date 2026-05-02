package com.amouri_coding.FitGear.config;

import com.amouri_coding.FitGear.role.UserRole;
import com.amouri_coding.FitGear.role.UserRoleRepository;
import com.amouri_coding.FitGear.training.catalog.ExerciseCatalog;
import com.amouri_coding.FitGear.training.catalog.ExerciseCatalogRepository;
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
    private final ExerciseCatalogRepository exerciseCatalogRepository;

    @Override
    public void run(ApplicationArguments args) {
        seedRoles();
        seedCoaches();
        seedClients();
        seedExerciseCatalog();
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

    // ── Exercise Catalog ─────────────────────────────────────────────────────
    private void seedExerciseCatalog() {
        if (exerciseCatalogRepository.count() > 0) {
            log.info("DataSeeder: exercise catalog already seeded ({} entries)", exerciseCatalogRepository.count());
            return;
        }

        record Ex(String name, String group) {}

        List<Ex> exercises = List.of(
            // ── CHEST ──
            new Ex("Barbell Bench Press", "Chest"),
            new Ex("Incline Barbell Bench Press", "Chest"),
            new Ex("Decline Barbell Bench Press", "Chest"),
            new Ex("Dumbbell Bench Press", "Chest"),
            new Ex("Incline Dumbbell Bench Press", "Chest"),
            new Ex("Decline Dumbbell Bench Press", "Chest"),
            new Ex("Dumbbell Fly", "Chest"),
            new Ex("Incline Dumbbell Fly", "Chest"),
            new Ex("Cable Fly", "Chest"),
            new Ex("Low Cable Fly", "Chest"),
            new Ex("High Cable Fly", "Chest"),
            new Ex("Machine Chest Press", "Chest"),
            new Ex("Pec Deck Machine", "Chest"),
            new Ex("Push-Up", "Chest"),
            new Ex("Weighted Push-Up", "Chest"),
            new Ex("Dip (Chest Focus)", "Chest"),
            new Ex("Landmine Press", "Chest"),
            new Ex("Svend Press", "Chest"),
            new Ex("Close-Grip Bench Press", "Chest"),
            new Ex("Floor Press", "Chest"),

            // ── BACK ──
            new Ex("Barbell Bent-Over Row", "Back"),
            new Ex("Dumbbell Bent-Over Row", "Back"),
            new Ex("Pendlay Row", "Back"),
            new Ex("T-Bar Row", "Back"),
            new Ex("Seated Cable Row", "Back"),
            new Ex("Single-Arm Dumbbell Row", "Back"),
            new Ex("Lat Pulldown", "Back"),
            new Ex("Wide-Grip Lat Pulldown", "Back"),
            new Ex("Close-Grip Lat Pulldown", "Back"),
            new Ex("Reverse-Grip Lat Pulldown", "Back"),
            new Ex("Pull-Up", "Back"),
            new Ex("Chin-Up", "Back"),
            new Ex("Weighted Pull-Up", "Back"),
            new Ex("Neutral-Grip Pull-Up", "Back"),
            new Ex("Chest-Supported Row", "Back"),
            new Ex("Meadows Row", "Back"),
            new Ex("Machine Row", "Back"),
            new Ex("Cable Pullover", "Back"),
            new Ex("Dumbbell Pullover", "Back"),
            new Ex("Straight-Arm Pulldown", "Back"),
            new Ex("Inverted Row", "Back"),
            new Ex("Barbell Shrug", "Back"),
            new Ex("Dumbbell Shrug", "Back"),
            new Ex("Rack Pull", "Back"),
            new Ex("Seal Row", "Back"),
            new Ex("Helms Row", "Back"),

            // ── SHOULDERS ──
            new Ex("Overhead Press (Barbell)", "Shoulders"),
            new Ex("Seated Dumbbell Shoulder Press", "Shoulders"),
            new Ex("Arnold Press", "Shoulders"),
            new Ex("Dumbbell Lateral Raise", "Shoulders"),
            new Ex("Cable Lateral Raise", "Shoulders"),
            new Ex("Machine Lateral Raise", "Shoulders"),
            new Ex("Dumbbell Front Raise", "Shoulders"),
            new Ex("Cable Front Raise", "Shoulders"),
            new Ex("Barbell Upright Row", "Shoulders"),
            new Ex("Dumbbell Upright Row", "Shoulders"),
            new Ex("Face Pull", "Shoulders"),
            new Ex("Reverse Pec Deck", "Shoulders"),
            new Ex("Bent-Over Reverse Fly", "Shoulders"),
            new Ex("Cable Reverse Fly", "Shoulders"),
            new Ex("Rear Delt Row", "Shoulders"),
            new Ex("Machine Shoulder Press", "Shoulders"),
            new Ex("Behind-the-Neck Press", "Shoulders"),
            new Ex("Lu Raise", "Shoulders"),
            new Ex("Plate Front Raise", "Shoulders"),
            new Ex("Push Press", "Shoulders"),
            new Ex("Bradford Press", "Shoulders"),
            new Ex("Bus Driver Raise", "Shoulders"),

            // ── BICEPS ──
            new Ex("Barbell Curl", "Biceps"),
            new Ex("EZ-Bar Curl", "Biceps"),
            new Ex("Dumbbell Curl", "Biceps"),
            new Ex("Alternating Dumbbell Curl", "Biceps"),
            new Ex("Hammer Curl", "Biceps"),
            new Ex("Incline Dumbbell Curl", "Biceps"),
            new Ex("Preacher Curl", "Biceps"),
            new Ex("Machine Preacher Curl", "Biceps"),
            new Ex("Concentration Curl", "Biceps"),
            new Ex("Cable Curl", "Biceps"),
            new Ex("Rope Hammer Curl", "Biceps"),
            new Ex("Spider Curl", "Biceps"),
            new Ex("Drag Curl", "Biceps"),
            new Ex("Reverse Curl", "Biceps"),
            new Ex("Cross-Body Hammer Curl", "Biceps"),
            new Ex("Zottman Curl", "Biceps"),
            new Ex("21s (Bicep Curl)", "Biceps"),
            new Ex("Bayesian Cable Curl", "Biceps"),

            // ── TRICEPS ──
            new Ex("Tricep Pushdown (Rope)", "Triceps"),
            new Ex("Tricep Pushdown (V-Bar)", "Triceps"),
            new Ex("Tricep Pushdown (Straight Bar)", "Triceps"),
            new Ex("Overhead Tricep Extension (Cable)", "Triceps"),
            new Ex("Overhead Tricep Extension (Dumbbell)", "Triceps"),
            new Ex("Skull Crusher (EZ-Bar)", "Triceps"),
            new Ex("Skull Crusher (Dumbbell)", "Triceps"),
            new Ex("Dumbbell Kickback", "Triceps"),
            new Ex("Dip (Tricep Focus)", "Triceps"),
            new Ex("Bench Dip", "Triceps"),
            new Ex("Close-Grip Push-Up", "Triceps"),
            new Ex("Diamond Push-Up", "Triceps"),
            new Ex("JM Press", "Triceps"),
            new Ex("Tate Press", "Triceps"),
            new Ex("Single-Arm Cable Pushdown", "Triceps"),

            // ── QUADRICEPS ──
            new Ex("Barbell Back Squat", "Quadriceps"),
            new Ex("Barbell Front Squat", "Quadriceps"),
            new Ex("Goblet Squat", "Quadriceps"),
            new Ex("Hack Squat (Machine)", "Quadriceps"),
            new Ex("Leg Press", "Quadriceps"),
            new Ex("Leg Extension", "Quadriceps"),
            new Ex("Bulgarian Split Squat", "Quadriceps"),
            new Ex("Dumbbell Lunge", "Quadriceps"),
            new Ex("Barbell Lunge", "Quadriceps"),
            new Ex("Walking Lunge", "Quadriceps"),
            new Ex("Reverse Lunge", "Quadriceps"),
            new Ex("Step-Up", "Quadriceps"),
            new Ex("Sissy Squat", "Quadriceps"),
            new Ex("Smith Machine Squat", "Quadriceps"),
            new Ex("Pendulum Squat", "Quadriceps"),
            new Ex("Belt Squat", "Quadriceps"),
            new Ex("Cyclist Squat", "Quadriceps"),
            new Ex("Pistol Squat", "Quadriceps"),
            new Ex("Spanish Squat", "Quadriceps"),

            // ── HAMSTRINGS ──
            new Ex("Romanian Deadlift (Barbell)", "Hamstrings"),
            new Ex("Romanian Deadlift (Dumbbell)", "Hamstrings"),
            new Ex("Stiff-Leg Deadlift", "Hamstrings"),
            new Ex("Lying Leg Curl", "Hamstrings"),
            new Ex("Seated Leg Curl", "Hamstrings"),
            new Ex("Standing Leg Curl", "Hamstrings"),
            new Ex("Nordic Hamstring Curl", "Hamstrings"),
            new Ex("Good Morning", "Hamstrings"),
            new Ex("Glute-Ham Raise", "Hamstrings"),
            new Ex("Single-Leg Romanian Deadlift", "Hamstrings"),
            new Ex("Cable Pull-Through", "Hamstrings"),
            new Ex("Kettlebell Swing", "Hamstrings"),
            new Ex("Sumo Deadlift", "Hamstrings"),

            // ── GLUTES ──
            new Ex("Barbell Hip Thrust", "Glutes"),
            new Ex("Dumbbell Hip Thrust", "Glutes"),
            new Ex("Machine Hip Thrust", "Glutes"),
            new Ex("Glute Bridge", "Glutes"),
            new Ex("Single-Leg Glute Bridge", "Glutes"),
            new Ex("Cable Kickback", "Glutes"),
            new Ex("Donkey Kick (Machine)", "Glutes"),
            new Ex("Hip Abduction Machine", "Glutes"),
            new Ex("Banded Clamshell", "Glutes"),
            new Ex("Sumo Squat", "Glutes"),
            new Ex("Curtsy Lunge", "Glutes"),
            new Ex("Frog Pump", "Glutes"),

            // ── CALVES ──
            new Ex("Standing Calf Raise (Machine)", "Calves"),
            new Ex("Seated Calf Raise", "Calves"),
            new Ex("Leg Press Calf Raise", "Calves"),
            new Ex("Smith Machine Calf Raise", "Calves"),
            new Ex("Donkey Calf Raise", "Calves"),
            new Ex("Single-Leg Calf Raise", "Calves"),
            new Ex("Tibialis Raise", "Calves"),

            // ── ABS / CORE ──
            new Ex("Crunch", "Abs"),
            new Ex("Cable Crunch", "Abs"),
            new Ex("Machine Crunch", "Abs"),
            new Ex("Hanging Leg Raise", "Abs"),
            new Ex("Hanging Knee Raise", "Abs"),
            new Ex("Captain's Chair Leg Raise", "Abs"),
            new Ex("Decline Sit-Up", "Abs"),
            new Ex("Weighted Decline Sit-Up", "Abs"),
            new Ex("Ab Wheel Rollout", "Abs"),
            new Ex("Plank", "Abs"),
            new Ex("Weighted Plank", "Abs"),
            new Ex("Side Plank", "Abs"),
            new Ex("Russian Twist", "Abs"),
            new Ex("Pallof Press", "Abs"),
            new Ex("Woodchop (Cable)", "Abs"),
            new Ex("Dead Bug", "Abs"),
            new Ex("Bicycle Crunch", "Abs"),
            new Ex("Dragon Flag", "Abs"),
            new Ex("L-Sit", "Abs"),
            new Ex("Toe Touch (Lying)", "Abs"),
            new Ex("Oblique Crunch", "Abs"),

            // ── FOREARMS ──
            new Ex("Wrist Curl (Barbell)", "Forearms"),
            new Ex("Reverse Wrist Curl", "Forearms"),
            new Ex("Farmer's Walk", "Forearms"),
            new Ex("Plate Pinch Hold", "Forearms"),
            new Ex("Dead Hang", "Forearms"),
            new Ex("Wrist Roller", "Forearms"),
            new Ex("Gripper Squeeze", "Forearms"),
            new Ex("Behind-the-Back Wrist Curl", "Forearms"),

            // ── COMPOUND / FULL BODY ──
            new Ex("Conventional Deadlift", "Full Body"),
            new Ex("Trap Bar Deadlift", "Full Body"),
            new Ex("Power Clean", "Full Body"),
            new Ex("Hang Clean", "Full Body"),
            new Ex("Clean and Press", "Full Body"),
            new Ex("Snatch (Barbell)", "Full Body"),
            new Ex("Thruster (Barbell)", "Full Body"),
            new Ex("Dumbbell Thruster", "Full Body"),
            new Ex("Man Maker", "Full Body"),
            new Ex("Turkish Get-Up", "Full Body"),
            new Ex("Burpee", "Full Body"),
            new Ex("Battle Ropes", "Full Body")
        );

        int count = 0;
        for (Ex ex : exercises) {
            if (!exerciseCatalogRepository.existsByName(ex.name())) {
                exerciseCatalogRepository.save(
                    ExerciseCatalog.builder()
                        .name(ex.name())
                        .muscleGroup(ex.group())
                        .build()
                );
                count++;
            }
        }
        log.info("DataSeeder: seeded {} exercises into exercise_catalog", count);
    }
}

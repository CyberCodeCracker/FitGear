// ─── Auth ──────────────────────────────────────────────────────────
export interface LoginRequest { email: string; password: string; }
export interface AuthResponse { token: string; refreshToken?: string; }

export interface MeResponse {
  id: number; firstName: string; lastName: string;
  email: string; roles: string[];
  userType: 'CLIENT' | 'COACH' | 'UNKNOWN';
  coach?: CoachSummary | null;
  // Client-only
  height?: number; weight?: number; bodyFatPercentage?: number;
  // Coach-only
  rating?: number; monthlyRate?: number;
  description?: string; phoneNumber?: string;
  yearsOfExperience?: number; isAvailable?: boolean;
  profilePicture?: string;
}

export interface CoachSummary {
  id: number; fullName: string; monthlyRate: number;
  rating: number; profilePicture?: string;
}

// ─── Registration ──────────────────────────────────────────────────
export interface CoachRegistrationRequest {
  firstName: string; lastName: string; email: string;
  password: string; passwordConfirm: string;
  phoneNumber: string; description: string;
  yearsOfExperience: number; monthlyRate: number;
}
export interface ClientRegistrationRequest {
  firstName: string; lastName: string; email: string;
  password: string; passwordConfirm: string;
  height: number; weight: number; bodyFatPercentage: number;
}

// ─── Coach Discovery ───────────────────────────────────────────────
export interface CoachCard {
  id: number; fullName: string; description: string;
  yearsOfExperience: number; monthlyRate: number; rating: number;
  reviewCount: number; profilePicture?: string;
}

// ─── Coach Detail ──────────────────────────────────────────────────
export interface CoachDetailResponse {
  id: number; fullName: string; description: string;
  yearsOfExperience: number; monthlyRate: number; rating: number;
  reviewCount: number; phoneNumber?: string; profilePicture?: string;
  available: boolean; testimonials: TestimonialResponse[];
}

// ─── Testimonials ──────────────────────────────────────────────────
export interface TestimonialResponse {
  id: number; clientName: string; clientInitials: string;
  rating: number; comment?: string; createdAt?: string;
}
export interface TestimonialRequest {
  rating: number; comment?: string;
}

// ─── Subscription ──────────────────────────────────────────────────
export interface ClientCoachResponse {
  id: number; fullName: string; description: string;
  monthlyRate: number; rating: number; profilePicture?: string;
}

// ─── Coach client list ─────────────────────────────────────────────
export interface ClientResponse {
  id: number; firstName: string; lastName: string;
  height: number; weight: number; bodyFatPercentage: number;
  trainingProgramId: number | null;
  dietProgramId: number | null;
}

// ─── Training — API response shapes ───────────────────────────────
export interface ExerciseResponse {
  id: number; title: string; exerciseUrl?: string;
  exerciseNumber: number; restTime: number;
  numberOfSets: number; numberOfReps: number;
}
export interface TrainingDayResponse {
  id: number; title: string; dayOfWeek: string;
  estimatedBurnedCalories: number;
  exercises: ExerciseResponse[];
}
export interface TrainingProgramResponse {
  id: number;
  trainingDays: TrainingDayResponse[];
}

// ─── Exercise Catalog (autocomplete) ───────────────────────────────
export interface ExerciseCatalogEntry {
  id: number; name: string; muscleGroup: string;
}

// ─── Training — request shapes ─────────────────────────────────────
export interface ExerciseRequest {
  title: string; exerciseUrl?: string;
  exerciseNumber: number; restTime: number;
  numberOfSets: number; numberOfReps: number;
}
export interface TrainingDayRequest {
  programId?: number; title: string;
  day: string;           // DayOfWeek enum value e.g. 'MONDAY'
  estimatedBurnedCalories: number;
  exercises: ExerciseRequest[];
}
export interface TrainingProgramRequest {
  trainingDays: TrainingDayRequest[];
}

// ─── Nutrition — API response shapes ──────────────────────────────
export interface MealResponse {
  id: number; description: string; calories: number;
  protein: number; carbs: number; fats: number;
  timeToEat?: string;
}
export interface DietDayResponse {
  id: number; dayOfWeek: string;
  totalCaloriesInDay: number; totalProteinInDay: number;
  totalCarbsInDay: number; totalFatsInDay: number;
  meals: MealResponse[];
}
export interface DietProgramResponse {
  id: number; title: string; description: string;
  days: DietDayResponse[];
}

// ─── Nutrition — request shapes ────────────────────────────────────
export interface MealRequest {
  description: string; calories: number;
  protein: number; fats: number; carbs: number;
  timeToEat?: string; // "HH:mm:ss"
}
export interface DietDayRequest {
  dayOfWeek: string;
  totalCaloriesInDay: number; totalProteinInDay: number;
  totalCarbsInDay: number; totalFatsInDay: number;
  meals: MealRequest[];
}
export interface DietProgramRequest {
  title: string; description: string;
  days: DietDayRequest[];
}

// ─── Profile update ────────────────────────────────────────────────
export interface UpdateProfileRequest {
  firstName: string; lastName: string;
  // Client-only
  height?: number; weight?: number; bodyFatPercentage?: number;
  // Coach-only
  description?: string; phoneNumber?: string;
  yearsOfExperience?: number; monthlyRate?: number;
  isAvailable?: boolean;
}

// ─── Progress ──────────────────────────────────────────────────────
export interface ProgressEntry {
  id: number;
  entryDate: string;       // "yyyy-MM-dd"
  weight: number;
  bodyFat: number;
  muscleMass?: number | null;
  notes?: string | null;
  createdAt?: string;
}

export interface ProgressEntryRequest {
  entryDate: string;
  weight: number;
  bodyFat: number;
  muscleMass?: number | null;
  notes?: string | null;
}

// ─── Pagination ────────────────────────────────────────────────────
export interface PageResponse<T> {
  content: T[]; number: number; size: number;
  totalElements: number; totalPages: number;
  first: boolean; last: boolean;
}

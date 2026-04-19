// ─── Auth ──────────────────────────────────────────────────────────
export interface LoginRequest { email: string; password: string; }

export interface AuthResponse { token: string; refreshToken?: string; }

export interface MeResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  roles: string[];
  userType: 'CLIENT' | 'COACH' | 'UNKNOWN';
  coach?: CoachSummary | null;
  // Client-only fields
  height?: number;
  weight?: number;
  bodyFatPercentage?: number;
  // Coach-only fields
  rating?: number;
  monthlyRate?: number;
}

export interface CoachSummary {
  id: number;
  fullName: string;
  monthlyRate: number;
  rating: number;
  profilePicture?: string;
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
  id: number;
  fullName: string;
  description: string;
  yearsOfExperience: number;
  monthlyRate: number;
  rating: number;
  profilePicture?: string;
}

// ─── Subscription ──────────────────────────────────────────────────
export interface ClientCoachResponse {
  id: number;
  fullName: string;
  description: string;
  monthlyRate: number;
  rating: number;
  profilePicture?: string;
}

// ─── Training (API response shapes) ───────────────────────────────
export interface ExerciseResponse {
  id: number;
  title: string;
  exerciseUrl?: string;
  exerciseNumber: number;
  restTime: number;        // seconds
  numberOfSets: number;
  numberOfReps: number;
}

export interface TrainingDayResponse {
  title: string;
  dayOfWeek: string;       // MONDAY … SUNDAY
  estimatedBurnedCalories: number;
  exercises: ExerciseResponse[];
}

export interface TrainingProgramResponse {
  trainingDays: TrainingDayResponse[];
}

// ─── Nutrition (API response shapes) ──────────────────────────────
export interface MealResponse {
  description: string;
  calories: number;
  protein: number;
  carbs: number;
  fats: number;
  timeToEat?: string;      // "HH:mm:ss"
}

export interface DietDayResponse {
  dayOfWeek: string;
  totalCaloriesInDay: number;
  totalProteinInDay: number;
  totalCarbsInDay: number;
  totalFatsInDay: number;
  meals: MealResponse[];
}

export interface DietProgramResponse {
  title: string;
  description: string;
  days: DietDayResponse[];
}

// ─── Progress (local) ──────────────────────────────────────────────
export interface ProgressEntry {
  id: number;
  date: string;
  weight: number;
  bodyFat: number;
  muscleMass?: number;
  notes?: string;
}

// ─── Pagination ────────────────────────────────────────────────────
export interface PageResponse<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

// ─── Coach-side client view ────────────────────────────────────────
export interface ClientResponse {
  id: number;
  firstName: string;
  lastName: string;
  height: number;
  weight: number;
  bodyFatPercentage: number;
}

// ─── Coach-side program editing (local form models) ────────────────
export interface Exercise {
  id?: number;
  name: string;
  sets: number;
  reps: number;
  weight?: number;
  notes?: string;
}

export interface TrainingDay {
  id?: number;
  dayOfWeek: string;
  focus: string;
  exercises: Exercise[];
}

export interface TrainingProgram {
  id?: number;
  trainingDays: TrainingDay[];
}

export interface Meal {
  id?: number;
  name: string;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  time?: string;
}

export interface DietDay {
  id?: number;
  dayLabel: string;
  totalCalories: number;
  meals: Meal[];
}

export interface DietProgram {
  id?: number;
  title: string;
  description: string;
  days: DietDay[];
}

// ─── Auth ──────────────────────────────────────────────────────────
export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  refreshToken?: string;
}

export interface MeResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  roles: string[];
  userType: 'CLIENT' | 'COACH' | 'UNKNOWN';
  coach?: CoachSummary | null;
}  

export interface CoachSummary {
  id: number;
  fullName: string;
  monthlyRate: number;
  rating: number;
  profilePicture?: string;
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

export interface CoachDetail extends CoachCard {
  specialties?: string[];
}

// ─── Client ────────────────────────────────────────────────────────
export interface ClientResponse {
  id: number;
  firstName: string;
  lastName: string;
  height: number;
  weight: number;
  bodyFatPercentage: number;
}

export interface ClientCoachResponse {
  coachId: number;
  coachName: string;
}

// ─── Progress ──────────────────────────────────────────────────────
export interface ProgressEntry {
  id: number;
  date: string;
  weight: number;
  bodyFat: number;
  muscleMass?: number;
  notes?: string;
}

// ─── Training ──────────────────────────────────────────────────────
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

// ─── Diet ──────────────────────────────────────────────────────────
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

// ─── Pagination ────────────────────────────────────────────────────
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

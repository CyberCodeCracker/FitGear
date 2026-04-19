import { Injectable } from '@angular/core';
import {
  CoachCard, ClientResponse, TrainingProgram, DietProgram,
  ProgressEntry, PageResponse
} from '../models/models';

@Injectable({ providedIn: 'root' })
export class MockDataService {

  readonly coaches: CoachCard[] = [
    { id: 1, fullName: 'Alex Morgan', description: 'Strength & conditioning specialist with 10+ years helping athletes.', yearsOfExperience: 10, monthlyRate: 149, rating: 4.9, profilePicture: '' },
    { id: 2, fullName: 'Sara Lee', description: 'Holistic fitness coach combining yoga, HIIT and nutrition guidance.', yearsOfExperience: 7, monthlyRate: 99, rating: 4.7, profilePicture: '' },
    { id: 3, fullName: 'Marcus Hill', description: 'Former pro athlete. Specialises in hypertrophy and sports performance.', yearsOfExperience: 12, monthlyRate: 199, rating: 4.8, profilePicture: '' },
    { id: 4, fullName: 'Priya Patel', description: 'Nutrition-first approach. Meal planning & flexible dieting expert.', yearsOfExperience: 5, monthlyRate: 79, rating: 4.6, profilePicture: '' },
    { id: 5, fullName: 'Chris Walker', description: 'Powerlifting coach. Squat, bench, deadlift & OHP programming.', yearsOfExperience: 8, monthlyRate: 129, rating: 4.8, profilePicture: '' },
    { id: 6, fullName: 'Nina Torres', description: 'Weight loss & body recomposition. Evidence-based coaching.', yearsOfExperience: 6, monthlyRate: 89, rating: 4.5, profilePicture: '' },
  ];

  readonly clients: ClientResponse[] = [
    { id: 10, firstName: 'Jordan', lastName: 'Smith',   height: 178, weight: 85.5, bodyFatPercentage: 18.2 },
    { id: 11, firstName: 'Taylor', lastName: 'Brown',   height: 165, weight: 62.0, bodyFatPercentage: 22.5 },
    { id: 12, firstName: 'Morgan', lastName: 'Davis',   height: 182, weight: 91.0, bodyFatPercentage: 20.1 },
    { id: 13, firstName: 'Casey',  lastName: 'Wilson',  height: 170, weight: 70.3, bodyFatPercentage: 15.8 },
    { id: 14, firstName: 'Riley',  lastName: 'Johnson', height: 175, weight: 78.2, bodyFatPercentage: 17.4 },
  ];

  readonly progressEntries: ProgressEntry[] = [
    { id: 1, date: '2024-01-01', weight: 92.0, bodyFat: 24.0, muscleMass: 68.5 },
    { id: 2, date: '2024-02-01', weight: 90.5, bodyFat: 23.0, muscleMass: 69.0 },
    { id: 3, date: '2024-03-01', weight: 89.0, bodyFat: 22.0, muscleMass: 69.5 },
    { id: 4, date: '2024-04-01', weight: 87.2, bodyFat: 20.5, muscleMass: 70.0 },
    { id: 5, date: '2024-05-01', weight: 85.5, bodyFat: 18.2, muscleMass: 70.8 },
    { id: 6, date: '2024-06-01', weight: 84.0, bodyFat: 17.0, muscleMass: 71.2 },
  ];

  readonly trainingProgram: TrainingProgram = {
    id: 1,
    trainingDays: [
      { id: 1, dayOfWeek: 'Monday',    focus: 'Push (Chest / Shoulders / Triceps)',
        exercises: [
          { name: 'Bench Press',       sets: 4, reps: 8,  weight: 80 },
          { name: 'Overhead Press',    sets: 3, reps: 10, weight: 50 },
          { name: 'Incline Dumbbell',  sets: 3, reps: 12, weight: 28 },
          { name: 'Lateral Raise',     sets: 4, reps: 15, weight: 14 },
          { name: 'Tricep Pushdown',   sets: 3, reps: 12, weight: 35 },
        ]
      },
      { id: 2, dayOfWeek: 'Wednesday', focus: 'Pull (Back / Biceps)',
        exercises: [
          { name: 'Deadlift',          sets: 4, reps: 5,  weight: 120 },
          { name: 'Pull-ups',          sets: 3, reps: 8 },
          { name: 'Barbell Row',       sets: 3, reps: 10, weight: 70 },
          { name: 'Face Pulls',        sets: 4, reps: 15, weight: 20 },
          { name: 'Bicep Curl',        sets: 3, reps: 12, weight: 16 },
        ]
      },
      { id: 3, dayOfWeek: 'Friday',    focus: 'Legs',
        exercises: [
          { name: 'Squat',             sets: 4, reps: 8,  weight: 100 },
          { name: 'Romanian DL',       sets: 3, reps: 10, weight: 80 },
          { name: 'Leg Press',         sets: 3, reps: 12, weight: 150 },
          { name: 'Calf Raise',        sets: 4, reps: 20, weight: 60 },
        ]
      },
    ]
  };

  readonly dietProgram: DietProgram = {
    id: 1,
    title: 'Lean Bulk – 2800 kcal',
    description: 'High-protein lean bulk targeting +200 kcal surplus for steady muscle gain.',
    days: [
      { id: 1, dayLabel: 'Weekday Template', totalCalories: 2800,
        meals: [
          { name: 'Breakfast',  calories: 550,  protein: 40, carbs: 65,  fat: 12, time: '07:00' },
          { name: 'Pre-Workout',calories: 350,  protein: 25, carbs: 45,  fat: 6,  time: '11:30' },
          { name: 'Post-Workout',calories: 600, protein: 50, carbs: 70,  fat: 8,  time: '14:00' },
          { name: 'Dinner',     calories: 900,  protein: 55, carbs: 90,  fat: 28, time: '19:00' },
          { name: 'Evening Snack',calories: 400,protein: 30, carbs: 30,  fat: 18, time: '21:30' },
        ]
      },
    ]
  };

  getCoachesPage(page: number, size: number): PageResponse<CoachCard> {
    const start = page * size;
    const content = this.coaches.slice(start, start + size);
    return { content, totalElements: this.coaches.length, totalPages: Math.ceil(this.coaches.length / size), size, number: page };
  }
}

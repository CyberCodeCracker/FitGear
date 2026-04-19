/**
 * MockDataService — data removed; kept as a stub so coach components
 * (client-detail, coach-clients) compile while they await API migration.
 */
import { Injectable } from '@angular/core';
import {
  CoachCard, ClientResponse, TrainingProgram, DietProgram,
  ProgressEntry, PageResponse
} from '../models/models';

@Injectable({ providedIn: 'root' })
export class MockDataService {
  readonly coaches:         CoachCard[]      = [];
  readonly clients:         ClientResponse[] = [];
  readonly progressEntries: ProgressEntry[]  = [];

  readonly trainingProgram: TrainingProgram = { trainingDays: [] };

  readonly dietProgram: DietProgram = {
    title: '', description: '', days: []
  };

  getCoachesPage(page: number, size: number): PageResponse<CoachCard> {
    return {
      content: [], totalElements: 0, totalPages: 0,
      size, number: page, first: true, last: true
    };
  }
}

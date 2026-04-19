import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {
  CoachCard, ClientCoachResponse,
  TrainingProgramResponse, DietProgramResponse, PageResponse
} from '../models/models';

const API = 'http://localhost:8080/api/v1';

@Injectable({ providedIn: 'root' })
export class ClientApiService {
  private http = inject(HttpClient);

  // ── Coaches ────────────────────────────────────────────────────────
  getCoaches(page = 0, size = 12, q = ''): Observable<PageResponse<CoachCard>> {
    const params: Record<string, string | number> = { page, size };
    if (q) params['q'] = q;
    return this.http.get<PageResponse<CoachCard>>(`${API}/coaches`, { params });
  }

  subscribeToCoach(coachId: number): Observable<ClientCoachResponse> {
    return this.http.post<ClientCoachResponse>(`${API}/clients/me/coach/${coachId}`, {});
  }

  unsubscribeCoach(): Observable<void> {
    return this.http.delete<void>(`${API}/clients/me/coach`);
  }

  getMyCoach(): Observable<ClientCoachResponse | null> {
    return this.http.get<ClientCoachResponse>(`${API}/clients/me/coach`).pipe(
      catchError(() => of(null))
    );
  }

  // ── Training ──────────────────────────────────────────────────────
  getMyTrainingProgram(): Observable<TrainingProgramResponse> {
    return this.http.get<TrainingProgramResponse>(`${API}/clients/me/training/program`);
  }

  // ── Diet ──────────────────────────────────────────────────────────
  getMyDietProgram(): Observable<DietProgramResponse | null> {
    return this.http.get<DietProgramResponse>(`${API}/clients/me/nutrition/program`).pipe(
      catchError(() => of(null))
    );
  }
}

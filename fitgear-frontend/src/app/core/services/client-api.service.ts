import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {
  CoachCard, ClientCoachResponse, CoachDetailResponse,
  TrainingProgramResponse, DietProgramResponse, PageResponse,
  ProgressEntry, ProgressEntryRequest,
  TestimonialResponse, TestimonialRequest
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

  getCoachDetail(coachId: number): Observable<CoachDetailResponse> {
    return this.http.get<CoachDetailResponse>(`${API}/coaches/${coachId}`);
  }

  // ── Testimonials ──────────────────────────────────────────────────
  getTestimonials(coachId: number): Observable<TestimonialResponse[]> {
    return this.http.get<TestimonialResponse[]>(`${API}/coaches/${coachId}/testimonials`);
  }

  submitTestimonial(coachId: number, request: TestimonialRequest): Observable<TestimonialResponse> {
    return this.http.post<TestimonialResponse>(`${API}/coaches/${coachId}/testimonials`, request);
  }

  deleteTestimonial(coachId: number, testimonialId: number): Observable<void> {
    return this.http.delete<void>(`${API}/coaches/${coachId}/testimonials/${testimonialId}`);
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

  // ── Progress ─────────────────────────────────────────────────────
  getProgressEntries(): Observable<ProgressEntry[]> {
    return this.http.get<ProgressEntry[]>(`${API}/clients/me/progress`);
  }

  createProgressEntry(request: ProgressEntryRequest): Observable<ProgressEntry> {
    return this.http.post<ProgressEntry>(`${API}/clients/me/progress`, request);
  }

  deleteProgressEntry(entryId: number): Observable<void> {
    return this.http.delete<void>(`${API}/clients/me/progress/${entryId}`);
  }
}

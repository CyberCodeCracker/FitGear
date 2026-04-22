import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ClientResponse, PageResponse,
  TrainingProgramResponse, TrainingProgramRequest,
  DietProgramResponse, DietProgramRequest
} from '../models/models';

const API = 'http://localhost:8080/api/v1';

@Injectable({ providedIn: 'root' })
export class CoachApiService {
  private http = inject(HttpClient);

  // ── Clients ──────────────────────────────────────────────────────
  getClients(page = 0, size = 10): Observable<PageResponse<ClientResponse>> {
    return this.http.get<PageResponse<ClientResponse>>(
      `${API}/coach/show-clients`, { params: { page, size } }
    );
  }
  searchClients(name: string, page = 0, size = 10): Observable<PageResponse<ClientResponse>> {
    return this.http.get<PageResponse<ClientResponse>>(
      `${API}/coach/show-client-name/${encodeURIComponent(name)}`, { params: { page, size } }
    );
  }
  getClientById(clientId: number): Observable<ClientResponse> {
    return this.http.get<ClientResponse>(`${API}/coach/show-client-id/${clientId}`);
  }

  // ── Training program ─────────────────────────────────────────────
  assignTrainingProgram(clientId: number, body: TrainingProgramRequest): Observable<void> {
    return this.http.post<void>(`${API}/clients/${clientId}/training/program`, body);
  }
  getTrainingProgram(clientId: number, programId: number): Observable<TrainingProgramResponse> {
    return this.http.get<TrainingProgramResponse>(`${API}/clients/${clientId}/training/${programId}`);
  }
  deleteTrainingProgram(clientId: number, programId: number): Observable<void> {
    return this.http.delete<void>(`${API}/clients/${clientId}/training/${programId}`);
  }

  // ── Diet program ─────────────────────────────────────────────────
  assignDietProgram(clientId: number, body: DietProgramRequest): Observable<void> {
    return this.http.post<void>(`${API}/clients/${clientId}/nutrition/diet`, body);
  }
  getDietProgram(clientId: number, programId: number): Observable<DietProgramResponse> {
    return this.http.get<DietProgramResponse>(`${API}/clients/${clientId}/nutrition/${programId}`);
  }
  deleteDietProgram(clientId: number, programId: number): Observable<void> {
    return this.http.delete<void>(`${API}/clients/${clientId}/nutrition/${programId}/delete-program`);
  }
}

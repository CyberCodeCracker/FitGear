import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientResponse, PageResponse } from '../models/models';

const API = 'http://localhost:8080/api/v1';

@Injectable({ providedIn: 'root' })
export class CoachApiService {
  private http = inject(HttpClient);

  // ── Clients ───────────────────────────────────────────────────────
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
}

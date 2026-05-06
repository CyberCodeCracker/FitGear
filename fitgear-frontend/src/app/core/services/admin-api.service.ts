import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminDashboardResponse, AdminUserResponse, PageResponse } from '../models/models';

const API = 'http://localhost:8080/api/v1';

@Injectable({ providedIn: 'root' })
export class AdminApiService {
  private http = inject(HttpClient);

  getDashboard(): Observable<AdminDashboardResponse> {
    return this.http.get<AdminDashboardResponse>(`${API}/admin/dashboard`);
  }

  getUsers(page = 0, size = 20, search = '', role = ''): Observable<PageResponse<AdminUserResponse>> {
    return this.http.get<PageResponse<AdminUserResponse>>(`${API}/admin/users`, {
      params: { page, size, search, role }
    });
  }

  toggleLock(id: number): Observable<AdminUserResponse> {
    return this.http.put<AdminUserResponse>(`${API}/admin/users/${id}/lock`, {});
  }

  toggleEnable(id: number): Observable<AdminUserResponse> {
    return this.http.put<AdminUserResponse>(`${API}/admin/users/${id}/enable`, {});
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/admin/users/${id}`);
  }
}

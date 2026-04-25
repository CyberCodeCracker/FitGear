import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, switchMap } from 'rxjs';
import {
  LoginRequest, AuthResponse, MeResponse,
  ClientRegistrationRequest, CoachRegistrationRequest,
  UpdateProfileRequest
} from '../models/models';

const API = 'http://localhost:8080/api/v1';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'fg_token';
  private readonly USER_KEY  = 'fg_user';

  private http   = inject(HttpClient);
  private router = inject(Router);

  private _user = signal<MeResponse | null>(this.loadUser());

  readonly user       = this._user.asReadonly();
  readonly isLoggedIn = computed(() => !!this._user());
  readonly isCoach    = computed(() => this._user()?.userType === 'COACH');
  readonly isClient   = computed(() => this._user()?.userType === 'CLIENT');

  // ── Login ─────────────────────────────────────────────────────────────────
  login(email: string, password: string): Observable<MeResponse> {
    return this.http.post<AuthResponse>(`${API}/auth/authenticate`, { email, password } as LoginRequest).pipe(
      tap(res => {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        if (res.refreshToken) localStorage.setItem('fg_refresh', res.refreshToken);
      }),
      switchMap(() => this.fetchMe())
    );
  }

  // ── Register ──────────────────────────────────────────────────────────────
  registerCoach(payload: CoachRegistrationRequest, file?: File | null): Observable<unknown> {
    const formData = new FormData();
    formData.append('request', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
    if (file) {
      formData.append('file', file);
    }
    return this.http.post(`${API}/auth/register/coach`, formData, { observe: 'response' });
  }

  registerClient(payload: ClientRegistrationRequest): Observable<unknown> {
    return this.http.post(`${API}/auth/register/client`, payload, { observe: 'response' });
  }

  // ── Me ────────────────────────────────────────────────────────────────────
  fetchMe(): Observable<MeResponse> {
    return this.http.get<MeResponse>(`${API}/me`).pipe(
      tap(me => {
        localStorage.setItem(this.USER_KEY, JSON.stringify(me));
        this._user.set(me);
      })
    );
  }

  // ── Update profile ────────────────────────────────────────────────────────
  updateProfile(payload: UpdateProfileRequest): Observable<MeResponse> {
    return this.http.put<MeResponse>(`${API}/me`, payload).pipe(
      tap(me => {
        localStorage.setItem(this.USER_KEY, JSON.stringify(me));
        this._user.set(me);
      })
    );
  }

  // ── Profile picture ───────────────────────────────────────────────────────
  uploadProfilePicture(file: File): Observable<{ profilePicture: string }> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post<{ profilePicture: string }>(`${API}/me/profile-picture`, fd).pipe(
      tap(res => {
        const u = this._user();
        if (u) {
          const updated = { ...u, profilePicture: res.profilePicture };
          localStorage.setItem(this.USER_KEY, JSON.stringify(updated));
          this._user.set(updated);
        }
      })
    );
  }

  deleteProfilePicture(): Observable<void> {
    return this.http.delete<void>(`${API}/me/profile-picture`).pipe(
      tap(() => {
        const u = this._user();
        if (u) {
          const updated = { ...u, profilePicture: null };
          localStorage.setItem(this.USER_KEY, JSON.stringify(updated));
          this._user.set(updated as any);
        }
      })
    );
  }

  /** Full URL for a profile picture relative path */
  pictureUrl(relativePath: string | undefined | null): string | null {
    if (!relativePath) return null;
    return `${API}/uploads/${relativePath}`;
  }

  // ── Logout ────────────────────────────────────────────────────────────────
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem('fg_refresh');
    localStorage.removeItem(this.USER_KEY);
    this._user.set(null);
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null { return localStorage.getItem(this.TOKEN_KEY); }

  private loadUser(): MeResponse | null {
    try {
      const raw = localStorage.getItem(this.USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch { return null; }
  }
}

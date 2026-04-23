import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
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
  registerCoach(payload: CoachRegistrationRequest): Observable<unknown> {
    return this.http.post(`${API}/auth/register/coach`, payload, { observe: 'response' });
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

  // ── Update profile ─────────────────────────────────────────────────────────
  updateProfile(payload: UpdateProfileRequest): Observable<MeResponse> {
    return this.http.put<MeResponse>(`${API}/me`, payload).pipe(
      tap(me => {
        localStorage.setItem(this.USER_KEY, JSON.stringify(me));
        this._user.set(me);
      })
    );
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

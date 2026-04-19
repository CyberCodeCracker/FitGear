import { Injectable, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { MeResponse } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'fg_token';
  private readonly USER_KEY  = 'fg_user';

  private _user = signal<MeResponse | null>(this.loadUser());

  readonly user     = this._user.asReadonly();
  readonly isLoggedIn = computed(() => !!this._user());
  readonly isCoach    = computed(() => this._user()?.userType === 'COACH');
  readonly isClient   = computed(() => this._user()?.userType === 'CLIENT');

  constructor(private router: Router) {}

  /** Mock login — replace body with real HTTP call */
  login(email: string, password: string): boolean {
    // Mock credentials
    const coaches = ['coach@fitgear.com'];
    const clients = ['client@fitgear.com'];

    let mockUser: MeResponse | null = null;

    if (coaches.includes(email) && password === 'password') {
      mockUser = { id: 1, firstName: 'Alex', lastName: 'Morgan', email, roles: ['ROLE_COACH'], userType: 'COACH', coach: null };
    } else if (clients.includes(email) && password === 'password') {
      mockUser = { id: 2, firstName: 'Jordan', lastName: 'Smith', email, roles: ['ROLE_CLIENT'], userType: 'CLIENT', coach: null };
    }

    if (mockUser) {
      const token = 'mock-jwt-token-' + Date.now();
      localStorage.setItem(this.TOKEN_KEY, token);
      localStorage.setItem(this.USER_KEY, JSON.stringify(mockUser));
      this._user.set(mockUser);
      return true;
    }
    return false;
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this._user.set(null);
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private loadUser(): MeResponse | null {
    try {
      const raw = localStorage.getItem(this.USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch { return null; }
  }
}

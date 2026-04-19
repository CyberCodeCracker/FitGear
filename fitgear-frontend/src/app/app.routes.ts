import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  // Default redirect
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

  // ── Auth (public) ──────────────────────────────────────────────
  {
    path: 'auth',
    children: [
      { path: 'login',  loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
      { path: 'verify', loadComponent: () => import('./features/auth/verify-email/verify-email.component').then(m => m.VerifyEmailComponent) },
      { path: '', redirectTo: 'login', pathMatch: 'full' },
    ],
  },

  // ── Client (role: CLIENT) ──────────────────────────────────────
  {
    path: 'client',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['CLIENT'] },
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/client/dashboard/client-dashboard.component').then(m => m.ClientDashboardComponent) },
      { path: 'coaches',   loadComponent: () => import('./features/client/coaches/coaches.component').then(m => m.CoachesComponent) },
      { path: 'progress',  loadComponent: () => import('./features/client/progress/client-progress.component').then(m => m.ClientProgressComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },

  // ── Coach (role: COACH) ────────────────────────────────────────
  {
    path: 'coach',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['COACH'] },
    children: [
      { path: 'dashboard',       loadComponent: () => import('./features/coach/dashboard/coach-dashboard.component').then(m => m.CoachDashboardComponent) },
      { path: 'clients',         loadComponent: () => import('./features/coach/clients/coach-clients.component').then(m => m.CoachClientsComponent) },
      { path: 'clients/:id',     loadComponent: () => import('./features/coach/client-detail/client-detail.component').then(m => m.ClientDetailComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },

  // ── Wildcard ──────────────────────────────────────────────────
  { path: '**', redirectTo: 'auth/login' },
];

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="min-h-screen bg-bg flex">
      <!-- Left panel -->
      <div class="auth-visual-panel hidden lg:flex flex-col justify-between w-2/5 bg-card p-12 border-r border-white/5 relative overflow-hidden">
        <div class="relative">
          <div class="flex items-center gap-3 mb-16">
            <div class="w-10 h-10 rounded-xl bg-accent flex items-center justify-center">
              <i class="fa-solid fa-dumbbell text-black"></i>
            </div>
            <span class="text-xl font-bold text-white">FitGear</span>
          </div>
          <h2 class="text-4xl font-bold text-white leading-tight mb-4">
            Your fitness<br>journey starts<br><span class="gradient-text">here.</span>
          </h2>
          <p class="text-gray-400 text-lg">Connect with expert coaches, track progress and hit your goals.</p>
        </div>
        <div class="relative space-y-4">
          <div *ngFor="let t of testimonials" class="card bg-card-2/50 flex items-start gap-3">
            <div class="w-9 h-9 rounded-full bg-accent/20 flex items-center justify-center flex-shrink-0 text-accent text-xs font-bold">{{ t.initials }}</div>
            <div>
              <p class="text-sm text-gray-300 italic">"{{ t.text }}"</p>
              <p class="text-xs text-gray-500 mt-1">— {{ t.name }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Right panel -->
      <div class="auth-form-panel flex-1 flex items-center justify-center px-6 py-12 relative overflow-hidden">
        <div class="relative z-10 w-full max-w-md animate-slide-up">
          <!-- Mobile logo -->
          <div class="flex items-center gap-2 mb-8 lg:hidden">
            <div class="w-8 h-8 rounded-lg bg-accent flex items-center justify-center">
              <i class="fa-solid fa-dumbbell text-black text-xs"></i>
            </div>
            <span class="font-bold text-lg text-white">FitGear</span>
          </div>

          <h1 class="text-3xl font-bold text-white mb-2">Welcome back</h1>
          <p class="text-muted mb-8">Sign in to continue to your dashboard.</p>

          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
            <div class="form-group">
              <label class="form-label">Email address</label>
              <div class="relative">
                <i class="fa-solid fa-envelope absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm"></i>
                <input formControlName="email" type="email" class="form-input pl-9"
                       placeholder="you@example.com"
                       [class.error]="f['email'].invalid && f['email'].touched">
              </div>
              <p *ngIf="f['email'].invalid && f['email'].touched" class="form-error">Enter a valid email address.</p>
            </div>

            <div class="form-group">
              <label class="form-label">Password</label>
              <div class="relative">
                <i class="fa-solid fa-lock absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm"></i>
                <input formControlName="password" [type]="showPw ? 'text' : 'password'" class="form-input pl-9 pr-10"
                       placeholder="••••••••"
                       [class.error]="f['password'].invalid && f['password'].touched">
                <button type="button" (click)="showPw = !showPw"
                        class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300">
                  <i class="fa-solid text-sm" [class.fa-eye]="!showPw" [class.fa-eye-slash]="showPw"></i>
                </button>
              </div>
              <p *ngIf="f['password'].invalid && f['password'].touched" class="form-error">Password is required.</p>
            </div>

            <p *ngIf="error" class="text-danger text-sm text-center bg-danger/10 rounded-lg px-3 py-2">
              <i class="fa-solid fa-circle-exclamation mr-1"></i>{{ error }}
            </p>

            <button type="submit" class="btn-primary btn-lg w-full justify-center" [disabled]="loading">
              <i class="fa-solid fa-spinner fa-spin" *ngIf="loading"></i>
              <i class="fa-solid fa-right-to-bracket" *ngIf="!loading"></i>
              {{ loading ? 'Signing in…' : 'Sign In' }}
            </button>
          </form>

          <p class="text-center text-muted mt-6">
            Don't have an account?
            <a routerLink="/auth/register" class="text-accent hover:underline ml-1">Create one</a>
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-visual-panel,
    .auth-form-panel {
      background-image: linear-gradient(rgba(17, 24, 39, 0.78), rgba(17, 24, 39, 0.86)), url('/images/gym_bg.png');
      background-size: cover;
      background-position: center;
    }

    .auth-form-panel::before {
      content: '';
      position: absolute;
      inset: 0;
      background: rgba(17, 24, 39, 0.82);
      pointer-events: none;
    }

    @media (min-width: 1024px) {
      .auth-form-panel {
        background-image: none;
      }

      .auth-form-panel::before {
        display: none;
      }
    }
  `]
})
export class LoginComponent {
  private fb     = inject(FormBuilder);
  private auth   = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  error   = '';
  loading = false;
  showPw  = false;

  get f() { return this.form.controls; }

  testimonials = [
    { initials: 'JS', name: 'Jordan S.', text: 'Lost 12kg in 4 months. Best investment I ever made.' },
    { initials: 'ML', name: 'Maria L.',  text: 'My coach updated my program every week. Results speak.' },
  ];

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.error   = '';
    this.auth.login(this.f['email'].value!, this.f['password'].value!).subscribe({
      next: me => {
        this.loading = false;
        if (me.userType === 'COACH') {
          this.router.navigate(['/coach/dashboard']);
        } else {
          // Clients without a coach go to discovery; the guard handles the rest
          this.router.navigate([me.coach ? '/client/dashboard' : '/client/coaches']);
        }
      },
      error: () => {
        this.loading = false;
        this.error = 'Invalid email or password.';
      }
    });
  }
}

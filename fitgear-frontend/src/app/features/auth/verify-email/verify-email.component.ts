import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="min-h-screen bg-bg flex items-center justify-center px-6 py-12">
      <div class="w-full max-w-md animate-slide-up">

        <!-- Logo -->
        <div class="flex items-center gap-2 mb-10">
          <div class="w-8 h-8 rounded-lg bg-accent flex items-center justify-center">
            <i class="fa-solid fa-dumbbell text-black text-xs"></i>
          </div>
          <span class="font-bold text-lg text-white">FitGear</span>
        </div>

        <!-- Icon -->
        <div class="w-16 h-16 rounded-2xl bg-accent/15 flex items-center justify-center mb-6">
          <i class="fa-solid fa-envelope-circle-check text-accent text-3xl"></i>
        </div>

        <h1 class="text-3xl font-bold text-white mb-2">Verify your email</h1>
        <p class="text-muted mb-8">We sent a verification token to your email address. Paste it below to activate your account.</p>

        <ng-container *ngIf="!success; else successBlock">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
            <div class="form-group">
              <label class="form-label">Verification Token</label>
              <input formControlName="token" type="text" class="form-input font-mono tracking-widest text-center text-lg"
                     placeholder="XXXXXXXX-XXXX-XXXX"
                     [class.error]="f['token'].invalid && f['token'].touched">
              <p *ngIf="f['token'].invalid && f['token'].touched" class="form-error">Token is required.</p>
            </div>

            <p *ngIf="error" class="text-danger text-sm bg-danger/10 rounded-lg px-3 py-2 text-center">
              <i class="fa-solid fa-circle-exclamation mr-1"></i>{{ error }}
            </p>

            <button type="submit" class="btn-primary btn-lg w-full justify-center" [disabled]="loading">
              <i class="fa-solid fa-spinner fa-spin" *ngIf="loading"></i>
              <i class="fa-solid fa-check" *ngIf="!loading"></i>
              {{ loading ? 'Verifying…' : 'Confirm Account' }}
            </button>
          </form>
        </ng-container>

        <ng-template #successBlock>
          <div class="card bg-accent/10 border-accent/20 text-center py-8 animate-fade-in">
            <i class="fa-solid fa-circle-check text-accent text-5xl mb-4"></i>
            <h2 class="text-xl font-bold text-white mb-2">Account Verified!</h2>
            <p class="text-muted mb-6">Your email has been confirmed. You can now sign in.</p>
            <a routerLink="/auth/login" class="btn-primary mx-auto">
              <i class="fa-solid fa-right-to-bracket"></i> Go to Login
            </a>
          </div>
        </ng-template>

        <p class="text-center text-muted mt-6">
          <a routerLink="/auth/login" class="text-accent hover:underline">
            <i class="fa-solid fa-arrow-left text-xs mr-1"></i> Back to login
          </a>
        </p>
      </div>
    </div>
  `
})
export class VerifyEmailComponent {
  private fb     = inject(FormBuilder);
  private router = inject(Router);

  form = this.fb.group({ token: ['', Validators.required] });

  error   = '';
  loading = false;
  success = false;

  get f() { return this.form.controls; }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.error   = '';
    setTimeout(() => {
      this.loading = false;
      // Mock: any non-empty token succeeds
      this.success = true;
    }, 800);
  }
}

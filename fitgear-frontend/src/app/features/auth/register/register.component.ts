import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

function passwordsMatch(g: AbstractControl): ValidationErrors | null {
  return g.get('password')?.value === g.get('passwordConfirm')?.value
    ? null : { mismatch: true };
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="min-h-screen bg-bg flex">

      <!-- ── Left panel ─────────────────────────────────────────── -->
      <div class="auth-visual-panel hidden lg:flex flex-col justify-between w-2/5 bg-card p-12 border-r border-white/5 relative overflow-hidden">
        <div class="relative">
          <div class="flex items-center gap-3 mb-16">
            <div class="w-10 h-10 rounded-xl bg-accent flex items-center justify-center">
              <i class="fa-solid fa-dumbbell text-black"></i>
            </div>
            <span class="text-xl font-bold text-white">FitGear</span>
          </div>
          <h2 class="text-4xl font-bold text-white leading-tight mb-4">
            Join a community<br>built around<br><span class="gradient-text">results.</span>
          </h2>
          <p class="text-gray-400 text-lg">Whether you coach or train, FitGear gives you the tools to succeed.</p>
        </div>
        <div class="relative space-y-4">
          <div class="card bg-card-2/50 flex items-start gap-3">
            <div class="w-9 h-9 rounded-full bg-accent/20 flex items-center justify-center flex-shrink-0">
              <i class="fa-solid fa-users text-accent text-xs"></i>
            </div>
            <div>
              <p class="text-sm font-medium text-white mb-0.5">For Coaches</p>
              <p class="text-xs text-gray-400">Build your roster, create programs, and grow your business.</p>
            </div>
          </div>
          <div class="card bg-card-2/50 flex items-start gap-3">
            <div class="w-9 h-9 rounded-full bg-accent/20 flex items-center justify-center flex-shrink-0">
              <i class="fa-solid fa-chart-line text-accent text-xs"></i>
            </div>
            <div>
              <p class="text-sm font-medium text-white mb-0.5">For Clients</p>
              <p class="text-xs text-gray-400">Track every rep, meal and milestone with your coach by your side.</p>
            </div>
          </div>
        </div>
      </div>

      <!-- ── Right panel ─────────────────────────────────────────── -->
      <div class="auth-form-panel flex-1 flex items-center justify-center px-6 py-12 overflow-y-auto relative">
        <div class="relative z-10 w-full max-w-lg animate-slide-up">

          <!-- Mobile logo -->
          <div class="flex items-center gap-2 mb-8 lg:hidden">
            <div class="w-8 h-8 rounded-lg bg-accent flex items-center justify-center">
              <i class="fa-solid fa-dumbbell text-black text-xs"></i>
            </div>
            <span class="font-bold text-lg text-white">FitGear</span>
          </div>

          <!-- Success state -->
          <ng-container *ngIf="success; else formBlock">
            <div class="card bg-accent/10 border-accent/20 text-center py-10 animate-fade-in">
              <i class="fa-solid fa-envelope-circle-check text-accent text-5xl mb-4"></i>
              <h2 class="text-2xl font-bold text-white mb-2">Check your inbox!</h2>
              <p class="text-muted mb-6">
                We sent a verification code to <span class="text-white">{{ registeredEmail }}</span>.
                Enter it on the next screen to activate your account.
              </p>
              <a routerLink="/auth/verify" class="btn-primary mx-auto">
                <i class="fa-solid fa-envelope-open-text"></i> Verify Email
              </a>
            </div>
          </ng-container>

          <ng-template #formBlock>
            <h1 class="text-3xl font-bold text-white mb-1">Create an account</h1>
            <p class="text-muted mb-6">Choose your role to get started.</p>

            <!-- Role tabs -->
            <div class="flex rounded-xl bg-card-2 p-1 mb-7 border border-white/5">
              <button type="button"
                      class="flex-1 py-2.5 rounded-lg text-sm font-medium transition-all duration-200"
                      [class]="tab==='coach' ? 'bg-accent text-black shadow' : 'text-gray-400 hover:text-white'"
                      (click)="switchTab('coach')">
                <i class="fa-solid fa-whistle mr-2"></i>Coach
              </button>
              <button type="button"
                      class="flex-1 py-2.5 rounded-lg text-sm font-medium transition-all duration-200"
                      [class]="tab==='client' ? 'bg-accent text-black shadow' : 'text-gray-400 hover:text-white'"
                      (click)="switchTab('client')">
                <i class="fa-solid fa-person-running mr-2"></i>Client
              </button>
            </div>

            <!-- ── COACH FORM ─────────────────────────────────────── -->
            <form *ngIf="tab === 'coach'" [formGroup]="coachForm" (ngSubmit)="submitCoach()" class="space-y-4">

              <div class="grid grid-cols-2 gap-4">
                <div class="form-group">
                  <label class="form-label">First name</label>
                  <input formControlName="firstName" type="text" class="form-input" placeholder="Alex"
                         [class.error]="cf['firstName'].invalid && cf['firstName'].touched">
                  <p *ngIf="cf['firstName'].invalid && cf['firstName'].touched" class="form-error">Required.</p>
                </div>
                <div class="form-group">
                  <label class="form-label">Last name</label>
                  <input formControlName="lastName" type="text" class="form-input" placeholder="Morgan"
                         [class.error]="cf['lastName'].invalid && cf['lastName'].touched">
                  <p *ngIf="cf['lastName'].invalid && cf['lastName'].touched" class="form-error">Required.</p>
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Email address</label>
                <div class="relative">
                  <i class="fa-solid fa-envelope absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm"></i>
                  <input formControlName="email" type="email" class="form-input pl-9" placeholder="coach@example.com"
                         [class.error]="cf['email'].invalid && cf['email'].touched">
                </div>
                <p *ngIf="cf['email'].invalid && cf['email'].touched" class="form-error">Enter a valid email.</p>
              </div>

              <div class="form-group">
                <label class="form-label">Phone number</label>
                <div class="relative">
                  <i class="fa-solid fa-phone absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm"></i>
                  <input formControlName="phoneNumber" type="tel" class="form-input pl-9" placeholder="12345678"
                         [class.error]="cf['phoneNumber'].invalid && cf['phoneNumber'].touched">
                </div>
                <p *ngIf="cf['phoneNumber'].invalid && cf['phoneNumber'].touched" class="form-error">8-digit number required.</p>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div class="form-group">
                  <label class="form-label">Experience (years)</label>
                  <input formControlName="yearsOfExperience" type="number" min="0" class="form-input" placeholder="5"
                         [class.error]="cf['yearsOfExperience'].invalid && cf['yearsOfExperience'].touched">
                  <p *ngIf="cf['yearsOfExperience'].invalid && cf['yearsOfExperience'].touched" class="form-error">Required.</p>
                </div>
                <div class="form-group">
                  <label class="form-label">Monthly rate ($)</label>
                  <input formControlName="monthlyRate" type="number" min="1" class="form-input" placeholder="99"
                         [class.error]="cf['monthlyRate'].invalid && cf['monthlyRate'].touched">
                  <p *ngIf="cf['monthlyRate'].invalid && cf['monthlyRate'].touched" class="form-error">Required.</p>
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Profile picture (optional, max 5 MB)</label>
                <div class="flex items-center gap-4">
                  <div class="relative group">
                    <div *ngIf="!picturePreview"
                         class="w-16 h-16 rounded-full bg-card-2 border-2 border-dashed border-white/10
                                flex items-center justify-center cursor-pointer hover:border-accent/30 transition-colors">
                      <i class="fa-solid fa-camera text-gray-500 group-hover:text-accent"></i>
                    </div>
                    <img *ngIf="picturePreview" [src]="picturePreview"
                         class="w-16 h-16 rounded-full object-cover border-2 border-accent/30">
                    <input type="file" accept="image/jpeg,image/png,image/webp,image/gif"
                           class="absolute inset-0 opacity-0 cursor-pointer"
                           (change)="onFileSelected($event)">
                  </div>
                  <div class="flex-1">
                    <p class="text-sm text-gray-400">{{ selectedFile ? selectedFile.name : 'No file selected' }}</p>
                    <p class="text-xs text-gray-600">JPEG, PNG, WebP or GIF</p>
                  </div>
                  <button *ngIf="selectedFile" type="button" (click)="clearFile()"
                          class="btn-icon text-danger hover:bg-danger/10">
                    <i class="fa-solid fa-xmark"></i>
                  </button>
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Bio / Description</label>
                <textarea formControlName="description" rows="3" class="form-input resize-none" placeholder="Tell clients about yourself…"
                          [class.error]="cf['description'].invalid && cf['description'].touched"></textarea>
                <p *ngIf="cf['description'].invalid && cf['description'].touched" class="form-error">Required.</p>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div class="form-group">
                  <label class="form-label">Password</label>
                  <div class="relative">
                    <i class="fa-solid fa-lock absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm"></i>
                    <input formControlName="password" [type]="showPw ? 'text' : 'password'"
                           class="form-input pl-9 pr-9" placeholder="••••••••"
                           [class.error]="(cf['password'].invalid && cf['password'].touched) || (coachForm.hasError('mismatch') && cf['passwordConfirm'].touched)">
                    <button type="button" (click)="showPw=!showPw"
                            class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300">
                      <i class="fa-solid text-sm" [class.fa-eye]="!showPw" [class.fa-eye-slash]="showPw"></i>
                    </button>
                  </div>
                  <p *ngIf="cf['password'].invalid && cf['password'].touched" class="form-error">8–20 chars.</p>
                </div>
                <div class="form-group">
                  <label class="form-label">Confirm password</label>
                  <input formControlName="passwordConfirm" [type]="showPw ? 'text' : 'password'"
                         class="form-input" placeholder="••••••••"
                         [class.error]="coachForm.hasError('mismatch') && cf['passwordConfirm'].touched">
                  <p *ngIf="coachForm.hasError('mismatch') && cf['passwordConfirm'].touched" class="form-error">Passwords don't match.</p>
                </div>
              </div>

              <p *ngIf="error" class="text-danger text-sm bg-danger/10 rounded-lg px-3 py-2 text-center">
                <i class="fa-solid fa-circle-exclamation mr-1"></i>{{ error }}
              </p>

              <button type="submit" class="btn-primary btn-lg w-full justify-center" [disabled]="loading">
                <i class="fa-solid fa-spinner fa-spin" *ngIf="loading"></i>
                <i class="fa-solid fa-whistle" *ngIf="!loading"></i>
                {{ loading ? 'Creating account…' : 'Register as Coach' }}
              </button>
            </form>
            <!-- end COACH FORM -->

            <!-- ── CLIENT FORM ─────────────────────────────────────── -->
            <form *ngIf="tab === 'client'" [formGroup]="clientForm" (ngSubmit)="submitClient()" class="space-y-4">

              <div class="grid grid-cols-2 gap-4">
                <div class="form-group">
                  <label class="form-label">First name</label>
                  <input formControlName="firstName" type="text" class="form-input" placeholder="Jordan"
                         [class.error]="lf['firstName'].invalid && lf['firstName'].touched">
                  <p *ngIf="lf['firstName'].invalid && lf['firstName'].touched" class="form-error">Required.</p>
                </div>
                <div class="form-group">
                  <label class="form-label">Last name</label>
                  <input formControlName="lastName" type="text" class="form-input" placeholder="Smith"
                         [class.error]="lf['lastName'].invalid && lf['lastName'].touched">
                  <p *ngIf="lf['lastName'].invalid && lf['lastName'].touched" class="form-error">Required.</p>
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Email address</label>
                <div class="relative">
                  <i class="fa-solid fa-envelope absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm"></i>
                  <input formControlName="email" type="email" class="form-input pl-9" placeholder="you@example.com"
                         [class.error]="lf['email'].invalid && lf['email'].touched">
                </div>
                <p *ngIf="lf['email'].invalid && lf['email'].touched" class="form-error">Enter a valid email.</p>
              </div>

              <div class="grid grid-cols-3 gap-3">
                <div class="form-group">
                  <label class="form-label">Height (cm)</label>
                  <input formControlName="height" type="number" class="form-input" placeholder="175"
                         [class.error]="lf['height'].invalid && lf['height'].touched">
                  <p *ngIf="lf['height'].invalid && lf['height'].touched" class="form-error">Min 100 cm.</p>
                </div>
                <div class="form-group">
                  <label class="form-label">Weight (kg)</label>
                  <input formControlName="weight" type="number" class="form-input" placeholder="70"
                         [class.error]="lf['weight'].invalid && lf['weight'].touched">
                  <p *ngIf="lf['weight'].invalid && lf['weight'].touched" class="form-error">Min 40 kg.</p>
                </div>
                <div class="form-group">
                  <label class="form-label">Body fat (%)</label>
                  <input formControlName="bodyFatPercentage" type="number" class="form-input" placeholder="18"
                         [class.error]="lf['bodyFatPercentage'].invalid && lf['bodyFatPercentage'].touched">
                  <p *ngIf="lf['bodyFatPercentage'].invalid && lf['bodyFatPercentage'].touched" class="form-error">0–80%.</p>
                </div>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div class="form-group">
                  <label class="form-label">Password</label>
                  <div class="relative">
                    <i class="fa-solid fa-lock absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm"></i>
                    <input formControlName="password" [type]="showPw ? 'text' : 'password'"
                           class="form-input pl-9 pr-9" placeholder="••••••••"
                           [class.error]="(lf['password'].invalid && lf['password'].touched) || (clientForm.hasError('mismatch') && lf['passwordConfirm'].touched)">
                    <button type="button" (click)="showPw=!showPw"
                            class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300">
                      <i class="fa-solid text-sm" [class.fa-eye]="!showPw" [class.fa-eye-slash]="showPw"></i>
                    </button>
                  </div>
                  <p *ngIf="lf['password'].invalid && lf['password'].touched" class="form-error">8–20 chars.</p>
                </div>
                <div class="form-group">
                  <label class="form-label">Confirm password</label>
                  <input formControlName="passwordConfirm" [type]="showPw ? 'text' : 'password'"
                         class="form-input" placeholder="••••••••"
                         [class.error]="clientForm.hasError('mismatch') && lf['passwordConfirm'].touched">
                  <p *ngIf="clientForm.hasError('mismatch') && lf['passwordConfirm'].touched" class="form-error">Passwords don't match.</p>
                </div>
              </div>

              <p *ngIf="error" class="text-danger text-sm bg-danger/10 rounded-lg px-3 py-2 text-center">
                <i class="fa-solid fa-circle-exclamation mr-1"></i>{{ error }}
              </p>

              <button type="submit" class="btn-primary btn-lg w-full justify-center" [disabled]="loading">
                <i class="fa-solid fa-spinner fa-spin" *ngIf="loading"></i>
                <i class="fa-solid fa-person-running" *ngIf="!loading"></i>
                {{ loading ? 'Creating account…' : 'Register as Client' }}
              </button>
            </form>
            <!-- end CLIENT FORM -->

            <p class="text-center text-muted mt-6">
              Already have an account?
              <a routerLink="/auth/login" class="text-accent hover:underline ml-1">Sign in</a>
            </p>
          </ng-template>
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
export class RegisterComponent {
  private fb   = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  tab: 'coach' | 'client' = 'coach';
  loading = false;
  error   = '';
  showPw  = false;
  success = false;
  registeredEmail = '';
  selectedFile: File | null = null;
  picturePreview: string | null = null;

  // ── Coach form ──────────────────────────────────────────────────────────
  coachForm = this.fb.group({
    firstName:        ['', Validators.required],
    lastName:         ['', Validators.required],
    email:            ['', [Validators.required, Validators.email]],
    phoneNumber:      ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
    description:      ['', Validators.required],
    yearsOfExperience:[0,  [Validators.required, Validators.min(0)]],
    monthlyRate:      [0,  [Validators.required, Validators.min(1)]],
    password:         ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    passwordConfirm:  ['', Validators.required],
  }, { validators: passwordsMatch });

  // ── Client form ─────────────────────────────────────────────────────────
  clientForm = this.fb.group({
    firstName:         ['', Validators.required],
    lastName:          ['', Validators.required],
    email:             ['', [Validators.required, Validators.email]],
    height:            [null, [Validators.required, Validators.min(100)]],
    weight:            [null, [Validators.required, Validators.min(40)]],
    bodyFatPercentage: [null, [Validators.required, Validators.min(0), Validators.max(80)]],
    password:          ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    passwordConfirm:   ['', Validators.required],
  }, { validators: passwordsMatch });

  get cf() { return this.coachForm.controls; }
  get lf() { return this.clientForm.controls; }

  switchTab(t: 'coach' | 'client'): void {
    this.tab   = t;
    this.error = '';
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    if (file.size > 5 * 1024 * 1024) { this.error = 'File exceeds 5 MB limit.'; return; }
    this.selectedFile = file;
    // Generate preview
    const reader = new FileReader();
    reader.onload = () => this.picturePreview = reader.result as string;
    reader.readAsDataURL(file);
  }

  clearFile(): void {
    this.selectedFile = null;
    this.picturePreview = null;
  }

  submitCoach(): void {
    if (this.coachForm.invalid) { this.coachForm.markAllAsTouched(); return; }
    this.loading = true; this.error = '';
    const v = this.coachForm.value;

    this.auth.registerCoach({
      firstName: v.firstName!, lastName: v.lastName!, email: v.email!,
      phoneNumber: v.phoneNumber!, description: v.description!,
      yearsOfExperience: Number(v.yearsOfExperience), monthlyRate: Number(v.monthlyRate),
      password: v.password!, passwordConfirm: v.passwordConfirm!
    }, this.selectedFile).subscribe({
      next: () => { this.loading = false; this.registeredEmail = v.email!; this.success = true; },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message ?? 'Registration failed. Please try again.';
      }
    });
  }

  submitClient(): void {
    if (this.clientForm.invalid) { this.clientForm.markAllAsTouched(); return; }
    this.loading = true; this.error = '';
    const v = this.clientForm.value;
    this.auth.registerClient({
      firstName: v.firstName!, lastName: v.lastName!, email: v.email!,
      height: Number(v.height), weight: Number(v.weight),
      bodyFatPercentage: Number(v.bodyFatPercentage),
      password: v.password!, passwordConfirm: v.passwordConfirm!
    }).subscribe({
      next: () => { this.loading = false; this.registeredEmail = v.email!; this.success = true; },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message ?? 'Registration failed. Please try again.';
      }
    });
  }
}

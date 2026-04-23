import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../shared/components/toast/toast.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, SidebarComponent, NavbarComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>
      <div class="flex-1 flex flex-col min-w-0">
        <app-navbar title="My Profile"></app-navbar>

        <main class="flex-1 p-6 space-y-6 animate-fade-in">

          <!-- Header -->
          <div class="card bg-gradient-to-r from-accent/15 via-card to-card border-accent/20">
            <div class="flex items-center gap-5">
              <div class="w-20 h-20 rounded-full bg-gradient-to-br from-accent/30 to-accent/10
                          flex items-center justify-center text-accent text-3xl font-bold flex-shrink-0">
                {{ initials }}
              </div>
              <div>
                <h2 class="text-2xl font-bold text-white">
                  {{ auth.user()?.firstName }} {{ auth.user()?.lastName }}
                </h2>
                <p class="text-muted">{{ auth.user()?.email }}</p>
                <span class="badge mt-1.5"
                      [class.badge-green]="auth.isCoach()"
                      [class.badge-blue]="auth.isClient()">
                  <i class="fa-solid" [class.fa-shield-halved]="auth.isCoach()" [class.fa-user]="auth.isClient()"></i>
                  {{ auth.isCoach() ? 'Coach' : 'Client' }}
                </span>
              </div>
            </div>
          </div>

          <!-- Form -->
          <div class="card space-y-6">
            <div class="flex items-center justify-between">
              <h3 class="section-title flex items-center gap-2">
                <i class="fa-solid fa-pen-to-square text-accent"></i> Edit Profile
              </h3>
              <span *ngIf="saved()" class="badge badge-green animate-fade-in">
                <i class="fa-solid fa-check"></i> Saved!
              </span>
            </div>

            <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">

              <!-- Common fields -->
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div class="form-group">
                  <label class="form-label">First name</label>
                  <input formControlName="firstName" type="text" class="form-input"
                         [class.error]="f['firstName'].invalid && f['firstName'].touched">
                  <p *ngIf="f['firstName'].invalid && f['firstName'].touched" class="form-error">Required.</p>
                </div>
                <div class="form-group">
                  <label class="form-label">Last name</label>
                  <input formControlName="lastName" type="text" class="form-input"
                         [class.error]="f['lastName'].invalid && f['lastName'].touched">
                  <p *ngIf="f['lastName'].invalid && f['lastName'].touched" class="form-error">Required.</p>
                </div>
              </div>

              <!-- Email (read-only) -->
              <div class="form-group">
                <label class="form-label">Email address</label>
                <div class="relative">
                  <i class="fa-solid fa-envelope absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm"></i>
                  <input [value]="auth.user()?.email" type="email" class="form-input pl-9 opacity-60 cursor-not-allowed" disabled>
                </div>
                <p class="text-xs text-gray-600 mt-1">Email cannot be changed.</p>
              </div>

              <!-- ─── CLIENT FIELDS ─────────────────────────────── -->
              <ng-container *ngIf="auth.isClient()">
                <div class="divider"></div>
                <p class="text-xs text-gray-500 uppercase tracking-wider">Body metrics</p>
                <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                  <div class="form-group">
                    <label class="form-label">Height (cm)</label>
                    <input formControlName="height" type="number" class="form-input" min="100"
                           [class.error]="f['height'].invalid && f['height'].touched">
                    <p *ngIf="f['height'].invalid && f['height'].touched" class="form-error">Min 100 cm.</p>
                  </div>
                  <div class="form-group">
                    <label class="form-label">Weight (kg)</label>
                    <input formControlName="weight" type="number" class="form-input" min="40"
                           [class.error]="f['weight'].invalid && f['weight'].touched">
                    <p *ngIf="f['weight'].invalid && f['weight'].touched" class="form-error">Min 40 kg.</p>
                  </div>
                  <div class="form-group">
                    <label class="form-label">Body fat (%)</label>
                    <input formControlName="bodyFatPercentage" type="number" class="form-input" min="0" max="80"
                           [class.error]="f['bodyFatPercentage'].invalid && f['bodyFatPercentage'].touched">
                    <p *ngIf="f['bodyFatPercentage'].invalid && f['bodyFatPercentage'].touched" class="form-error">0–80%.</p>
                  </div>
                </div>
              </ng-container>

              <!-- ─── COACH FIELDS ──────────────────────────────── -->
              <ng-container *ngIf="auth.isCoach()">
                <div class="divider"></div>
                <p class="text-xs text-gray-500 uppercase tracking-wider">Coaching profile</p>

                <div class="form-group">
                  <label class="form-label">Bio / Description</label>
                  <textarea formControlName="description" rows="3" class="form-input resize-none"
                            placeholder="Tell clients about yourself…"></textarea>
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                  <div class="form-group">
                    <label class="form-label">Phone number</label>
                    <input formControlName="phoneNumber" type="tel" class="form-input" placeholder="12345678"
                           [class.error]="f['phoneNumber'].invalid && f['phoneNumber'].touched">
                    <p *ngIf="f['phoneNumber'].invalid && f['phoneNumber'].touched" class="form-error">8-digit number.</p>
                  </div>
                  <div class="form-group">
                    <label class="form-label">Experience (years)</label>
                    <input formControlName="yearsOfExperience" type="number" class="form-input" min="0"
                           [class.error]="f['yearsOfExperience'].invalid && f['yearsOfExperience'].touched">
                  </div>
                  <div class="form-group">
                    <label class="form-label">Monthly rate ($)</label>
                    <input formControlName="monthlyRate" type="number" class="form-input" min="1"
                           [class.error]="f['monthlyRate'].invalid && f['monthlyRate'].touched">
                  </div>
                </div>

                <div class="flex items-center gap-3 px-1">
                  <label class="relative inline-flex items-center cursor-pointer">
                    <input formControlName="isAvailable" type="checkbox" class="sr-only peer">
                    <div class="w-11 h-6 bg-card-2 peer-focus:ring-2 peer-focus:ring-accent/30
                                rounded-full peer peer-checked:bg-accent transition-colors
                                after:content-[''] after:absolute after:top-[2px] after:left-[2px]
                                after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-transform
                                peer-checked:after:translate-x-full"></div>
                  </label>
                  <div>
                    <p class="text-sm font-medium text-white">Available for new clients</p>
                    <p class="text-xs text-gray-500">Turn off to hide from the coach discovery page.</p>
                  </div>
                </div>
              </ng-container>

              <!-- Submit -->
              <div class="flex items-center gap-3 pt-2">
                <button type="submit" class="btn-primary" [disabled]="loading() || form.invalid">
                  <i class="fa-solid fa-spinner fa-spin" *ngIf="loading()"></i>
                  <i class="fa-solid fa-floppy-disk" *ngIf="!loading()"></i>
                  {{ loading() ? 'Saving…' : 'Save Changes' }}
                </button>
                <button type="button" (click)="resetForm()" class="btn-secondary">
                  <i class="fa-solid fa-rotate-left"></i> Reset
                </button>
              </div>
            </form>
          </div>

        </main>
      </div>
    </div>
  `
})
export class ProfileComponent implements OnInit {
  auth    = inject(AuthService);
  private fb    = inject(FormBuilder);
  private toast = inject(ToastService);

  loading = signal(false);
  saved   = signal(false);

  form = this.fb.group({
    firstName:         ['', Validators.required],
    lastName:          ['', Validators.required],
    // Client
    height:            [null as number | null, [Validators.min(100)]],
    weight:            [null as number | null, [Validators.min(40)]],
    bodyFatPercentage: [null as number | null, [Validators.min(0), Validators.max(80)]],
    // Coach
    description:       [''],
    phoneNumber:       ['', [Validators.pattern(/^\d{8}$/)]],
    yearsOfExperience: [null as number | null, [Validators.min(0)]],
    monthlyRate:       [null as number | null, [Validators.min(1)]],
    isAvailable:       [true],
  });

  get f() { return this.form.controls; }

  get initials(): string {
    const u = this.auth.user();
    return u ? ((u.firstName?.[0] ?? '') + (u.lastName?.[0] ?? '')).toUpperCase() : '?';
  }

  ngOnInit(): void { this.resetForm(); }

  resetForm(): void {
    const u = this.auth.user();
    if (!u) return;
    this.form.patchValue({
      firstName:         u.firstName,
      lastName:          u.lastName,
      height:            u.height ?? null,
      weight:            u.weight ?? null,
      bodyFatPercentage: u.bodyFatPercentage ?? null,
      description:       u.description ?? '',
      phoneNumber:       u.phoneNumber ?? '',
      yearsOfExperience: u.yearsOfExperience ?? null,
      monthlyRate:       u.monthlyRate ?? null,
      isAvailable:       u.isAvailable ?? true,
    });
    this.form.markAsPristine();
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading.set(true);

    const v = this.form.value;
    const payload: any = { firstName: v.firstName!, lastName: v.lastName! };

    if (this.auth.isClient()) {
      payload.height            = v.height;
      payload.weight            = v.weight;
      payload.bodyFatPercentage = v.bodyFatPercentage;
    } else {
      payload.description       = v.description;
      payload.phoneNumber       = v.phoneNumber;
      payload.yearsOfExperience = v.yearsOfExperience;
      payload.monthlyRate       = v.monthlyRate;
      payload.isAvailable       = v.isAvailable;
    }

    this.auth.updateProfile(payload).subscribe({
      next: () => {
        this.loading.set(false);
        this.saved.set(true);
        this.toast.success('Profile updated successfully!');
        setTimeout(() => this.saved.set(false), 2500);
      },
      error: (err) => {
        this.loading.set(false);
        this.toast.error(err?.error?.message ?? 'Failed to update profile.');
      }
    });
  }
}

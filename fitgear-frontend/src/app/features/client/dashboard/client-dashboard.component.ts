import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { AuthService } from '../../../core/services/auth.service';
import { ClientApiService } from '../../../core/services/client-api.service';
import { TrainingProgramResponse, DietProgramResponse } from '../../../core/models/models';

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarComponent, NavbarComponent, StatCardComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>

      <div class="flex-1 flex flex-col min-w-0">
        <app-navbar title="Dashboard">
          <a routerLink="/client/progress" class="btn-secondary btn-sm">
            <i class="fa-solid fa-chart-line"></i> Track Progress
          </a>
        </app-navbar>

        <main class="flex-1 p-6 space-y-6 animate-fade-in">

          <!-- Welcome banner -->
          <div class="card bg-gradient-to-r from-accent/15 via-card to-card border-accent/20">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-400 text-sm mb-1">Good {{ greeting }} 👋</p>
                <h2 class="text-2xl font-bold text-white">
                  {{ auth.user()?.firstName }} {{ auth.user()?.lastName }}
                </h2>
                <p class="text-muted mt-1">Here's your progress overview for this month.</p>
              </div>
              <div class="hidden md:block text-6xl opacity-20">💪</div>
            </div>
          </div>

          <!-- Stats row (from registration data) -->
          <div class="grid grid-cols-2 lg:grid-cols-3 gap-4">
            <app-stat-card label="Height" [value]="auth.user()?.height ?? '—'" unit="cm"
              icon="fa-ruler-vertical" iconColor="#22C55E" iconBg="rgba(34,197,94,0.15)"></app-stat-card>
            <app-stat-card label="Weight" [value]="auth.user()?.weight ?? '—'" unit="kg"
              icon="fa-weight-scale" iconColor="#3B82F6" iconBg="rgba(59,130,246,0.15)"></app-stat-card>
            <app-stat-card label="Body Fat" [value]="auth.user()?.bodyFatPercentage ?? '—'" unit="%"
              icon="fa-percent" iconColor="#F59E0B" iconBg="rgba(245,158,11,0.15)"></app-stat-card>
          </div>

          <!-- Middle row: Training + Diet -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">

            <!-- Training Program -->
            <div class="card space-y-4">
              <div class="flex items-center justify-between">
                <h3 class="section-title flex items-center gap-2">
                  <i class="fa-solid fa-dumbbell text-accent"></i> Training Program
                </h3>
                <span *ngIf="!loadingTraining() && training()?.trainingDays?.length" class="badge badge-green">Active</span>
              </div>

              <!-- Skeleton -->
              <div *ngIf="loadingTraining()" class="space-y-2">
                <div *ngFor="let s of [1,2,3]" class="h-14 bg-card-2 rounded-lg animate-pulse"></div>
              </div>

              <!-- Training days -->
              <div *ngIf="!loadingTraining() && training()?.trainingDays?.length" class="space-y-2">
                <div *ngFor="let day of training()!.trainingDays"
                     class="flex items-center justify-between py-2.5 px-3 rounded-lg bg-card-2/60 hover:bg-card-2 transition-colors">
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-lg bg-accent/15 flex items-center justify-center">
                      <i class="fa-solid fa-calendar-day text-accent text-xs"></i>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-white">{{ day.dayOfWeek | titlecase }}</p>
                      <p class="text-xs text-gray-500">{{ day.title }}</p>
                    </div>
                  </div>
                  <div class="flex items-center gap-2">
                    <span class="badge badge-green text-xs">{{ day.exercises.length }} ex.</span>
                    <span class="text-xs text-gray-500">~{{ day.estimatedBurnedCalories }} kcal</span>
                  </div>
                </div>
              </div>

              <!-- No training yet -->
              <div *ngIf="!loadingTraining() && !training()?.trainingDays?.length"
                   class="flex flex-col items-center justify-center py-8 text-center gap-2">
                <i class="fa-solid fa-dumbbell text-gray-600 text-3xl"></i>
                <p class="text-gray-400 text-sm">No training program yet.</p>
                <p class="text-gray-600 text-xs">Your coach will assign one soon.</p>
              </div>
            </div>

            <!-- Diet Plan -->
            <div class="card space-y-4">
              <div class="flex items-center justify-between">
                <h3 class="section-title flex items-center gap-2">
                  <i class="fa-solid fa-bowl-food text-warning"></i> Diet Plan
                </h3>
                <span *ngIf="!loadingDiet() && diet()" class="badge badge-yellow truncate max-w-[140px]">
                  {{ diet()!.title }}
                </span>
              </div>

              <!-- Skeleton -->
              <div *ngIf="loadingDiet()" class="space-y-2">
                <div *ngFor="let s of [1,2,3,4]" class="h-14 bg-card-2 rounded-lg animate-pulse"></div>
              </div>

              <!-- Diet days -->
              <ng-container *ngIf="!loadingDiet() && diet()?.days?.length">
                <div *ngFor="let day of diet()!.days" class="space-y-2">
                  <p class="text-xs text-gray-500 uppercase tracking-wider px-1">
                    {{ day.dayOfWeek | titlecase }}
                  </p>
                  <div *ngFor="let meal of day.meals"
                       class="flex items-start gap-3 py-2.5 px-3 rounded-lg bg-card-2/60 hover:bg-card-2 transition-colors">
                    <div class="w-8 h-8 rounded-lg bg-warning/15 flex items-center justify-center flex-shrink-0 mt-0.5">
                      <i class="fa-solid fa-utensils text-warning text-xs"></i>
                    </div>
                    <div class="flex-1 min-w-0">
                      <p class="text-sm font-medium text-white leading-snug">{{ meal.description }}</p>
                      <p class="text-xs text-gray-500 mt-0.5">
                        P:{{ meal.protein }}g · C:{{ meal.carbs }}g · F:{{ meal.fats }}g
                        <span *ngIf="meal.timeToEat"> · {{ meal.timeToEat | slice:0:5 }}</span>
                      </p>
                    </div>
                    <span class="text-sm font-semibold text-warning flex-shrink-0">{{ meal.calories }} kcal</span>
                  </div>
                  <div class="flex items-center justify-between px-3 pt-1">
                    <span class="text-xs text-gray-500 uppercase tracking-wider">Day total</span>
                    <span class="font-bold text-white text-sm">{{ day.totalCaloriesInDay }} kcal</span>
                  </div>
                </div>
              </ng-container>

              <!-- No diet yet -->
              <div *ngIf="!loadingDiet() && !diet()?.days?.length"
                   class="flex flex-col items-center justify-center py-8 text-center gap-2">
                <i class="fa-solid fa-bowl-food text-gray-600 text-3xl"></i>
                <p class="text-gray-400 text-sm">No diet plan assigned yet.</p>
                <p class="text-gray-600 text-xs">Your coach will set one up for you.</p>
              </div>
            </div>
          </div>

          <!-- Coach card -->
          <div class="card flex items-center gap-4" *ngIf="auth.user()?.coach as coach">
            <div class="w-14 h-14 rounded-full bg-accent/20 flex items-center justify-center flex-shrink-0">
              <i class="fa-solid fa-user-tie text-accent text-xl"></i>
            </div>
            <div class="flex-1">
              <p class="text-xs text-gray-500 uppercase tracking-wider mb-0.5">Your Coach</p>
              <p class="text-white font-semibold text-lg">{{ coach.fullName }}</p>
              <div class="flex items-center gap-1 mt-0.5">
                <i *ngFor="let s of [1,2,3,4,5]" class="fa-solid fa-star text-xs"
                   [class.text-warning]="s <= Math.floor(coach.rating)"
                   [class.text-gray-600]="s > Math.floor(coach.rating)"></i>
                <span class="text-xs text-gray-400 ml-1">{{ coach.rating }}/5</span>
              </div>
            </div>
            <a routerLink="/client/coaches" class="btn-secondary btn-sm">Change coach</a>
          </div>

        </main>
      </div>
    </div>
  `
})
export class ClientDashboardComponent implements OnInit {
  auth = inject(AuthService);
  private api = inject(ClientApiService);
  Math = Math;

  training     = signal<TrainingProgramResponse | null>(null);
  diet         = signal<DietProgramResponse | null>(null);
  loadingTraining = signal(true);
  loadingDiet     = signal(true);

  get greeting(): string {
    const h = new Date().getHours();
    if (h < 12) return 'morning';
    if (h < 18) return 'afternoon';
    return 'evening';
  }

  ngOnInit(): void {
    this.api.getMyTrainingProgram().subscribe({
      next: res => { this.training.set(res); this.loadingTraining.set(false); },
      error: ()  => this.loadingTraining.set(false)
    });

    this.api.getMyDietProgram().subscribe({
      next: res => { this.diet.set(res); this.loadingDiet.set(false); },
      error: ()  => this.loadingDiet.set(false)
    });
  }
}

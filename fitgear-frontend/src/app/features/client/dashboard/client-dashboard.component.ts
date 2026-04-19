import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { AuthService } from '../../../core/services/auth.service';
import { MockDataService } from '../../../core/services/mock-data.service';

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

          <!-- Welcome -->
          <div class="card bg-gradient-to-r from-accent/15 via-card to-card border-accent/20">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-400 text-sm mb-1">Good morning 👋</p>
                <h2 class="text-2xl font-bold text-white">{{ auth.user()?.firstName }} {{ auth.user()?.lastName }}</h2>
                <p class="text-muted mt-1">Here's your progress overview for this month.</p>
              </div>
              <div class="hidden md:block text-6xl opacity-20">💪</div>
            </div>
          </div>

          <!-- Stats row -->
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <app-stat-card label="Current Weight" value="85.5" unit="kg" icon="fa-weight-scale"
              iconColor="#22C55E" iconBg="rgba(34,197,94,0.15)" [change]="-1.2" changeUnit=" kg"></app-stat-card>
            <app-stat-card label="Body Fat" value="18.2" unit="%" icon="fa-percent"
              iconColor="#3B82F6" iconBg="rgba(59,130,246,0.15)" [change]="-0.8" changeUnit="%"></app-stat-card>
            <app-stat-card label="Muscle Mass" value="70.8" unit="kg" icon="fa-fire-flame-curved"
              iconColor="#F59E0B" iconBg="rgba(245,158,11,0.15)" [change]="0.6" changeUnit=" kg"></app-stat-card>
            <app-stat-card label="Workouts Done" value="12" unit="/mo" icon="fa-dumbbell"
              iconColor="#A855F7" iconBg="rgba(168,85,247,0.15)" [change]="3" changeUnit=""></app-stat-card>
          </div>

          <!-- Middle row: Training + Diet -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">

            <!-- Training Program -->
            <div class="card space-y-4">
              <div class="flex items-center justify-between">
                <h3 class="section-title flex items-center gap-2">
                  <i class="fa-solid fa-dumbbell text-accent"></i> Training Program
                </h3>
                <span class="badge badge-green">Active</span>
              </div>
              <div class="space-y-2">
                <div *ngFor="let day of data.trainingProgram.trainingDays"
                     class="flex items-center justify-between py-2.5 px-3 rounded-lg bg-card-2/60 hover:bg-card-2 transition-colors">
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-lg bg-accent/15 flex items-center justify-center">
                      <i class="fa-solid fa-calendar-day text-accent text-xs"></i>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-white">{{ day.dayOfWeek }}</p>
                      <p class="text-xs text-gray-500">{{ day.focus }}</p>
                    </div>
                  </div>
                  <span class="badge badge-green text-xs">{{ day.exercises.length }} ex.</span>
                </div>
              </div>
            </div>

            <!-- Diet Plan -->
            <div class="card space-y-4">
              <div class="flex items-center justify-between">
                <h3 class="section-title flex items-center gap-2">
                  <i class="fa-solid fa-bowl-food text-warning"></i> Diet Plan
                </h3>
                <span class="badge badge-yellow">{{ data.dietProgram.title }}</span>
              </div>
              <div class="space-y-2">
                <div *ngFor="let meal of data.dietProgram.days[0].meals"
                     class="flex items-center justify-between py-2.5 px-3 rounded-lg bg-card-2/60 hover:bg-card-2 transition-colors">
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-lg bg-warning/15 flex items-center justify-center">
                      <i class="fa-solid fa-utensils text-warning text-xs"></i>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-white">{{ meal.name }}</p>
                      <p class="text-xs text-gray-500">{{ meal.time }} · P:{{ meal.protein }}g C:{{ meal.carbs }}g F:{{ meal.fat }}g</p>
                    </div>
                  </div>
                  <span class="text-sm font-semibold text-warning">{{ meal.calories }} kcal</span>
                </div>
              </div>
              <div class="divider"></div>
              <div class="flex items-center justify-between px-3">
                <span class="text-xs text-gray-500 uppercase tracking-wider">Total</span>
                <span class="font-bold text-white">{{ data.dietProgram.days[0].totalCalories }} kcal / day</span>
              </div>
            </div>
          </div>

          <!-- Progress history -->
          <div class="card space-y-4">
            <div class="flex items-center justify-between">
              <h3 class="section-title flex items-center gap-2">
                <i class="fa-solid fa-chart-line text-info"></i> Progress History
              </h3>
              <a routerLink="/client/progress" class="btn-ghost btn-sm">View all</a>
            </div>

            <!-- Visual bar chart -->
            <div class="flex items-end gap-3 h-32 pt-2">
              <div *ngFor="let e of data.progressEntries; let i = index"
                   class="flex-1 flex flex-col items-center gap-1 group">
                <div class="w-full rounded-t-md bg-accent/80 hover:bg-accent transition-all duration-300 relative"
                     [style.height.px]="barHeight(e.weight)"
                     title="{{ e.weight }} kg">
                  <div class="absolute -top-7 left-1/2 -translate-x-1/2 text-xs text-gray-400 opacity-0 group-hover:opacity-100 whitespace-nowrap bg-card border border-white/10 px-1.5 py-0.5 rounded transition-opacity">
                    {{ e.weight }}kg
                  </div>
                </div>
                <span class="text-xs text-gray-500">{{ e.date | date:'MMM' }}</span>
              </div>
            </div>

            <div class="table-wrapper">
              <table class="data-table">
                <thead><tr>
                  <th>Date</th><th>Weight (kg)</th><th>Body Fat (%)</th><th>Muscle (kg)</th>
                </tr></thead>
                <tbody>
                  <tr *ngFor="let e of data.progressEntries.slice().reverse().slice(0,4)">
                    <td>{{ e.date | date:'dd MMM yyyy' }}</td>
                    <td>{{ e.weight }}</td>
                    <td>{{ e.bodyFat }}</td>
                    <td>{{ e.muscleMass ?? '—' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Coach card -->
          <div class="card flex items-center gap-4" *ngIf="data.coaches[0] as coach">
            <div class="w-14 h-14 rounded-full bg-accent/20 flex items-center justify-center flex-shrink-0">
              <i class="fa-solid fa-user-tie text-accent text-xl"></i>
            </div>
            <div class="flex-1">
              <p class="text-xs text-gray-500 uppercase tracking-wider mb-0.5">Your Coach</p>
              <p class="text-white font-semibold text-lg">{{ coach.fullName }}</p>
              <div class="flex items-center gap-1 mt-0.5">
                <i *ngFor="let s of [1,2,3,4,5]" class="fa-solid fa-star text-xs"
                   [class.text-warning]="s <= Math.floor(coach.rating)" [class.text-gray-600]="s > Math.floor(coach.rating)"></i>
                <span class="text-xs text-gray-400 ml-1">{{ coach.rating }}/5</span>
              </div>
            </div>
          </div>

        </main>
      </div>
    </div>
  `
})
export class ClientDashboardComponent {
  auth = inject(AuthService);
  data = inject(MockDataService);
  Math = Math;

  barHeight(w: number): number {
    const min = Math.min(...this.data.progressEntries.map(e => e.weight));
    const max = Math.max(...this.data.progressEntries.map(e => e.weight));
    return max === min ? 60 : ((w - min) / (max - min)) * 90 + 18;
  }
}

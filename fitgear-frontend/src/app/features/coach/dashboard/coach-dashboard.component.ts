import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { AuthService } from '../../../core/services/auth.service';
import { MockDataService } from '../../../core/services/mock-data.service';

@Component({
  selector: 'app-coach-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarComponent, NavbarComponent, StatCardComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>

      <div class="flex-1 flex flex-col min-w-0">
        <app-navbar title="Coach Dashboard">
          <a routerLink="/coach/clients" class="btn-primary btn-sm">
            <i class="fa-solid fa-users"></i> My Clients
          </a>
        </app-navbar>

        <main class="flex-1 p-6 space-y-6 animate-fade-in">

          <!-- Welcome banner -->
          <div class="card bg-gradient-to-r from-accent/15 via-card to-card border-accent/20 relative overflow-hidden">
            <div class="absolute right-4 top-1/2 -translate-y-1/2 text-8xl opacity-10 select-none">🏋️</div>
            <p class="text-gray-400 text-sm mb-1">Welcome back, Coach 👋</p>
            <h2 class="text-2xl font-bold text-white">{{ auth.user()?.firstName }} {{ auth.user()?.lastName }}</h2>
            <p class="text-muted mt-1">You have <span class="text-accent font-semibold">{{ data.clients.length }} active clients</span> this month.</p>
          </div>

          <!-- Stats -->
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <app-stat-card label="Total Clients" [value]="data.clients.length" icon="fa-users"
              iconColor="#22C55E" iconBg="rgba(34,197,94,0.15)" [change]="2" changeUnit=""></app-stat-card>
            <app-stat-card label="Avg. Rating" value="4.8" unit="/5" icon="fa-star"
              iconColor="#F59E0B" iconBg="rgba(245,158,11,0.15)" [change]="0.1" changeUnit=""></app-stat-card>
            <app-stat-card label="Programs Active" value="5" icon="fa-dumbbell"
              iconColor="#3B82F6" iconBg="rgba(59,130,246,0.15)"></app-stat-card>
            <app-stat-card label="Monthly Revenue" value="$875" icon="fa-dollar-sign"
              iconColor="#A855F7" iconBg="rgba(168,85,247,0.15)" [change]="125" changeUnit="$"></app-stat-card>
          </div>

          <!-- Clients overview -->
          <div class="card space-y-4">
            <div class="flex items-center justify-between">
              <h3 class="section-title flex items-center gap-2">
                <i class="fa-solid fa-users text-accent"></i> Client Overview
              </h3>
              <a routerLink="/coach/clients" class="btn-ghost btn-sm">View all</a>
            </div>
            <div class="table-wrapper">
              <table class="data-table">
                <thead><tr>
                  <th>Client</th><th>Height (cm)</th><th>Weight (kg)</th><th>Body Fat (%)</th><th>Action</th>
                </tr></thead>
                <tbody>
                  <tr *ngFor="let c of data.clients.slice(0, 5)">
                    <td>
                      <div class="flex items-center gap-3">
                        <div class="w-8 h-8 rounded-full bg-accent/20 flex items-center justify-center text-accent text-xs font-bold flex-shrink-0">
                          {{ c.firstName[0] }}{{ c.lastName[0] }}
                        </div>
                        <span class="font-medium text-white">{{ c.firstName }} {{ c.lastName }}</span>
                      </div>
                    </td>
                    <td>{{ c.height }}</td>
                    <td>{{ c.weight }}</td>
                    <td>
                      <span class="badge" [class.badge-green]="c.bodyFatPercentage < 18" [class.badge-yellow]="c.bodyFatPercentage >= 18 && c.bodyFatPercentage < 25" [class.badge-red]="c.bodyFatPercentage >= 25">
                        {{ c.bodyFatPercentage }}%
                      </span>
                    </td>
                    <td>
                      <a [routerLink]="['/coach/clients', c.id]" class="btn-secondary btn-sm">
                        <i class="fa-solid fa-arrow-right"></i> Manage
                      </a>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Activity feed -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div class="card space-y-3">
              <h3 class="section-title flex items-center gap-2">
                <i class="fa-solid fa-bolt text-warning"></i> Recent Activity
              </h3>
              <div *ngFor="let a of activity" class="flex items-start gap-3 py-2.5 border-b border-white/5 last:border-0">
                <div class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0"
                     [style.background]="a.bg">
                  <i class="fa-solid text-xs" [class]="a.icon" [style.color]="a.color"></i>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm text-gray-300">{{ a.text }}</p>
                  <p class="text-xs text-gray-600 mt-0.5">{{ a.time }}</p>
                </div>
              </div>
            </div>

            <div class="card space-y-3">
              <h3 class="section-title flex items-center gap-2">
                <i class="fa-solid fa-calendar-check text-info"></i> This Week's Focus
              </h3>
              <div *ngFor="let t of tasks" class="flex items-center gap-3 py-2.5 border-b border-white/5 last:border-0">
                <i class="fa-solid flex-shrink-0 w-5 text-center" [class]="t.done ? 'fa-circle-check text-accent' : 'fa-circle text-gray-600'"></i>
                <span class="text-sm flex-1" [class.line-through]="t.done" [class.text-gray-500]="t.done" [class.text-gray-300]="!t.done">{{ t.text }}</span>
                <span class="badge" [class.badge-green]="t.done" [class.badge-yellow]="!t.done">{{ t.done ? 'Done' : 'Todo' }}</span>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  `
})
export class CoachDashboardComponent {
  auth = inject(AuthService);
  data = inject(MockDataService);

  activity = [
    { text: 'Jordan Smith logged new progress entry', time: '2 hours ago', icon: 'fa-arrow-trend-up', color: '#22C55E', bg: 'rgba(34,197,94,0.15)' },
    { text: "Taylor Brown completed today's workout", time: '4 hours ago', icon: 'fa-dumbbell', color: '#3B82F6', bg: 'rgba(59,130,246,0.15)' },
    { text: 'Morgan Davis sent you a message', time: '6 hours ago', icon: 'fa-comment', color: '#F59E0B', bg: 'rgba(245,158,11,0.15)' },
    { text: "Casey Wilson missed yesterday's training", time: '1 day ago', icon: 'fa-triangle-exclamation', color: '#EF4444', bg: 'rgba(239,68,68,0.15)' },
  ];

  tasks = [
    { text: 'Update Jordan\'s training program', done: true },
    { text: 'Review Taylor\'s diet plan', done: true },
    { text: 'Create program for new client Riley', done: false },
    { text: 'Reply to Morgan\'s message', done: false },
    { text: 'Schedule monthly check-ins', done: false },
  ];
}

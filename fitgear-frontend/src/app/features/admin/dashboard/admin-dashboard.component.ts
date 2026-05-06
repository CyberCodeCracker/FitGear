import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { AuthService } from '../../../core/services/auth.service';
import { AdminApiService } from '../../../core/services/admin-api.service';
import { AdminDashboardResponse } from '../../../core/models/models';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarComponent, NavbarComponent, StatCardComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>
      <div class="flex-1 flex flex-col min-w-0">

        <app-navbar title="Admin Dashboard"></app-navbar>

        <main class="flex-1 p-6 space-y-6 animate-fade-in">

          <!-- Welcome banner -->
          <div class="card bg-gradient-to-r from-violet-500/15 via-card to-card border-violet-500/20 relative overflow-hidden">
            <div class="absolute right-4 top-1/2 -translate-y-1/2 text-8xl opacity-10 select-none">🛡️</div>
            <p class="text-gray-400 text-sm mb-1">Admin Panel 👋</p>
            <h2 class="text-2xl font-bold text-white">
              {{ auth.user()?.firstName }} {{ auth.user()?.lastName }}
            </h2>
            <p class="text-muted mt-1">Platform overview and user management.</p>
          </div>

          <!-- Stats grid -->
          <ng-container *ngIf="!loading(); else skeletonTpl">
            <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
              <app-stat-card
                label="Total Clients"
                [value]="stats()?.totalClients ?? 0"
                icon="fa-users"
                iconColor="#22C55E"
                iconBg="rgba(34,197,94,0.15)">
              </app-stat-card>
              <app-stat-card
                label="Total Coaches"
                [value]="stats()?.totalCoaches ?? 0"
                icon="fa-person-running"
                iconColor="#3B82F6"
                iconBg="rgba(59,130,246,0.15)">
              </app-stat-card>
              <app-stat-card
                label="Clients with Coach"
                [value]="stats()?.clientsWithCoach ?? 0"
                icon="fa-handshake"
                iconColor="#F59E0B"
                iconBg="rgba(245,158,11,0.15)">
              </app-stat-card>
              <app-stat-card
                label="Training Programs"
                [value]="stats()?.totalTrainingPrograms ?? 0"
                icon="fa-dumbbell"
                iconColor="#A855F7"
                iconBg="rgba(168,85,247,0.15)">
              </app-stat-card>
            </div>

            <div class="grid grid-cols-2 lg:grid-cols-3 gap-4">
              <app-stat-card
                label="Diet Programs"
                [value]="stats()?.totalDietPrograms ?? 0"
                icon="fa-bowl-food"
                iconColor="#EF4444"
                iconBg="rgba(239,68,68,0.15)">
              </app-stat-card>
              <app-stat-card
                label="Clients with Training"
                [value]="stats()?.clientsWithTrainingProgram ?? 0"
                icon="fa-calendar-check"
                iconColor="#22C55E"
                iconBg="rgba(34,197,94,0.15)">
              </app-stat-card>
              <app-stat-card
                label="Clients with Diet"
                [value]="stats()?.clientsWithDietProgram ?? 0"
                icon="fa-utensils"
                iconColor="#F59E0B"
                iconBg="rgba(245,158,11,0.15)">
              </app-stat-card>
            </div>
          </ng-container>

          <ng-template #skeletonTpl>
            <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
              <div *ngFor="let s of [1,2,3,4]" class="h-24 bg-card rounded-xl animate-pulse"></div>
            </div>
            <div class="grid grid-cols-2 lg:grid-cols-3 gap-4">
              <div *ngFor="let s of [1,2,3]" class="h-24 bg-card rounded-xl animate-pulse"></div>
            </div>
          </ng-template>

          <!-- Quick action -->
          <div class="card flex items-center justify-between">
            <div>
              <h3 class="section-title flex items-center gap-2">
                <i class="fa-solid fa-users-gear text-violet-400"></i> User Management
              </h3>
              <p class="text-muted text-sm mt-1">View, enable, lock or delete platform users.</p>
            </div>
            <a routerLink="/admin/users" class="btn-primary btn-sm">
              <i class="fa-solid fa-arrow-right"></i> Manage Users
            </a>
          </div>

        </main>
      </div>
    </div>
  `
})
export class AdminDashboardComponent implements OnInit {
  auth    = inject(AuthService);
  private api = inject(AdminApiService);

  loading = signal(true);
  stats   = signal<AdminDashboardResponse | null>(null);

  ngOnInit(): void {
    this.api.getDashboard().subscribe({
      next: data => { this.stats.set(data); this.loading.set(false); },
      error: ()   => this.loading.set(false)
    });
  }
}

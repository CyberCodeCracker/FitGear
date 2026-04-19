import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { AuthService } from '../../../core/services/auth.service';
import { CoachApiService } from '../../../core/services/coach-api.service';
import { ClientResponse } from '../../../core/models/models';

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
            <p class="text-muted mt-1">
              You have
              <span class="text-accent font-semibold">{{ totalClients() }} active client{{ totalClients() !== 1 ? 's' : '' }}</span>
              this month.
            </p>
          </div>

          <!-- Stats -->
          <div class="grid grid-cols-2 lg:grid-cols-3 gap-4">
            <app-stat-card label="Total Clients" [value]="totalClients()" icon="fa-users"
              iconColor="#22C55E" iconBg="rgba(34,197,94,0.15)"></app-stat-card>
            <app-stat-card label="Avg. Rating" [value]="auth.user()?.rating ?? 0" unit="/5" icon="fa-star"
              iconColor="#F59E0B" iconBg="rgba(245,158,11,0.15)"></app-stat-card>
            <app-stat-card label="Monthly Revenue"
              [value]="'$' + ((auth.user()?.monthlyRate ?? 0) * totalClients())"
              icon="fa-dollar-sign"
              iconColor="#A855F7" iconBg="rgba(168,85,247,0.15)"></app-stat-card>
          </div>

          <!-- Client overview table -->
          <div class="card space-y-4">
            <div class="flex items-center justify-between">
              <h3 class="section-title flex items-center gap-2">
                <i class="fa-solid fa-users text-accent"></i> Client Overview
              </h3>
              <a routerLink="/coach/clients" class="btn-ghost btn-sm">View all</a>
            </div>

            <!-- Skeleton -->
            <div *ngIf="loading()" class="space-y-2">
              <div *ngFor="let s of [1,2,3,4,5]" class="h-14 bg-card-2 rounded-lg animate-pulse"></div>
            </div>

            <div *ngIf="!loading()" class="table-wrapper">
              <table class="data-table">
                <thead><tr>
                  <th>Client</th><th>Weight (kg)</th><th>Body Fat (%)</th><th>Action</th>
                </tr></thead>
                <tbody>
                  <tr *ngFor="let c of recentClients()">
                    <td>
                      <div class="flex items-center gap-3">
                        <div class="w-8 h-8 rounded-full bg-accent/20 flex items-center justify-center text-accent text-xs font-bold flex-shrink-0">
                          {{ c.firstName[0] }}{{ c.lastName[0] }}
                        </div>
                        <span class="font-medium text-white">{{ c.firstName }} {{ c.lastName }}</span>
                      </div>
                    </td>
                    <td>{{ c.weight }}</td>
                    <td>
                      <span class="badge"
                            [class.badge-green]="c.bodyFatPercentage < 18"
                            [class.badge-yellow]="c.bodyFatPercentage >= 18 && c.bodyFatPercentage < 25"
                            [class.badge-red]="c.bodyFatPercentage >= 25">
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
              <p *ngIf="recentClients().length === 0" class="text-center text-muted py-8">
                <i class="fa-solid fa-users text-2xl block mb-2"></i>No clients yet.
              </p>
            </div>
          </div>

        </main>
      </div>
    </div>
  `
})
export class CoachDashboardComponent implements OnInit {
  auth = inject(AuthService);
  private api = inject(CoachApiService);

  loading       = signal(true);
  recentClients = signal<ClientResponse[]>([]);
  totalClients  = signal(0);

  ngOnInit(): void {
    this.api.getClients(0, 5).subscribe({
      next: res => {
        this.recentClients.set(res.content);
        this.totalClients.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}

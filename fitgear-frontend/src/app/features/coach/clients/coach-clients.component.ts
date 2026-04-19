import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, startWith } from 'rxjs/operators';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { CoachApiService } from '../../../core/services/coach-api.service';
import { ClientResponse } from '../../../core/models/models';
@Component({
  selector: 'app-coach-clients',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SidebarComponent, NavbarComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>
      <div class="flex-1 flex flex-col min-w-0">

        <app-navbar title="My Clients">
          <div class="relative">
            <i class="fa-solid fa-magnifying-glass absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-xs"></i>
            <input [(ngModel)]="query" (ngModelChange)="search$.next($event)" type="text"
                   placeholder="Search clients…" class="form-input pl-8 py-1.5 text-sm w-48">
          </div>
          <div class="flex gap-1 border border-white/10 rounded-lg p-0.5">
            <button (click)="view.set('grid')" class="btn-sm rounded"
                    [class.bg-accent]="view()==='grid'" [class.text-black]="view()==='grid'"
                    [class.text-gray-400]="view()!=='grid'">
              <i class="fa-solid fa-grip"></i>
            </button>
            <button (click)="view.set('table')" class="btn-sm rounded"
                    [class.bg-accent]="view()==='table'" [class.text-black]="view()==='table'"
                    [class.text-gray-400]="view()!=='table'">
              <i class="fa-solid fa-list"></i>
            </button>
          </div>
        </app-navbar>

        <main class="flex-1 p-6 space-y-6 animate-fade-in">
          <div class="flex items-center justify-between">
            <div>
              <h2 class="page-title">Client List</h2>
              <p class="text-muted">Manage and monitor all your clients.</p>
            </div>
            <span class="badge badge-green">{{ total() }} clients</span>
          </div>

          <!-- Skeleton -->
          <div *ngIf="loading()" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
            <div *ngFor="let s of [1,2,3,4,5,6]" class="card animate-pulse space-y-4">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-full bg-card-2"></div>
                <div class="space-y-2 flex-1">
                  <div class="h-4 bg-card-2 rounded w-1/2"></div>
                  <div class="h-3 bg-card-2 rounded w-1/3"></div>
                </div>
              </div>
              <div class="grid grid-cols-3 gap-2">
                <div class="h-14 bg-card-2 rounded-lg"></div>
                <div class="h-14 bg-card-2 rounded-lg"></div>
                <div class="h-14 bg-card-2 rounded-lg"></div>
              </div>
              <div class="h-8 bg-card-2 rounded-lg"></div>
            </div>
          </div>

          <!-- Grid view -->
          <div *ngIf="!loading() && view()==='grid'" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
            <div *ngFor="let c of clients()"
                 class="card-hover flex flex-col gap-4 animate-slide-up cursor-pointer"
                 (click)="goToClient(c.id)">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-full bg-gradient-to-br from-accent/30 to-accent/10 flex items-center justify-center flex-shrink-0">
                  <span class="text-accent font-bold">{{ c.firstName[0] }}{{ c.lastName[0] }}</span>
                </div>
                <div>
                  <h3 class="font-semibold text-white">{{ c.firstName }} {{ c.lastName }}</h3>
                  <p class="text-xs text-gray-500">Client #{{ c.id }}</p>
                </div>
              </div>
              <div class="grid grid-cols-3 gap-2">
                <div class="bg-card-2/60 rounded-lg p-2.5 text-center">
                  <p class="text-xs text-gray-500 mb-0.5">Height</p>
                  <p class="text-sm font-semibold text-white">{{ c.height }}<span class="text-xs text-gray-500"> cm</span></p>
                </div>
                <div class="bg-card-2/60 rounded-lg p-2.5 text-center">
                  <p class="text-xs text-gray-500 mb-0.5">Weight</p>
                  <p class="text-sm font-semibold text-white">{{ c.weight }}<span class="text-xs text-gray-500"> kg</span></p>
                </div>
                <div class="bg-card-2/60 rounded-lg p-2.5 text-center">
                  <p class="text-xs text-gray-500 mb-0.5">Body Fat</p>
                  <p class="text-sm font-semibold"
                     [class.text-accent]="c.bodyFatPercentage < 18"
                     [class.text-warning]="c.bodyFatPercentage >= 18 && c.bodyFatPercentage < 25"
                     [class.text-danger]="c.bodyFatPercentage >= 25">
                    {{ c.bodyFatPercentage }}<span class="text-xs text-gray-500">%</span>
                  </p>
                </div>
              </div>
              <div class="w-full bg-card-2 rounded-full h-1.5">
                <div class="h-1.5 rounded-full transition-all duration-500"
                     [style.width.%]="c.bodyFatPercentage"
                     [class.bg-accent]="c.bodyFatPercentage < 18"
                     [class.bg-warning]="c.bodyFatPercentage >= 18 && c.bodyFatPercentage < 25"
                     [class.bg-danger]="c.bodyFatPercentage >= 25"></div>
              </div>
              <a [routerLink]="['/coach/clients', c.id]" (click)="$event.stopPropagation()"
                 class="btn-primary justify-center text-sm">
                <i class="fa-solid fa-pen-to-square"></i> Manage
              </a>
            </div>
          </div>

          <!-- Table view -->
          <div *ngIf="!loading() && view()==='table'" class="table-wrapper animate-fade-in">
            <table class="data-table">
              <thead><tr>
                <th>Client</th><th>Height (cm)</th><th>Weight (kg)</th><th>Body Fat (%)</th><th>Actions</th>
              </tr></thead>
              <tbody>
                <tr *ngFor="let c of clients()" class="cursor-pointer" (click)="goToClient(c.id)">
                  <td>
                    <div class="flex items-center gap-3">
                      <div class="w-8 h-8 rounded-full bg-accent/20 flex items-center justify-center text-accent text-xs font-bold">
                        {{ c.firstName[0] }}{{ c.lastName[0] }}
                      </div>
                      <div>
                        <p class="font-medium text-white">{{ c.firstName }} {{ c.lastName }}</p>
                        <p class="text-xs text-gray-500">ID #{{ c.id }}</p>
                      </div>
                    </div>
                  </td>
                  <td>{{ c.height }}</td>
                  <td class="font-semibold text-white">{{ c.weight }}</td>
                  <td>
                    <span class="badge"
                          [class.badge-green]="c.bodyFatPercentage < 18"
                          [class.badge-yellow]="c.bodyFatPercentage >= 18 && c.bodyFatPercentage < 25"
                          [class.badge-red]="c.bodyFatPercentage >= 25">
                      {{ c.bodyFatPercentage }}%
                    </span>
                  </td>
                  <td (click)="$event.stopPropagation()">
                    <a [routerLink]="['/coach/clients', c.id]" class="btn-secondary btn-sm">
                      <i class="fa-solid fa-pen-to-square"></i> Manage
                    </a>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Empty -->
          <p *ngIf="!loading() && clients().length === 0" class="text-center text-muted py-16">
            <i class="fa-solid fa-users text-3xl mb-3 block"></i>
            {{ query ? 'No clients match "' + query + '".' : 'No clients yet.' }}
          </p>

          <!-- Pagination -->
          <div *ngIf="!loading() && totalPages() > 1" class="flex items-center justify-center gap-2">
            <button (click)="loadPage(currentPage() - 1)" [disabled]="currentPage() === 0" class="btn-secondary btn-sm">
              <i class="fa-solid fa-chevron-left"></i>
            </button>
            <span class="text-sm text-gray-400">Page {{ currentPage() + 1 }} / {{ totalPages() }}</span>
            <button (click)="loadPage(currentPage() + 1)" [disabled]="currentPage() === totalPages() - 1" class="btn-secondary btn-sm">
              <i class="fa-solid fa-chevron-right"></i>
            </button>
          </div>

        </main>
      </div>
    </div>
  `
})
export class CoachClientsComponent implements OnInit {
  private api    = inject(CoachApiService);
  private router = inject(Router);

  clients     = signal<ClientResponse[]>([]);
  loading     = signal(true);
  view        = signal<'grid' | 'table'>('grid');
  total       = signal(0);
  totalPages  = signal(1);
  currentPage = signal(0);
  query       = '';
  search$     = new Subject<string>();

  ngOnInit(): void {
    this.search$.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(q => {
        this.loading.set(true);
        return q.trim()
          ? this.api.searchClients(q.trim(), 0, 10)
          : this.api.getClients(0, 10);
      })
    ).subscribe(res => {
      this.clients.set(res.content);
      this.total.set(res.totalElements);
      this.totalPages.set(res.totalPages);
      this.currentPage.set(res.number);
      this.loading.set(false);
    });
  }

  loadPage(page: number): void {
    this.loading.set(true);
    const obs = this.query.trim()
      ? this.api.searchClients(this.query.trim(), page, 10)
      : this.api.getClients(page, 10);
    obs.subscribe(res => {
      this.clients.set(res.content);
      this.total.set(res.totalElements);
      this.totalPages.set(res.totalPages);
      this.currentPage.set(res.number);
      this.loading.set(false);
    });
  }

  goToClient(id: number): void { this.router.navigate(['/coach/clients', id]); }
}

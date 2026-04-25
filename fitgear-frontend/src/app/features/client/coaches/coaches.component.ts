import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject, switchMap, startWith } from 'rxjs';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { ClientApiService } from '../../../core/services/client-api.service';
import { AuthService } from '../../../core/services/auth.service';
import { CoachCard } from '../../../core/models/models';

@Component({
  selector: 'app-coaches',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarComponent, NavbarComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>

      <div class="flex-1 flex flex-col min-w-0">
        <app-navbar title="Discover Coaches">
          <div class="relative">
            <i class="fa-solid fa-magnifying-glass absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-xs"></i>
            <input type="text" [(ngModel)]="query" placeholder="Search coaches…"
                   class="form-input pl-8 py-1.5 text-sm w-52"
                   (ngModelChange)="onSearch($event)">
          </div>
        </app-navbar>

        <main class="flex-1 p-6 space-y-6 animate-fade-in">

          <!-- No-coach banner -->
          <div *ngIf="!currentCoachId()" class="card bg-info/10 border-info/20 flex items-center gap-4">
            <div class="w-10 h-10 rounded-xl bg-info/20 flex items-center justify-center flex-shrink-0">
              <i class="fa-solid fa-circle-info text-info"></i>
            </div>
            <div>
              <p class="text-white font-medium">You don't have a coach yet</p>
              <p class="text-muted text-sm">Subscribe to a coach below to unlock your dashboard and progress tracking.</p>
            </div>
          </div>

          <!-- Current coach banner -->
          <div *ngIf="currentCoachId()" class="card bg-accent/10 border-accent/20 flex items-center gap-4">
            <div class="w-10 h-10 rounded-xl bg-accent/20 flex items-center justify-center flex-shrink-0">
              <i class="fa-solid fa-circle-check text-accent"></i>
            </div>
            <div class="flex-1">
              <p class="text-white font-medium">You are subscribed to <span class="text-accent">{{ currentCoachName() }}</span></p>
              <p class="text-muted text-sm">Your dashboard and progress tracking are now active.</p>
            </div>
            <button (click)="unsubscribe()" class="btn-danger btn-sm" [disabled]="subscribing()">
              <i class="fa-solid fa-xmark"></i> Unsubscribe
            </button>
          </div>

          <div class="flex items-center justify-between">
            <div>
              <h2 class="page-title">Available Coaches</h2>
              <p class="text-muted">Find the perfect coach for your fitness goals.</p>
            </div>
            <span class="badge badge-green">{{ total() }} coaches</span>
          </div>

          <!-- Skeleton loading -->
          <div *ngIf="loading()" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
            <div *ngFor="let s of [1,2,3,4,5,6]" class="card animate-pulse space-y-4">
              <div class="flex items-start gap-4">
                <div class="w-14 h-14 rounded-xl bg-card-2"></div>
                <div class="flex-1 space-y-2">
                  <div class="h-4 bg-card-2 rounded w-2/3"></div>
                  <div class="h-3 bg-card-2 rounded w-1/3"></div>
                </div>
              </div>
              <div class="h-3 bg-card-2 rounded"></div>
              <div class="h-3 bg-card-2 rounded w-4/5"></div>
              <div class="h-8 bg-card-2 rounded-lg mt-2"></div>
            </div>
          </div>

          <!-- Coach cards -->
          <div *ngIf="!loading()" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
            <div *ngFor="let coach of coaches()" class="card-hover group flex flex-col gap-4 animate-slide-up">
              <div class="flex items-start gap-4">
                <img *ngIf="coachPicUrl(coach.profilePicture)"
                     [src]="coachPicUrl(coach.profilePicture)"
                     alt="" class="w-14 h-14 rounded-xl object-cover flex-shrink-0">
                <div *ngIf="!coachPicUrl(coach.profilePicture)"
                     class="w-14 h-14 rounded-xl bg-gradient-to-br from-accent/30 to-accent/10 flex items-center justify-center flex-shrink-0">
                  <i class="fa-solid fa-user-tie text-accent text-xl"></i>
                </div>
                <div class="flex-1 min-w-0">
                  <h3 class="font-semibold text-white text-base truncate">{{ coach.fullName }}</h3>
                  <div class="flex items-center gap-1 mt-0.5">
                    <i *ngFor="let s of [1,2,3,4,5]" class="fa-solid fa-star text-xs"
                       [class.text-warning]="s <= Math.floor(coach.rating)"
                       [class.text-gray-600]="s > Math.floor(coach.rating)"></i>
                    <span class="text-xs text-gray-400 ml-1">{{ coach.rating }} ({{ coach.reviewCount }})</span>
                  </div>
                  <p class="text-xs text-gray-500 mt-0.5">
                    <i class="fa-solid fa-briefcase mr-1"></i>{{ coach.yearsOfExperience }} yrs exp.
                  </p>
                </div>
                <span class="badge badge-green flex-shrink-0">{{ '$' + coach.monthlyRate }}<span class="text-gray-400">/mo</span></span>
              </div>

              <p class="text-sm text-gray-400 leading-relaxed flex-1">{{ coach.description }}</p>
              <div class="divider my-0"></div>

              <div class="flex items-center gap-2">
                <a [routerLink]="['/client/coaches', coach.id]" class="btn-secondary flex-1 justify-center text-sm">
                  <i class="fa-solid fa-eye"></i> Details
                </a>
                <button (click)="subscribe(coach); $event.stopPropagation()"
                        class="flex-1 justify-center text-sm"
                        [class]="currentCoachId() === coach.id ? 'btn btn-secondary cursor-default' : 'btn-primary btn'"
                        [disabled]="subscribing() || currentCoachId() === coach.id">
                  <i class="fa-solid fa-spinner fa-spin" *ngIf="subscribing() && pendingId() === coach.id"></i>
                  <i class="fa-solid fa-check" *ngIf="currentCoachId() === coach.id && pendingId() !== coach.id"></i>
                  <i class="fa-solid fa-plus" *ngIf="currentCoachId() !== coach.id && pendingId() !== coach.id"></i>
                  {{ currentCoachId() === coach.id ? 'Your Coach' : 'Subscribe' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Empty -->
          <p *ngIf="!loading() && coaches().length === 0" class="text-center text-muted py-16">
            <i class="fa-solid fa-magnifying-glass text-3xl mb-3 block"></i>
            No coaches found{{ query ? ' for "' + query + '"' : '' }}.
          </p>

          <!-- Pagination -->
          <div *ngIf="!loading() && totalPages() > 1" class="flex items-center justify-center gap-2">
            <button (click)="loadPage(page() - 1)" [disabled]="page() === 0" class="btn-secondary btn-sm">
              <i class="fa-solid fa-chevron-left"></i>
            </button>
            <span class="text-sm text-gray-400">Page {{ page() + 1 }} / {{ totalPages() }}</span>
            <button (click)="loadPage(page() + 1)" [disabled]="page() === totalPages() - 1" class="btn-secondary btn-sm">
              <i class="fa-solid fa-chevron-right"></i>
            </button>
          </div>

        </main>
      </div>
    </div>
  `
})
export class CoachesComponent implements OnInit {
  private api  = inject(ClientApiService);
  private auth = inject(AuthService);
  Math = Math;

  coaches      = signal<CoachCard[]>([]);
  loading      = signal(true);
  subscribing  = signal(false);
  pendingId    = signal<number | null>(null);
  total        = signal(0);
  totalPages   = signal(1);
  page         = signal(0);
  currentCoachId   = signal<number | null>(this.auth.user()?.coach?.id ?? null);
  currentCoachName = signal<string>( this.auth.user()?.coach?.fullName ?? '');

  query = '';
  private search$ = new Subject<string>();

  private readonly API_BASE = 'http://localhost:8080/api/v1';
  coachPicUrl(filename?: string): string | null {
    return filename ? `${this.API_BASE}/uploads/${filename}` : null;
  }

  ngOnInit(): void {
    this.search$.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(q => { this.loading.set(true); return this.api.getCoaches(0, 12, q); })
    ).subscribe(res => {
      this.coaches.set(res.content);
      this.total.set(res.totalElements);
      this.totalPages.set(res.totalPages);
      this.page.set(res.number);
      this.loading.set(false);
    });
  }

  onSearch(q: string): void { this.search$.next(q); }

  loadPage(p: number): void {
    this.loading.set(true);
    this.api.getCoaches(p, 12, this.query).subscribe(res => {
      this.coaches.set(res.content);
      this.total.set(res.totalElements);
      this.totalPages.set(res.totalPages);
      this.page.set(res.number);
      this.loading.set(false);
    });
  }

  subscribe(coach: CoachCard): void {
    if (this.subscribing() || this.currentCoachId() === coach.id) return;
    this.subscribing.set(true);
    this.pendingId.set(coach.id);
    this.api.subscribeToCoach(coach.id).subscribe({
      next: res => {
        this.currentCoachId.set(res.id);
        this.currentCoachName.set(res.fullName);
        // Patch the in-memory user so the guard sees the coach immediately
        const u = this.auth.user();
        if (u) {
          const updated: any = { ...u, coach: { id: res.id, fullName: res.fullName, monthlyRate: res.monthlyRate, rating: res.rating } };
          localStorage.setItem('fg_user', JSON.stringify(updated));
          this.auth['_user'].set(updated);
        }
        this.subscribing.set(false);
        this.pendingId.set(null);
      },
      error: () => { this.subscribing.set(false); this.pendingId.set(null); }
    });
  }

  unsubscribe(): void {
    this.subscribing.set(true);
    this.api.unsubscribeCoach().subscribe({
      next: () => {
        this.currentCoachId.set(null);
        this.currentCoachName.set('');
        const u = this.auth.user();
        if (u) {
          const updated: any = { ...u, coach: null };
          localStorage.setItem('fg_user', JSON.stringify(updated));
          this.auth['_user'].set(updated);
        }
        this.subscribing.set(false);
      },
      error: () => this.subscribing.set(false)
    });
  }
}

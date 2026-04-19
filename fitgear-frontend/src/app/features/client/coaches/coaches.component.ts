import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { MockDataService } from '../../../core/services/mock-data.service';
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
                   class="form-input pl-8 py-1.5 text-sm w-52" (ngModelChange)="filter()">
          </div>
        </app-navbar>

        <main class="flex-1 p-6 space-y-6 animate-fade-in">
          <div class="flex items-center justify-between">
            <div>
              <h2 class="page-title">Available Coaches</h2>
              <p class="text-muted">Find the perfect coach for your fitness goals.</p>
            </div>
            <span class="badge badge-green">{{ filtered().length }} coaches</span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
            <div *ngFor="let coach of filtered()" class="card-hover group flex flex-col gap-4 animate-slide-up">

              <!-- Header -->
              <div class="flex items-start gap-4">
                <div class="w-14 h-14 rounded-xl bg-gradient-to-br from-accent/30 to-accent/10 flex items-center justify-center flex-shrink-0">
                  <i class="fa-solid fa-user-tie text-accent text-xl"></i>
                </div>
                <div class="flex-1 min-w-0">
                  <h3 class="font-semibold text-white text-base truncate">{{ coach.fullName }}</h3>
                  <div class="flex items-center gap-1 mt-0.5">
                    <i *ngFor="let s of [1,2,3,4,5]" class="fa-solid fa-star text-xs"
                       [class.text-warning]="s <= Math.floor(coach.rating)"
                       [class.text-gray-600]="s > Math.floor(coach.rating)"></i>
                    <span class="text-xs text-gray-400 ml-1">{{ coach.rating }}</span>
                  </div>
                  <p class="text-xs text-gray-500 mt-0.5"><i class="fa-solid fa-briefcase mr-1"></i>{{ coach.yearsOfExperience }} yrs exp.</p>
                </div>
                <span class="badge badge-green flex-shrink-0">
                  {{ '$' + coach.monthlyRate }}<span class="text-gray-400">/mo</span>
                </span>
              </div>

              <p class="text-sm text-gray-400 leading-relaxed flex-1">{{ coach.description }}</p>

              <div class="divider my-0"></div>

              <div class="flex items-center gap-2">
                <button (click)="subscribe(coach)"
                        class="btn-primary flex-1 justify-center text-sm"
                        [class.btn-secondary]="subscribedId() === coach.id"
                        [disabled]="subscribedId() === coach.id">
                  <i class="fa-solid" [class.fa-plus]="subscribedId() !== coach.id" [class.fa-check]="subscribedId() === coach.id"></i>
                  {{ subscribedId() === coach.id ? 'Subscribed' : 'Subscribe' }}
                </button>
                <button class="btn-icon border border-white/10 hover:border-accent/40 text-gray-400 hover:text-accent">
                  <i class="fa-solid fa-ellipsis"></i>
                </button>
              </div>
            </div>
          </div>

          <p *ngIf="filtered().length === 0" class="text-center text-muted py-16">
            <i class="fa-solid fa-magnifying-glass text-3xl mb-3 block"></i>
            No coaches match your search.
          </p>
        </main>
      </div>
    </div>
  `
})
export class CoachesComponent {
  data = inject(MockDataService);
  Math = Math;
  query = '';
  subscribedId = signal<number | null>(null);
  filtered = signal<CoachCard[]>([...this.data.coaches]);

  // Filter coaches by query
  filter() {
    const q = this.query.toLowerCase();
    this.filtered.set(!q ? [...this.data.coaches]
      : this.data.coaches.filter(c =>
          c.fullName.toLowerCase().includes(q) ||
          c.description.toLowerCase().includes(q)));
  }

  subscribe(coach: CoachCard) {
    this.subscribedId.set(coach.id);
  }
}

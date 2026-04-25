import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { ClientApiService } from '../../../core/services/client-api.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { CoachDetailResponse, TestimonialResponse } from '../../../core/models/models';

@Component({
  selector: 'app-coach-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, SidebarComponent, NavbarComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>
      <div class="flex-1 flex flex-col min-w-0">

        <app-navbar title="Coach Profile"
          [breadcrumbs]="[
            {label:'Dashboard', route:'/client/dashboard'},
            {label:'Coaches', route:'/client/coaches'},
            {label: coach()?.fullName ?? '…'}
          ]">
        </app-navbar>

        <!-- Loading -->
        <div *ngIf="loading()" class="flex-1 flex items-center justify-center">
          <i class="fa-solid fa-spinner fa-spin text-accent text-3xl"></i>
        </div>

        <main *ngIf="!loading() && coach() as c" class="flex-1 p-6 space-y-6 animate-fade-in">

          <!-- ── Coach Header ──────────────────────────────────── -->
          <div class="card bg-gradient-to-r from-accent/15 via-card to-card border-accent/20">
            <div class="flex flex-col sm:flex-row items-start sm:items-center gap-5">
              <img *ngIf="coachPicUrl(c.profilePicture)"
                   [src]="coachPicUrl(c.profilePicture)"
                   alt="" class="w-20 h-20 rounded-full object-cover border-2 border-accent/30 flex-shrink-0">
              <div *ngIf="!coachPicUrl(c.profilePicture)"
                   class="w-20 h-20 rounded-full bg-gradient-to-br from-accent/30 to-accent/10
                          flex items-center justify-center text-accent text-3xl font-bold flex-shrink-0">
                {{ c.fullName.split(' ')[0][0] }}{{ c.fullName.split(' ')[1]?.[0] ?? '' }}
              </div>
              <div class="flex-1">
                <h2 class="text-2xl font-bold text-white">{{ c.fullName }}</h2>
                <div class="flex items-center gap-2 mt-1">
                  <div class="flex items-center gap-0.5">
                    <i *ngFor="let s of [1,2,3,4,5]" class="fa-solid fa-star text-sm"
                       [class.text-warning]="s <= Math.round(c.rating)"
                       [class.text-gray-600]="s > Math.round(c.rating)"></i>
                  </div>
                  <span class="text-sm text-gray-400">{{ c.rating }}/5</span>
                  <span class="text-sm text-gray-500">({{ c.reviewCount }} reviews)</span>
                </div>
                <p class="text-muted mt-2 leading-relaxed">{{ c.description }}</p>
              </div>
            </div>
          </div>

          <!-- ── Stats row ──────────────────────────────────────── -->
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <div class="card text-center py-4">
              <p class="text-xs text-gray-500 uppercase tracking-wider mb-1">Experience</p>
              <p class="text-2xl font-bold text-white">{{ c.yearsOfExperience }}<span class="text-sm text-gray-400"> yrs</span></p>
            </div>
            <div class="card text-center py-4">
              <p class="text-xs text-gray-500 uppercase tracking-wider mb-1">Monthly Rate</p>
              <p class="text-2xl font-bold text-accent">{{ '$' + c.monthlyRate }}</p>
            </div>
            <div class="card text-center py-4">
              <p class="text-xs text-gray-500 uppercase tracking-wider mb-1">Rating</p>
              <p class="text-2xl font-bold text-warning">{{ c.rating }}<span class="text-sm text-gray-400">/5</span></p>
            </div>
            <div class="card text-center py-4">
              <p class="text-xs text-gray-500 uppercase tracking-wider mb-1">Status</p>
              <span class="badge" [class.badge-green]="c.available" [class.badge-red]="!c.available">
                {{ c.available ? 'Available' : 'Unavailable' }}
              </span>
            </div>
          </div>

          <!-- ── Subscribe / Actions ────────────────────────────── -->
          <div class="card flex items-center justify-between gap-4">
            <div class="flex items-center gap-3">
              <i class="fa-solid text-xl"
                 [class.fa-circle-check]="isMyCoach()" [class.text-accent]="isMyCoach()"
                 [class.fa-circle-plus]="!isMyCoach()" [class.text-gray-400]="!isMyCoach()"></i>
              <div>
                <p class="font-medium text-white">
                  {{ isMyCoach() ? 'This is your current coach' : 'Want to train with ' + c.fullName + '?' }}
                </p>
                <p class="text-xs text-gray-500">
                  {{ isMyCoach() ? 'You can leave a review below.' : 'Subscribe to get started.' }}
                </p>
              </div>
            </div>
            <button *ngIf="!isMyCoach()" (click)="subscribeToCoach()" class="btn-primary"
                    [disabled]="subscribing()">
              <i class="fa-solid fa-spinner fa-spin" *ngIf="subscribing()"></i>
              <i class="fa-solid fa-plus" *ngIf="!subscribing()"></i> Subscribe
            </button>
          </div>

          <!-- ── Write Review (only if subscribed) ──────────────── -->
          <div *ngIf="isMyCoach()" class="card space-y-4 border-warning/20 animate-slide-up">
            <h3 class="section-title flex items-center gap-2">
              <i class="fa-solid fa-pen text-warning"></i> Your Review
            </h3>
            <form [formGroup]="reviewForm" (ngSubmit)="submitReview()" class="space-y-4">
              <div class="form-group">
                <label class="form-label">Rating</label>
                <div class="flex items-center gap-1">
                  <button *ngFor="let s of [1,2,3,4,5]" type="button" (click)="setRating(s)"
                          class="text-2xl transition-colors duration-150 hover:scale-110 active:scale-95"
                          [class.text-warning]="s <= (hoverRating() || reviewForm.get('rating')?.value || 0)"
                          [class.text-gray-600]="s > (hoverRating() || reviewForm.get('rating')?.value || 0)"
                          (mouseenter)="hoverRating.set(s)" (mouseleave)="hoverRating.set(0)">
                    <i class="fa-solid fa-star"></i>
                  </button>
                  <span class="text-sm text-gray-400 ml-2">{{ reviewForm.get('rating')?.value || 0 }}/5</span>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">Comment (optional)</label>
                <textarea formControlName="comment" rows="3" class="form-input resize-none"
                          placeholder="Share your experience…" maxlength="1000"></textarea>
                <p class="text-xs text-gray-600 mt-1 text-right">
                  {{ (reviewForm.get('comment')?.value || '').length }}/1000
                </p>
              </div>
              <button type="submit" class="btn-primary" [disabled]="reviewForm.invalid || submittingReview()">
                <i class="fa-solid fa-spinner fa-spin" *ngIf="submittingReview()"></i>
                <i class="fa-solid fa-paper-plane" *ngIf="!submittingReview()"></i>
                {{ submittingReview() ? 'Submitting…' : 'Submit Review' }}
              </button>
            </form>
          </div>

          <!-- ── Testimonials ───────────────────────────────────── -->
          <div class="card space-y-4">
            <div class="flex items-center justify-between">
              <h3 class="section-title flex items-center gap-2">
                <i class="fa-solid fa-comments text-accent"></i> Client Reviews
              </h3>
              <span class="badge badge-green">{{ c.testimonials.length }} reviews</span>
            </div>

            <div *ngIf="c.testimonials.length" class="space-y-3">
              <div *ngFor="let t of c.testimonials"
                   class="border border-white/5 rounded-xl p-4 space-y-2 hover:border-white/10 transition-colors">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-accent/20 flex items-center justify-center
                                text-accent text-sm font-bold flex-shrink-0">
                      {{ t.clientInitials }}
                    </div>
                    <div>
                      <p class="font-medium text-white text-sm">{{ t.clientName }}</p>
                      <p class="text-xs text-gray-500">{{ t.createdAt | date:'dd MMM yyyy' }}</p>
                    </div>
                  </div>
                  <div class="flex items-center gap-0.5">
                    <i *ngFor="let s of [1,2,3,4,5]" class="fa-solid fa-star text-xs"
                       [class.text-warning]="s <= t.rating"
                       [class.text-gray-600]="s > t.rating"></i>
                  </div>
                </div>
                <p *ngIf="t.comment" class="text-sm text-gray-300 leading-relaxed pl-[52px]">{{ t.comment }}</p>
              </div>
            </div>

            <div *ngIf="!c.testimonials.length"
                 class="flex flex-col items-center justify-center py-10 text-center gap-2">
              <i class="fa-solid fa-comments text-gray-600 text-3xl"></i>
              <p class="text-gray-400 text-sm">No reviews yet.</p>
              <p class="text-gray-600 text-xs">Be the first to share your experience!</p>
            </div>
          </div>

        </main>
      </div>
    </div>
  `
})
export class CoachProfileComponent implements OnInit {
  private api   = inject(ClientApiService);
  private auth  = inject(AuthService);
  private route = inject(ActivatedRoute);
  private fb    = inject(FormBuilder);
  private toast = inject(ToastService);
  Math = Math;

  private readonly API_BASE = 'http://localhost:8080/api/v1';
  coachPicUrl(filename?: string): string | null {
    return filename ? `${this.API_BASE}/uploads/${filename}` : null;
  }

  coach            = signal<CoachDetailResponse | null>(null);
  loading          = signal(true);
  subscribing      = signal(false);
  submittingReview = signal(false);
  hoverRating      = signal(0);

  reviewForm = this.fb.group({
    rating:  [0, [Validators.required, Validators.min(1), Validators.max(5)]],
    comment: [''],
  });

  isMyCoach = () => this.auth.user()?.coach?.id === this.coach()?.id;

  ngOnInit(): void {
    const coachId = Number(this.route.snapshot.paramMap.get('id'));
    this.api.getCoachDetail(coachId).subscribe({
      next: c => {
        this.coach.set(c);
        this.loading.set(false);
        // Pre-fill if the client already left a review
        const myReview = c.testimonials.find(
          t => t.clientName === `${this.auth.user()?.firstName} ${this.auth.user()?.lastName}`
        );
        if (myReview) {
          this.reviewForm.patchValue({ rating: myReview.rating, comment: myReview.comment ?? '' });
        }
      },
      error: () => this.loading.set(false)
    });
  }

  setRating(r: number): void {
    this.reviewForm.patchValue({ rating: r });
  }

  subscribeToCoach(): void {
    const c = this.coach();
    if (!c) return;
    this.subscribing.set(true);
    this.api.subscribeToCoach(c.id).subscribe({
      next: res => {
        const u = this.auth.user();
        if (u) {
          const updated: any = { ...u, coach: { id: res.id, fullName: res.fullName, monthlyRate: res.monthlyRate, rating: res.rating } };
          localStorage.setItem('fg_user', JSON.stringify(updated));
          this.auth['_user'].set(updated);
        }
        this.subscribing.set(false);
        this.toast.success('Subscribed to ' + c.fullName + '!');
      },
      error: err => {
        this.subscribing.set(false);
        this.toast.error(err?.error?.message ?? 'Failed to subscribe.');
      }
    });
  }

  submitReview(): void {
    if (this.reviewForm.invalid) { this.reviewForm.markAllAsTouched(); return; }
    const c = this.coach();
    if (!c) return;
    this.submittingReview.set(true);

    const v = this.reviewForm.value;
    this.api.submitTestimonial(c.id, {
      rating: v.rating!,
      comment: v.comment || undefined,
    }).subscribe({
      next: () => {
        this.submittingReview.set(false);
        this.toast.success('Review submitted!');
        // Reload coach to get updated rating + testimonials
        this.api.getCoachDetail(c.id).subscribe(updated => this.coach.set(updated));
      },
      error: err => {
        this.submittingReview.set(false);
        this.toast.error(err?.error?.message ?? 'Failed to submit review.');
      }
    });
  }
}

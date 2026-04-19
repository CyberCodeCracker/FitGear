import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { AuthService } from '../../../core/services/auth.service';
import { ProgressEntry } from '../../../core/models/models';

@Component({
  selector: 'app-client-progress',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SidebarComponent, NavbarComponent, StatCardComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>

      <div class="flex-1 flex flex-col min-w-0">
        <app-navbar title="Progress Tracking">
          <button (click)="showForm.set(!showForm())" class="btn-primary btn-sm">
            <i class="fa-solid fa-plus"></i> Log Entry
          </button>
        </app-navbar>

        <main class="flex-1 p-6 space-y-6 animate-fade-in">

          <!-- Current biometrics from profile -->
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <app-stat-card label="Current Weight" [value]="latest().weight" unit="kg"
              icon="fa-weight-scale" iconColor="#22C55E" iconBg="rgba(34,197,94,0.15)"
              [change]="delta('weight')" changeUnit=" kg"></app-stat-card>
            <app-stat-card label="Body Fat" [value]="latest().bodyFat" unit="%"
              icon="fa-percent" iconColor="#3B82F6" iconBg="rgba(59,130,246,0.15)"
              [change]="delta('bodyFat')" changeUnit="%"></app-stat-card>
            <app-stat-card label="Muscle Mass" [value]="latest().muscleMass ?? '—'" unit="kg"
              icon="fa-fire" iconColor="#F59E0B" iconBg="rgba(245,158,11,0.15)"
              [change]="delta('muscleMass')" changeUnit=" kg"></app-stat-card>
            <app-stat-card label="Total Entries" [value]="entries().length" unit="logs"
              icon="fa-list-check" iconColor="#A855F7" iconBg="rgba(168,85,247,0.15)"></app-stat-card>
          </div>

          <!-- Add Entry Form -->
          <div *ngIf="showForm()" class="card animate-slide-up border-accent/20">
            <h3 class="section-title mb-4 flex items-center gap-2">
              <i class="fa-solid fa-plus text-accent"></i> New Progress Entry
            </h3>
            <form [formGroup]="form" (ngSubmit)="addEntry()" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              <div class="form-group">
                <label class="form-label">Date</label>
                <input formControlName="date" type="date" class="form-input">
              </div>
              <div class="form-group">
                <label class="form-label">Weight (kg)</label>
                <input formControlName="weight" type="number" step="0.1" class="form-input" placeholder="85.5">
              </div>
              <div class="form-group">
                <label class="form-label">Body Fat (%)</label>
                <input formControlName="bodyFat" type="number" step="0.1" class="form-input" placeholder="18.2">
              </div>
              <div class="form-group">
                <label class="form-label">Muscle Mass (kg)</label>
                <input formControlName="muscleMass" type="number" step="0.1" class="form-input" placeholder="70.8">
              </div>
              <div class="form-group sm:col-span-2 lg:col-span-4">
                <label class="form-label">Notes (optional)</label>
                <input formControlName="notes" type="text" class="form-input" placeholder="Great week overall…">
              </div>
              <div class="sm:col-span-2 lg:col-span-4 flex gap-3">
                <button type="submit" class="btn-primary" [disabled]="form.invalid">
                  <i class="fa-solid fa-floppy-disk"></i> Save Entry
                </button>
                <button type="button" (click)="showForm.set(false)" class="btn-secondary">Cancel</button>
              </div>
            </form>
          </div>

          <!-- Weight Trend chart -->
          <div class="card space-y-4">
            <h3 class="section-title flex items-center gap-2">
              <i class="fa-solid fa-chart-area text-info"></i> Weight Trend
            </h3>

            <div *ngIf="entries().length > 1" class="flex items-end gap-2 h-40 pt-4">
              <div *ngFor="let e of entries()" class="flex-1 flex flex-col items-center gap-1.5 group">
                <div class="w-full rounded-t-md transition-all duration-500 relative cursor-pointer"
                     [style.height.px]="barH(e.weight)"
                     style="background: linear-gradient(to top, #22C55E, #16A34A)">
                  <div class="absolute -top-7 left-1/2 -translate-x-1/2 text-xs bg-card border border-white/10 px-1.5 py-0.5 rounded opacity-0 group-hover:opacity-100 whitespace-nowrap transition-opacity pointer-events-none">
                    {{ e.weight }}kg
                  </div>
                </div>
                <span class="text-xs text-gray-500">{{ e.date | date:'dd/MM' }}</span>
              </div>
            </div>

            <div *ngIf="entries().length <= 1"
                 class="flex flex-col items-center justify-center h-32 text-center gap-2">
              <i class="fa-solid fa-chart-area text-gray-600 text-3xl"></i>
              <p class="text-gray-400 text-sm">Log at least 2 entries to see a trend.</p>
            </div>
          </div>

          <!-- History Table -->
          <div class="card space-y-4">
            <h3 class="section-title flex items-center gap-2">
              <i class="fa-solid fa-clock-rotate-left text-warning"></i> All Entries
            </h3>

            <div *ngIf="entries().length" class="table-wrapper">
              <table class="data-table">
                <thead><tr>
                  <th>Date</th><th>Weight (kg)</th><th>Body Fat (%)</th><th>Muscle (kg)</th><th>Notes</th>
                </tr></thead>
                <tbody>
                  <tr *ngFor="let e of entries().slice().reverse()">
                    <td>{{ e.date | date:'dd MMM yyyy' }}</td>
                    <td class="font-semibold text-white">{{ e.weight }}</td>
                    <td>{{ e.bodyFat }}</td>
                    <td>{{ e.muscleMass ?? '—' }}</td>
                    <td class="text-gray-500">{{ e.notes ?? '—' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div *ngIf="!entries().length"
                 class="flex flex-col items-center justify-center py-10 text-center gap-2">
              <i class="fa-solid fa-list-check text-gray-600 text-3xl"></i>
              <p class="text-gray-400 text-sm">No entries yet. Hit <strong class="text-white">Log Entry</strong> to start tracking.</p>
            </div>
          </div>

        </main>
      </div>
    </div>
  `
})
export class ClientProgressComponent {
  private auth = inject(AuthService);
  private fb   = inject(FormBuilder);

  private me = this.auth.user();

  // Seed the first entry from the user's registration data so the page
  // is never empty for a fresh account.
  entries = signal<ProgressEntry[]>(
    this.me?.weight && this.me?.bodyFatPercentage
      ? [{ id: 0, date: new Date().toISOString().substring(0, 10),
           weight: this.me.weight, bodyFat: this.me.bodyFatPercentage }]
      : []
  );

  showForm = signal(false);

  form = this.fb.group({
    date:       [new Date().toISOString().substring(0, 10), Validators.required],
    weight:     [null as number | null, Validators.required],
    bodyFat:    [null as number | null, Validators.required],
    muscleMass: [null as number | null],
    notes:      [''],
  });

  latest = computed(() => this.entries().at(-1) ?? { weight: 0, bodyFat: 0, muscleMass: 0 } as any);

  delta(field: 'weight' | 'bodyFat' | 'muscleMass'): number {
    const arr = this.entries();
    if (arr.length < 2) return 0;
    return +((arr.at(-1)![field] ?? 0) as number) - +((arr.at(-2)![field] ?? 0) as number);
  }

  barH(w: number): number {
    const all = this.entries().map(e => e.weight);
    const min = Math.min(...all), max = Math.max(...all);
    return max === min ? 50 : ((w - min) / (max - min)) * 110 + 20;
  }

  addEntry(): void {
    if (this.form.invalid) return;
    const v = this.form.value;
    const entry: ProgressEntry = {
      id:         this.entries().length + 1,
      date:       v.date!,
      weight:     v.weight!,
      bodyFat:    v.bodyFat!,
      muscleMass: v.muscleMass ?? undefined,
      notes:      v.notes ?? undefined,
    };
    this.entries.update(e => [...e, entry]);
    this.form.reset({ date: new Date().toISOString().substring(0, 10) });
    this.showForm.set(false);
  }
}

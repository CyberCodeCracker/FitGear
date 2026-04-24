import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { ClientApiService } from '../../../core/services/client-api.service';
import { ToastService } from '../../../shared/components/toast/toast.service';
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

          <!-- Loading -->
          <div *ngIf="loading()" class="flex items-center justify-center py-16">
            <i class="fa-solid fa-spinner fa-spin text-accent text-3xl"></i>
          </div>

          <ng-container *ngIf="!loading()">
            <!-- Stat cards -->
            <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
              <app-stat-card label="Current Weight" [value]="latest()?.weight ?? '—'" unit="kg"
                icon="fa-weight-scale" iconColor="#22C55E" iconBg="rgba(34,197,94,0.15)"
                [change]="delta('weight')" changeUnit=" kg"></app-stat-card>
              <app-stat-card label="Body Fat" [value]="latest()?.bodyFat ?? '—'" unit="%"
                icon="fa-percent" iconColor="#3B82F6" iconBg="rgba(59,130,246,0.15)"
                [change]="delta('bodyFat')" changeUnit="%"></app-stat-card>
              <app-stat-card label="Muscle Mass" [value]="latest()?.muscleMass ?? '—'" unit="kg"
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
                  <input formControlName="entryDate" type="date" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">Weight (kg)</label>
                  <input formControlName="weight" type="number" step="0.1" class="form-input" placeholder="85.5"
                         [class.error]="f['weight'].invalid && f['weight'].touched">
                  <p *ngIf="f['weight'].invalid && f['weight'].touched" class="form-error">Required, min 20 kg.</p>
                </div>
                <div class="form-group">
                  <label class="form-label">Body Fat (%)</label>
                  <input formControlName="bodyFat" type="number" step="0.1" class="form-input" placeholder="18.2"
                         [class.error]="f['bodyFat'].invalid && f['bodyFat'].touched">
                  <p *ngIf="f['bodyFat'].invalid && f['bodyFat'].touched" class="form-error">Required, 0–80%.</p>
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
                  <button type="submit" class="btn-primary" [disabled]="form.invalid || saving()">
                    <i class="fa-solid fa-spinner fa-spin" *ngIf="saving()"></i>
                    <i class="fa-solid fa-floppy-disk" *ngIf="!saving()"></i>
                    {{ saving() ? 'Saving…' : 'Save Entry' }}
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
                  <span class="text-xs text-gray-500">{{ e.entryDate | date:'dd/MM' }}</span>
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
                    <th>Date</th><th>Weight (kg)</th><th>Body Fat (%)</th><th>Muscle (kg)</th><th>Notes</th><th></th>
                  </tr></thead>
                  <tbody>
                    <tr *ngFor="let e of reversed()">
                      <td>{{ e.entryDate | date:'dd MMM yyyy' }}</td>
                      <td class="font-semibold text-white">{{ e.weight }}</td>
                      <td>{{ e.bodyFat }}</td>
                      <td>{{ e.muscleMass ?? '—' }}</td>
                      <td class="text-gray-500 max-w-[200px] truncate">{{ e.notes ?? '—' }}</td>
                      <td>
                        <button (click)="deleteEntry(e.id)" class="btn-icon text-danger hover:bg-danger/10"
                                [disabled]="deleting() === e.id" title="Delete entry">
                          <i class="fa-solid" [class.fa-spinner]="deleting() === e.id"
                             [class.fa-spin]="deleting() === e.id"
                             [class.fa-trash]="deleting() !== e.id"></i>
                        </button>
                      </td>
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
          </ng-container>

        </main>
      </div>
    </div>
  `
})
export class ClientProgressComponent implements OnInit {
  private api   = inject(ClientApiService);
  private fb    = inject(FormBuilder);
  private toast = inject(ToastService);

  entries  = signal<ProgressEntry[]>([]);
  loading  = signal(true);
  saving   = signal(false);
  deleting = signal<number | null>(null);
  showForm = signal(false);

  form = this.fb.group({
    entryDate:  [new Date().toISOString().substring(0, 10), Validators.required],
    weight:     [null as number | null, [Validators.required, Validators.min(20)]],
    bodyFat:    [null as number | null, [Validators.required, Validators.min(0), Validators.max(80)]],
    muscleMass: [null as number | null],
    notes:      [''],
  });

  get f() { return this.form.controls; }

  latest   = computed(() => this.entries().at(-1) ?? null);
  reversed = computed(() => [...this.entries()].reverse());

  ngOnInit(): void {
    this.api.getProgressEntries().subscribe({
      next: entries => { this.entries.set(entries); this.loading.set(false); },
      error: ()     => this.loading.set(false)
    });
  }

  delta(field: 'weight' | 'bodyFat' | 'muscleMass'): number {
    const arr = this.entries();
    if (arr.length < 2) return 0;
    const curr = (arr.at(-1) as any)?.[field] ?? 0;
    const prev = (arr.at(-2) as any)?.[field] ?? 0;
    return +(curr - prev).toFixed(1);
  }

  barH(w: number): number {
    const all = this.entries().map(e => e.weight);
    const min = Math.min(...all), max = Math.max(...all);
    return max === min ? 50 : ((w - min) / (max - min)) * 110 + 20;
  }

  addEntry(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving.set(true);
    const v = this.form.value;

    this.api.createProgressEntry({
      entryDate:  v.entryDate!,
      weight:     v.weight!,
      bodyFat:    v.bodyFat!,
      muscleMass: v.muscleMass ?? null,
      notes:      v.notes || null,
    }).subscribe({
      next: entry => {
        this.entries.update(e => [...e, entry]);
        this.form.reset({ entryDate: new Date().toISOString().substring(0, 10) });
        this.showForm.set(false);
        this.saving.set(false);
        this.toast.success('Progress entry saved!');
      },
      error: err => {
        this.saving.set(false);
        this.toast.error(err?.error?.message ?? 'Failed to save entry.');
      }
    });
  }

  deleteEntry(id: number): void {
    this.deleting.set(id);
    this.api.deleteProgressEntry(id).subscribe({
      next: () => {
        this.entries.update(e => e.filter(x => x.id !== id));
        this.deleting.set(null);
        this.toast.success('Entry deleted.');
      },
      error: () => {
        this.deleting.set(null);
        this.toast.error('Failed to delete entry.');
      }
    });
  }
}

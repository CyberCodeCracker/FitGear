import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, FormArray, FormGroup } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { CoachApiService } from '../../../core/services/coach-api.service';
import { ClientResponse } from '../../../core/models/models';

type Tab = 'overview' | 'training' | 'diet';

@Component({
  selector: 'app-client-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, SidebarComponent, NavbarComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>
      <div class="flex-1 flex flex-col min-w-0">

        <app-navbar title="Client Management"
          [breadcrumbs]="[{label:'Clients', route:'/coach/clients'}, {label: clientName()}]">
        </app-navbar>

        <!-- Loading -->
        <div *ngIf="loading()" class="flex-1 flex items-center justify-center">
          <i class="fa-solid fa-spinner fa-spin text-accent text-3xl"></i>
        </div>

        <!-- Error -->
        <div *ngIf="!loading() && !client()" class="flex-1 flex flex-col items-center justify-center gap-3">
          <i class="fa-solid fa-circle-exclamation text-danger text-4xl"></i>
          <p class="text-white font-semibold">Client not found</p>
          <a routerLink="/coach/clients" class="btn-secondary btn-sm">Back to clients</a>
        </div>

        <main *ngIf="!loading() && client() as c" class="flex-1 p-6 space-y-6 animate-fade-in">

          <!-- Header card -->
          <div class="card flex flex-col sm:flex-row items-start sm:items-center gap-5">
            <div class="w-16 h-16 rounded-full bg-gradient-to-br from-accent/30 to-accent/10 flex items-center justify-center text-accent text-2xl font-bold flex-shrink-0">
              {{ c.firstName[0] }}{{ c.lastName[0] }}
            </div>
            <div class="flex-1">
              <h2 class="text-xl font-bold text-white">{{ c.firstName }} {{ c.lastName }}</h2>
              <p class="text-muted">Client #{{ c.id }}</p>
              <div class="flex flex-wrap gap-2 mt-2">
                <span class="badge badge-blue"><i class="fa-solid fa-ruler-vertical mr-1"></i>{{ c.height }} cm</span>
                <span class="badge badge-green"><i class="fa-solid fa-weight-scale mr-1"></i>{{ c.weight }} kg</span>
                <span class="badge"
                      [class.badge-green]="c.bodyFatPercentage < 18"
                      [class.badge-yellow]="c.bodyFatPercentage >= 18 && c.bodyFatPercentage < 25"
                      [class.badge-red]="c.bodyFatPercentage >= 25">
                  <i class="fa-solid fa-percent mr-1"></i>{{ c.bodyFatPercentage }}% BF
                </span>
              </div>
            </div>
          </div>

          <!-- Tabs -->
          <div class="flex gap-1 border border-white/10 rounded-xl p-1 bg-card w-fit">
            <button *ngFor="let t of tabs" (click)="activeTab.set(t.key)"
                    class="px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200"
                    [class.bg-accent]="activeTab() === t.key"
                    [class.text-black]="activeTab() === t.key"
                    [class.text-gray-400]="activeTab() !== t.key">
              <i class="fa-solid mr-2" [class]="t.icon"></i>{{ t.label }}
            </button>
          </div>

          <!-- OVERVIEW -->
          <ng-container *ngIf="activeTab() === 'overview'">
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-5 animate-slide-up">
              <div class="card text-center space-y-2">
                <p class="text-xs text-gray-500 uppercase tracking-wider">Height</p>
                <p class="text-3xl font-bold text-white">{{ c.height }}<span class="text-lg text-gray-400"> cm</span></p>
              </div>
              <div class="card text-center space-y-2">
                <p class="text-xs text-gray-500 uppercase tracking-wider">Weight</p>
                <p class="text-3xl font-bold text-white">{{ c.weight }}<span class="text-lg text-gray-400"> kg</span></p>
              </div>
              <div class="card text-center space-y-2">
                <p class="text-xs text-gray-500 uppercase tracking-wider">Body Fat</p>
                <p class="text-3xl font-bold"
                   [class.text-accent]="c.bodyFatPercentage < 18"
                   [class.text-warning]="c.bodyFatPercentage >= 18 && c.bodyFatPercentage < 25"
                   [class.text-danger]="c.bodyFatPercentage >= 25">
                  {{ c.bodyFatPercentage }}<span class="text-lg text-gray-400">%</span>
                </p>
              </div>
            </div>
            <div class="card animate-slide-up">
              <p class="text-muted text-sm text-center py-6">
                <i class="fa-solid fa-dumbbell text-gray-600 text-2xl block mb-2"></i>
                Training and diet programs are managed by the coach.<br>
                Switch to the <strong class="text-white">Training</strong> or <strong class="text-white">Diet Plan</strong> tab to assign programs.
              </p>
            </div>
          </ng-container>

          <!-- TRAINING TAB (form — not yet wired to API) -->
          <ng-container *ngIf="activeTab() === 'training'">
            <div class="card space-y-4 animate-slide-up">
              <div class="flex items-center justify-between">
                <h3 class="section-title flex items-center gap-2">
                  <i class="fa-solid fa-dumbbell text-accent"></i> Training Program
                </h3>
                <span *ngIf="trainingSaved()" class="badge badge-green animate-fade-in">
                  <i class="fa-solid fa-check"></i> Saved!
                </span>
              </div>
              <form [formGroup]="trainingForm" (ngSubmit)="saveTraining()" class="space-y-5">
                <div formArrayName="days" class="space-y-4">
                  <div *ngFor="let day of daysArray.controls; let di = index" [formGroupName]="di"
                       class="border border-white/10 rounded-xl p-4 space-y-4 hover:border-accent/20 transition-colors">
                    <div class="flex items-end justify-between gap-3">
                      <div class="flex gap-3 flex-1">
                        <div class="form-group mb-0 w-40">
                          <label class="form-label">Day</label>
                          <input formControlName="dayOfWeek" class="form-input" placeholder="e.g. Monday">
                        </div>
                        <div class="form-group mb-0 flex-1">
                          <label class="form-label">Focus / muscle group</label>
                          <input formControlName="focus" class="form-input" placeholder="e.g. Push — Chest / Shoulders">
                        </div>
                      </div>
                      <button type="button" (click)="removeDay(di)" class="btn-danger btn-sm mb-0.5">
                        <i class="fa-solid fa-trash"></i> Remove Day
                      </button>
                    </div>
                    <div formArrayName="exercises" class="space-y-3 pl-4 border-l border-white/10">
                      <div *ngFor="let ex of getExercises(di).controls; let ei = index" [formGroupName]="ei"
                           class="grid grid-cols-12 gap-3 items-end border border-white/5 rounded-lg p-3 bg-card-2/30 hover:bg-card-2/50 transition-colors">
                        <div class="col-span-4 form-group mb-0">
                          <label class="form-label">Exercise name</label>
                          <input formControlName="name" class="form-input" placeholder="e.g. Bench Press">
                        </div>
                        <div class="col-span-2 form-group mb-0">
                          <label class="form-label">Sets</label>
                          <input formControlName="sets" class="form-input" type="number" min="1" placeholder="4">
                        </div>
                        <div class="col-span-2 form-group mb-0">
                          <label class="form-label">Reps</label>
                          <input formControlName="reps" class="form-input" type="number" min="1" placeholder="10">
                        </div>
                        <div class="col-span-3 form-group mb-0">
                          <label class="form-label">Weight (kg)</label>
                          <input formControlName="weight" class="form-input" type="number" min="0" placeholder="0">
                        </div>
                        <div class="col-span-1 flex items-end">
                          <button type="button" (click)="removeExercise(di,ei)"
                                  class="btn-icon text-danger hover:bg-danger/10 w-full flex justify-center">
                            <i class="fa-solid fa-xmark"></i>
                          </button>
                        </div>
                      </div>
                      <button type="button" (click)="addExercise(di)" class="btn-ghost btn-sm">
                        <i class="fa-solid fa-plus"></i> Add Exercise
                      </button>
                    </div>
                  </div>
                </div>
                <div class="flex gap-3">
                  <button type="button" (click)="addDay()" class="btn-secondary">
                    <i class="fa-solid fa-plus"></i> Add Day
                  </button>
                  <button type="submit" class="btn-primary">
                    <i class="fa-solid fa-floppy-disk"></i> Save Program
                  </button>
                </div>
              </form>
            </div>
          </ng-container>

          <!-- DIET TAB (form — not yet wired to API) -->
          <ng-container *ngIf="activeTab() === 'diet'">
            <div class="card space-y-4 animate-slide-up">
              <div class="flex items-center justify-between">
                <h3 class="section-title flex items-center gap-2">
                  <i class="fa-solid fa-bowl-food text-warning"></i> Diet Plan
                </h3>
                <span *ngIf="dietSaved()" class="badge badge-green animate-fade-in">
                  <i class="fa-solid fa-check"></i> Saved!
                </span>
              </div>
              <form [formGroup]="dietForm" (ngSubmit)="saveDiet()" class="space-y-5">
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div class="form-group">
                    <label class="form-label">Plan Title</label>
                    <input formControlName="title" class="form-input" placeholder="e.g. Lean Bulk – 2800 kcal">
                  </div>
                  <div class="form-group">
                    <label class="form-label">Description</label>
                    <input formControlName="description" class="form-input" placeholder="Brief description…">
                  </div>
                </div>
                <div formArrayName="days" class="space-y-4">
                  <div *ngFor="let day of dietDaysArray.controls; let di = index" [formGroupName]="di"
                       class="border border-white/10 rounded-xl p-4 space-y-4 hover:border-warning/20 transition-colors">
                    <div class="flex items-end justify-between gap-3">
                      <div class="form-group mb-0 w-64">
                        <label class="form-label">Day label</label>
                        <input formControlName="dayLabel" class="form-input" placeholder="e.g. Weekday Template">
                      </div>
                      <button type="button" (click)="removeDietDay(di)" class="btn-danger btn-sm mb-0.5">
                        <i class="fa-solid fa-trash"></i> Remove Day
                      </button>
                    </div>
                    <div formArrayName="meals" class="space-y-3 pl-4 border-l border-white/10">
                      <div *ngFor="let meal of getDietMeals(di).controls; let mi = index" [formGroupName]="mi"
                           class="grid grid-cols-12 gap-3 items-end border border-white/5 rounded-lg p-3 bg-card-2/30 hover:bg-card-2/50 transition-colors">
                        <div class="col-span-3 form-group mb-0">
                          <label class="form-label">Meal name</label>
                          <input formControlName="name" class="form-input" placeholder="e.g. Breakfast">
                        </div>
                        <div class="col-span-2 form-group mb-0">
                          <label class="form-label">Calories</label>
                          <input formControlName="calories" class="form-input" type="number" min="0" placeholder="500">
                        </div>
                        <div class="col-span-2 form-group mb-0">
                          <label class="form-label">Protein (g)</label>
                          <input formControlName="protein" class="form-input" type="number" min="0" placeholder="40">
                        </div>
                        <div class="col-span-2 form-group mb-0">
                          <label class="form-label">Carbs (g)</label>
                          <input formControlName="carbs" class="form-input" type="number" min="0" placeholder="60">
                        </div>
                        <div class="col-span-2 form-group mb-0">
                          <label class="form-label">Fat (g)</label>
                          <input formControlName="fat" class="form-input" type="number" min="0" placeholder="15">
                        </div>
                        <div class="col-span-1 flex items-end">
                          <button type="button" (click)="removeMeal(di,mi)"
                                  class="btn-icon text-danger hover:bg-danger/10 w-full flex justify-center">
                            <i class="fa-solid fa-xmark"></i>
                          </button>
                        </div>
                      </div>
                      <button type="button" (click)="addMeal(di)" class="btn-ghost btn-sm">
                        <i class="fa-solid fa-plus"></i> Add Meal
                      </button>
                    </div>
                  </div>
                </div>
                <div class="flex gap-3">
                  <button type="button" (click)="addDietDay()" class="btn-secondary">
                    <i class="fa-solid fa-plus"></i> Add Day
                  </button>
                  <button type="submit" class="btn-primary">
                    <i class="fa-solid fa-floppy-disk"></i> Save Diet Plan
                  </button>
                </div>
              </form>
            </div>
          </ng-container>

        </main>
      </div>
    </div>
  `
})
export class ClientDetailComponent implements OnInit {
  private api   = inject(CoachApiService);
  private route = inject(ActivatedRoute);
  private fb    = inject(FormBuilder);

  client      = signal<ClientResponse | null>(null);
  loading     = signal(true);
  activeTab   = signal<Tab>('overview');
  trainingSaved = signal(false);
  dietSaved     = signal(false);

  tabs = [
    { key: 'overview' as Tab, label: 'Overview',  icon: 'fa-gauge-high' },
    { key: 'training' as Tab, label: 'Training',  icon: 'fa-dumbbell'   },
    { key: 'diet'     as Tab, label: 'Diet Plan', icon: 'fa-bowl-food'  },
  ];

  trainingForm!: FormGroup;
  dietForm!: FormGroup;

  clientName = () => {
    const c = this.client();
    return c ? `${c.firstName} ${c.lastName}` : '…';
  };

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.buildForms();
    this.api.getClientById(id).subscribe({
      next: c  => { this.client.set(c); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  buildForms(): void {
    this.trainingForm = this.fb.group({ days: this.fb.array([]) });
    this.dietForm = this.fb.group({
      title: [''], description: [''], days: this.fb.array([])
    });
  }

  get daysArray()     { return this.trainingForm.get('days') as FormArray; }
  get dietDaysArray() { return this.dietForm.get('days') as FormArray; }
  getExercises(di: number) { return (this.daysArray.at(di) as FormGroup).get('exercises') as FormArray; }
  getDietMeals(di: number) { return (this.dietDaysArray.at(di) as FormGroup).get('meals') as FormArray; }

  addDay() { this.daysArray.push(this.fb.group({ dayOfWeek: [''], focus: [''], exercises: this.fb.array([]) })); }
  removeDay(i: number) { this.daysArray.removeAt(i); }
  addExercise(di: number) { this.getExercises(di).push(this.fb.group({ name: [''], sets: [3], reps: [10], weight: [''] })); }
  removeExercise(di: number, ei: number) { this.getExercises(di).removeAt(ei); }
  addDietDay() { this.dietDaysArray.push(this.fb.group({ dayLabel: [''], meals: this.fb.array([]) })); }
  removeDietDay(i: number) { this.dietDaysArray.removeAt(i); }
  addMeal(di: number) { this.getDietMeals(di).push(this.fb.group({ name: [''], calories: [0], protein: [0], carbs: [0], fat: [0] })); }
  removeMeal(di: number, mi: number) { this.getDietMeals(di).removeAt(mi); }

  saveTraining() { this.trainingSaved.set(true); setTimeout(() => this.trainingSaved.set(false), 2500); }
  saveDiet()     { this.dietSaved.set(true);    setTimeout(() => this.dietSaved.set(false), 2500); }
}

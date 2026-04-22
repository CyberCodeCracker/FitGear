import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, FormArray, FormGroup, Validators } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { CoachApiService } from '../../../core/services/coach-api.service';
import {
  ClientResponse, TrainingProgramResponse, DietProgramResponse,
  TrainingProgramRequest, DietProgramRequest, TrainingDayResponse, DietDayResponse
} from '../../../core/models/models';

type Tab = 'overview' | 'training' | 'diet';
const DAYS = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];

@Component({
  selector: 'app-client-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, SidebarComponent, NavbarComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>
      <div class="flex-1 flex flex-col min-w-0">

        <app-navbar title="Client Management"
          [breadcrumbs]="[{label:'Clients',route:'/coach/clients'},{label:clientName()}]">
        </app-navbar>

        <div *ngIf="loading()" class="flex-1 flex items-center justify-center">
          <i class="fa-solid fa-spinner fa-spin text-accent text-3xl"></i>
        </div>
        <div *ngIf="!loading() && !client()" class="flex-1 flex flex-col items-center justify-center gap-3">
          <i class="fa-solid fa-circle-exclamation text-danger text-4xl"></i>
          <p class="text-white font-semibold">Client not found</p>
          <a routerLink="/coach/clients" class="btn-secondary btn-sm">Back to clients</a>
        </div>

        <main *ngIf="!loading() && client() as c" class="flex-1 p-6 space-y-6 animate-fade-in">

          <!-- Header -->
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
            <button *ngFor="let t of tabs" (click)="setTab(t.key)"
                    class="px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200"
                    [class.bg-accent]="activeTab()===t.key" [class.text-black]="activeTab()===t.key"
                    [class.text-gray-400]="activeTab()!==t.key">
              <i class="fa-solid mr-2" [class]="t.icon"></i>{{ t.label }}
            </button>
          </div>

          <!-- ═══ OVERVIEW ═══ -->
          <ng-container *ngIf="activeTab()==='overview'">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 animate-slide-up">

              <!-- Training summary -->
              <div class="card space-y-3">
                <div class="flex items-center justify-between">
                  <h3 class="section-title flex items-center gap-2">
                    <i class="fa-solid fa-dumbbell text-accent"></i> Training Program
                  </h3>
                  <span *ngIf="training()" class="badge badge-green">Active</span>
                  <span *ngIf="!training() && !loadingTraining()" class="badge badge-yellow">Not assigned</span>
                  <i *ngIf="loadingTraining()" class="fa-solid fa-spinner fa-spin text-gray-400 text-sm"></i>
                </div>
                <ng-container *ngIf="training() as tp">
                  <div *ngFor="let day of tp.trainingDays"
                       class="flex items-center justify-between px-3 py-2.5 rounded-lg bg-card-2/60 hover:bg-card-2">
                    <div>
                      <p class="text-sm font-medium text-white">{{ day.dayOfWeek | titlecase }}</p>
                      <p class="text-xs text-gray-500">{{ day.title }}</p>
                    </div>
                    <div class="flex items-center gap-2">
                      <span class="badge badge-green">{{ day.exercises.length }} ex.</span>
                      <span class="text-xs text-gray-500">~{{ day.estimatedBurnedCalories }} kcal</span>
                    </div>
                  </div>
                </ng-container>
                <p *ngIf="!training() && !loadingTraining()" class="text-muted text-sm text-center py-4">
                  No training program. Switch to the Training tab to assign one.
                </p>
                <button (click)="setTab('training')" class="btn-ghost btn-sm w-full">
                  <i class="fa-solid fa-pen-to-square"></i>
                  {{ training() ? 'Edit program' : 'Assign program' }}
                </button>
              </div>

              <!-- Diet summary -->
              <div class="card space-y-3">
                <div class="flex items-center justify-between">
                  <h3 class="section-title flex items-center gap-2">
                    <i class="fa-solid fa-bowl-food text-warning"></i> Diet Plan
                  </h3>
                  <span *ngIf="diet()" class="badge badge-yellow truncate max-w-[140px]">{{ diet()!.title }}</span>
                  <span *ngIf="!diet() && !loadingDiet()" class="badge badge-yellow">Not assigned</span>
                  <i *ngIf="loadingDiet()" class="fa-solid fa-spinner fa-spin text-gray-400 text-sm"></i>
                </div>
                <ng-container *ngIf="diet() as dp">
                  <div *ngFor="let day of dp.days" class="space-y-1">
                    <p class="text-xs text-gray-500 uppercase tracking-wider px-1">{{ day.dayOfWeek | titlecase }}</p>
                    <div *ngFor="let meal of day.meals"
                         class="flex items-start gap-3 px-3 py-2 rounded-lg bg-card-2/60 hover:bg-card-2">
                      <i class="fa-solid fa-utensils text-warning text-xs mt-1 flex-shrink-0"></i>
                      <div class="flex-1 min-w-0">
                        <p class="text-sm font-medium text-white">{{ meal.description }}</p>
                        <p class="text-xs text-gray-500">P:{{ meal.protein }}g · C:{{ meal.carbs }}g · F:{{ meal.fats }}g</p>
                      </div>
                      <span class="text-sm font-semibold text-warning flex-shrink-0">{{ meal.calories }} kcal</span>
                    </div>
                  </div>
                </ng-container>
                <p *ngIf="!diet() && !loadingDiet()" class="text-muted text-sm text-center py-4">
                  No diet plan. Switch to the Diet Plan tab to assign one.
                </p>
                <button (click)="setTab('diet')" class="btn-ghost btn-sm w-full">
                  <i class="fa-solid fa-pen-to-square"></i>
                  {{ diet() ? 'Edit diet plan' : 'Assign diet plan' }}
                </button>
              </div>
            </div>
          </ng-container>

          <!-- ═══ TRAINING TAB ═══ -->
          <ng-container *ngIf="activeTab()==='training'">
            <div class="card space-y-5 animate-slide-up">
              <div class="flex items-center justify-between flex-wrap gap-3">
                <h3 class="section-title flex items-center gap-2">
                  <i class="fa-solid fa-dumbbell text-accent"></i> Training Program
                </h3>
                <div class="flex items-center gap-2">
                  <span *ngIf="trainingSaved()" class="badge badge-green animate-fade-in">
                    <i class="fa-solid fa-check"></i> Saved!
                  </span>
                  <button *ngIf="training()" type="button" (click)="deleteTraining()"
                          class="btn-danger btn-sm" [disabled]="savingTraining()">
                    <i class="fa-solid fa-trash"></i> Delete Program
                  </button>
                </div>
              </div>

              <form [formGroup]="trainingForm" (ngSubmit)="saveTraining()" class="space-y-4">
                <div formArrayName="days" class="space-y-4">
                  <div *ngFor="let day of daysArray.controls; let di = index" [formGroupName]="di"
                       class="border border-white/10 rounded-xl p-4 space-y-4 hover:border-accent/20 transition-colors">

                    <!-- Day header -->
                    <div class="flex items-end justify-between gap-3">
                      <div class="flex gap-3 flex-1 flex-wrap">
                        <div class="form-group mb-0">
                          <label class="form-label">Day of week</label>
                          <select formControlName="day" class="form-input w-40">
                            <option *ngFor="let d of dayOptions" [value]="d.value">{{ d.label }}</option>
                          </select>
                        </div>
                        <div class="form-group mb-0 flex-1 min-w-[160px]">
                          <label class="form-label">Session title</label>
                          <input formControlName="title" class="form-input" placeholder="e.g. Push — Chest / Shoulders">
                        </div>
                        <div class="form-group mb-0 w-36">
                          <label class="form-label">Est. burned kcal</label>
                          <input formControlName="estimatedBurnedCalories" class="form-input" type="number" min="0" placeholder="400">
                        </div>
                      </div>
                      <button type="button" (click)="removeDay(di)" class="btn-danger btn-sm mb-0.5 flex-shrink-0">
                        <i class="fa-solid fa-trash"></i>
                      </button>
                    </div>

                    <!-- Exercises -->
                    <div formArrayName="exercises" class="space-y-3 pl-4 border-l-2 border-accent/20">
                      <p class="text-xs text-gray-500 uppercase tracking-wider">Exercises</p>
                      <div *ngFor="let ex of getExercises(di).controls; let ei = index" [formGroupName]="ei"
                           class="grid grid-cols-12 gap-3 items-end p-3 rounded-lg bg-card-2/40 hover:bg-card-2/60 transition-colors">
                        <div class="col-span-5 form-group mb-0">
                          <label class="form-label">Exercise name</label>
                          <input formControlName="title" class="form-input" placeholder="e.g. Bench Press">
                        </div>
                        <div class="col-span-2 form-group mb-0">
                          <label class="form-label">Sets</label>
                          <input formControlName="numberOfSets" class="form-input" type="number" min="1" placeholder="4">
                        </div>
                        <div class="col-span-2 form-group mb-0">
                          <label class="form-label">Reps</label>
                          <input formControlName="numberOfReps" class="form-input" type="number" min="1" placeholder="10">
                        </div>
                        <div class="col-span-2 form-group mb-0">
                          <label class="form-label">Rest (sec)</label>
                          <input formControlName="restTime" class="form-input" type="number" min="0" placeholder="90">
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

                <div class="flex gap-3 flex-wrap">
                  <button type="button" (click)="addDay()" class="btn-secondary">
                    <i class="fa-solid fa-plus"></i> Add Training Day
                  </button>
                  <button type="submit" class="btn-primary" [disabled]="savingTraining() || trainingForm.invalid">
                    <i class="fa-solid fa-spinner fa-spin" *ngIf="savingTraining()"></i>
                    <i class="fa-solid fa-floppy-disk" *ngIf="!savingTraining()"></i>
                    {{ training() ? 'Update Program' : 'Assign Program' }}
                  </button>
                </div>
              </form>
            </div>
          </ng-container>

          <!-- ═══ DIET TAB ═══ -->
          <ng-container *ngIf="activeTab()==='diet'">
            <div class="card space-y-5 animate-slide-up">
              <div class="flex items-center justify-between flex-wrap gap-3">
                <h3 class="section-title flex items-center gap-2">
                  <i class="fa-solid fa-bowl-food text-warning"></i> Diet Plan
                </h3>
                <div class="flex items-center gap-2">
                  <span *ngIf="dietSaved()" class="badge badge-green animate-fade-in">
                    <i class="fa-solid fa-check"></i> Saved!
                  </span>
                  <button *ngIf="diet()" type="button" (click)="deleteDiet()"
                          class="btn-danger btn-sm" [disabled]="savingDiet()">
                    <i class="fa-solid fa-trash"></i> Delete Plan
                  </button>
                </div>
              </div>

              <form [formGroup]="dietForm" (ngSubmit)="saveDiet()" class="space-y-5">
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div class="form-group">
                    <label class="form-label">Plan title</label>
                    <input formControlName="title" class="form-input" placeholder="e.g. Lean Bulk – 2800 kcal">
                  </div>
                  <div class="form-group">
                    <label class="form-label">Plan description</label>
                    <input formControlName="description" class="form-input" placeholder="Brief overview of the plan…">
                  </div>
                </div>

                <div formArrayName="days" class="space-y-4">
                  <div *ngFor="let day of dietDaysArray.controls; let di = index" [formGroupName]="di"
                       class="border border-white/10 rounded-xl p-4 space-y-4 hover:border-warning/20 transition-colors">

                    <!-- Diet day header -->
                    <div class="flex items-end justify-between gap-3">
                      <div class="form-group mb-0">
                        <label class="form-label">Day of week</label>
                        <select formControlName="dayOfWeek" class="form-input w-44">
                          <option *ngFor="let d of dayOptions" [value]="d.value">{{ d.label }}</option>
                        </select>
                      </div>
                      <button type="button" (click)="removeDietDay(di)" class="btn-danger btn-sm mb-0.5">
                        <i class="fa-solid fa-trash"></i> Remove Day
                      </button>
                    </div>

                    <!-- Meals -->
                    <div formArrayName="meals" class="space-y-3 pl-4 border-l-2 border-warning/20">
                      <p class="text-xs text-gray-500 uppercase tracking-wider">Meals</p>
                      <div *ngFor="let meal of getDietMeals(di).controls; let mi = index" [formGroupName]="mi"
                           class="grid grid-cols-12 gap-3 items-end p-3 rounded-lg bg-card-2/40 hover:bg-card-2/60 transition-colors">
                        <div class="col-span-12 sm:col-span-5 form-group mb-0">
                          <label class="form-label">Description</label>
                          <input formControlName="description" class="form-input"
                                 placeholder="e.g. 200g chicken breast, 150g rice, 10g olive oil">
                        </div>
                        <div class="col-span-6 sm:col-span-2 form-group mb-0">
                          <label class="form-label">Calories</label>
                          <input formControlName="calories" class="form-input" type="number" min="0" placeholder="500">
                        </div>
                        <div class="col-span-6 sm:col-span-1 form-group mb-0">
                          <label class="form-label">Protein (g)</label>
                          <input formControlName="protein" class="form-input" type="number" min="0" placeholder="40">
                        </div>
                        <div class="col-span-6 sm:col-span-1 form-group mb-0">
                          <label class="form-label">Carbs (g)</label>
                          <input formControlName="carbs" class="form-input" type="number" min="0" placeholder="60">
                        </div>
                        <div class="col-span-6 sm:col-span-1 form-group mb-0">
                          <label class="form-label">Fat (g)</label>
                          <input formControlName="fats" class="form-input" type="number" min="0" placeholder="15">
                        </div>
                        <div class="col-span-6 sm:col-span-1 form-group mb-0">
                          <label class="form-label">Time</label>
                          <input formControlName="timeToEat" class="form-input" type="time">
                        </div>
                        <div class="col-span-6 sm:col-span-1 flex items-end">
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

                    <!-- Day totals (auto-computed) -->
                    <div class="flex flex-wrap gap-3 pt-2 border-t border-white/5">
                      <p class="text-xs text-gray-500 uppercase tracking-wider w-full">Day totals</p>
                      <div class="form-group mb-0 w-28">
                        <label class="form-label">Total kcal</label>
                        <input formControlName="totalCaloriesInDay" class="form-input" type="number" min="0" placeholder="2000">
                      </div>
                      <div class="form-group mb-0 w-28">
                        <label class="form-label">Protein (g)</label>
                        <input formControlName="totalProteinInDay" class="form-input" type="number" min="0" placeholder="180">
                      </div>
                      <div class="form-group mb-0 w-28">
                        <label class="form-label">Carbs (g)</label>
                        <input formControlName="totalCarbsInDay" class="form-input" type="number" min="0" placeholder="220">
                      </div>
                      <div class="form-group mb-0 w-28">
                        <label class="form-label">Fats (g)</label>
                        <input formControlName="totalFatsInDay" class="form-input" type="number" min="0" placeholder="70">
                      </div>
                    </div>
                  </div>
                </div>

                <div class="flex gap-3 flex-wrap">
                  <button type="button" (click)="addDietDay()" class="btn-secondary">
                    <i class="fa-solid fa-plus"></i> Add Diet Day
                  </button>
                  <button type="submit" class="btn-primary" [disabled]="savingDiet() || dietForm.invalid">
                    <i class="fa-solid fa-spinner fa-spin" *ngIf="savingDiet()"></i>
                    <i class="fa-solid fa-floppy-disk" *ngIf="!savingDiet()"></i>
                    {{ diet() ? 'Update Plan' : 'Assign Plan' }}
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

  client          = signal<ClientResponse | null>(null);
  training        = signal<TrainingProgramResponse | null>(null);
  diet            = signal<DietProgramResponse | null>(null);
  loading         = signal(true);
  loadingTraining = signal(false);
  loadingDiet     = signal(false);
  savingTraining  = signal(false);
  savingDiet      = signal(false);
  trainingSaved   = signal(false);
  dietSaved       = signal(false);
  activeTab       = signal<Tab>('overview');
  clientId        = 0;

  tabs = [
    { key: 'overview' as Tab, label: 'Overview',  icon: 'fa-gauge-high' },
    { key: 'training' as Tab, label: 'Training',  icon: 'fa-dumbbell'   },
    { key: 'diet'     as Tab, label: 'Diet Plan', icon: 'fa-bowl-food'  },
  ];

  dayOptions = DAYS.map(d => ({ value: d, label: d.charAt(0) + d.slice(1).toLowerCase() }));

  trainingForm!: FormGroup;
  dietForm!: FormGroup;

  clientName = computed(() => {
    const c = this.client();
    return c ? `${c.firstName} ${c.lastName}` : '…';
  });

  ngOnInit(): void {
    this.clientId = Number(this.route.snapshot.paramMap.get('id'));
    this.buildForms();
    this.api.getClientById(this.clientId).subscribe({
      next: c => {
        this.client.set(c);
        this.loading.set(false);
        if (c.trainingProgramId) this.loadTraining(c.trainingProgramId);
        if (c.dietProgramId)     this.loadDiet(c.dietProgramId);
      },
      error: () => this.loading.set(false)
    });
  }

  setTab(t: Tab): void { this.activeTab.set(t); }

  // ── Load existing programs ────────────────────────────────────────
  private loadTraining(programId: number): void {
    this.loadingTraining.set(true);
    this.api.getTrainingProgram(this.clientId, programId).subscribe({
      next: tp => { this.training.set(tp); this.prefillTrainingForm(tp); this.loadingTraining.set(false); },
      error: ()  => this.loadingTraining.set(false)
    });
  }

  private loadDiet(programId: number): void {
    this.loadingDiet.set(true);
    this.api.getDietProgram(this.clientId, programId).subscribe({
      next: dp => { this.diet.set(dp); this.prefillDietForm(dp); this.loadingDiet.set(false); },
      error: ()  => this.loadingDiet.set(false)
    });
  }

  // ── Form builders ─────────────────────────────────────────────────
  buildForms(): void {
    this.trainingForm = this.fb.group({ days: this.fb.array([]) });
    this.dietForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      days: this.fb.array([])
    });
  }

  private prefillTrainingForm(tp: TrainingProgramResponse): void {
    const daysArray = this.fb.array(
      tp.trainingDays.map(day => this.fb.group({
        day:                       [day.dayOfWeek, Validators.required],
        title:                     [day.title, Validators.required],
        estimatedBurnedCalories:   [day.estimatedBurnedCalories, [Validators.required, Validators.min(0)]],
        exercises: this.fb.array(
          day.exercises.map(ex => this.fb.group({
            title:         [ex.title, Validators.required],
            numberOfSets:  [ex.numberOfSets, [Validators.required, Validators.min(1)]],
            numberOfReps:  [ex.numberOfReps, [Validators.required, Validators.min(1)]],
            restTime:      [ex.restTime, [Validators.required, Validators.min(0)]],
            exerciseNumber:[ex.exerciseNumber],
            exerciseUrl:   [ex.exerciseUrl ?? ''],
          }))
        )
      }))
    );
    this.trainingForm = this.fb.group({ days: daysArray });
  }

  private prefillDietForm(dp: DietProgramResponse): void {
    const daysArray = this.fb.array(
      dp.days.map(day => this.fb.group({
        dayOfWeek:          [day.dayOfWeek, Validators.required],
        totalCaloriesInDay: [day.totalCaloriesInDay, [Validators.required, Validators.min(0)]],
        totalProteinInDay:  [day.totalProteinInDay,  [Validators.required, Validators.min(0)]],
        totalCarbsInDay:    [day.totalCarbsInDay,    [Validators.required, Validators.min(0)]],
        totalFatsInDay:     [day.totalFatsInDay,     [Validators.required, Validators.min(0)]],
        meals: this.fb.array(
          day.meals.map(m => this.fb.group({
            description: [m.description, Validators.required],
            calories:    [m.calories, [Validators.required, Validators.min(0)]],
            protein:     [m.protein,  [Validators.required, Validators.min(0)]],
            carbs:       [m.carbs,    [Validators.required, Validators.min(0)]],
            fats:        [m.fats,     [Validators.required, Validators.min(0)]],
            timeToEat:   [m.timeToEat ? m.timeToEat.substring(0,5) : ''],
          }))
        )
      }))
    );
    this.dietForm.patchValue({ title: dp.title, description: dp.description });
    (this.dietForm as FormGroup).setControl('days', daysArray);
  }

  // ── Form accessors ────────────────────────────────────────────────
  get daysArray()     { return this.trainingForm.get('days') as FormArray; }
  get dietDaysArray() { return this.dietForm.get('days') as FormArray; }
  getExercises(di: number) { return (this.daysArray.at(di) as FormGroup).get('exercises') as FormArray; }
  getDietMeals(di: number) { return (this.dietDaysArray.at(di) as FormGroup).get('meals') as FormArray; }

  // ── Add / remove rows ─────────────────────────────────────────────
  addDay(): void {
    this.daysArray.push(this.fb.group({
      day: ['MONDAY', Validators.required],
      title: ['', Validators.required],
      estimatedBurnedCalories: [0, [Validators.required, Validators.min(0)]],
      exercises: this.fb.array([])
    }));
  }
  removeDay(i: number): void { this.daysArray.removeAt(i); }

  addExercise(di: number): void {
    this.getExercises(di).push(this.fb.group({
      title: ['', Validators.required],
      numberOfSets: [3, [Validators.required, Validators.min(1)]],
      numberOfReps: [10, [Validators.required, Validators.min(1)]],
      restTime:     [60, [Validators.required, Validators.min(0)]],
      exerciseNumber: [this.getExercises(di).length],
      exerciseUrl:    [''],
    }));
  }
  removeExercise(di: number, ei: number): void { this.getExercises(di).removeAt(ei); }

  addDietDay(): void {
    this.dietDaysArray.push(this.fb.group({
      dayOfWeek:          ['MONDAY', Validators.required],
      totalCaloriesInDay: [0, [Validators.required, Validators.min(0)]],
      totalProteinInDay:  [0, [Validators.required, Validators.min(0)]],
      totalCarbsInDay:    [0, [Validators.required, Validators.min(0)]],
      totalFatsInDay:     [0, [Validators.required, Validators.min(0)]],
      meals: this.fb.array([])
    }));
  }
  removeDietDay(i: number): void { this.dietDaysArray.removeAt(i); }

  addMeal(di: number): void {
    this.getDietMeals(di).push(this.fb.group({
      description: ['', Validators.required],
      calories:    [0, [Validators.required, Validators.min(0)]],
      protein:     [0, [Validators.required, Validators.min(0)]],
      carbs:       [0, [Validators.required, Validators.min(0)]],
      fats:        [0, [Validators.required, Validators.min(0)]],
      timeToEat:   [''],
    }));
  }
  removeMeal(di: number, mi: number): void { this.getDietMeals(di).removeAt(mi); }

  // ── Save training ─────────────────────────────────────────────────
  saveTraining(): void {
    if (this.trainingForm.invalid) { this.trainingForm.markAllAsTouched(); return; }
    this.savingTraining.set(true);

    const body: TrainingProgramRequest = {
      trainingDays: this.daysArray.controls.map((dayCtrl, di) => {
        const d = dayCtrl.value;
        return {
          programId:               undefined,
          title:                   d.title,
          day:                     d.day,
          estimatedBurnedCalories: d.estimatedBurnedCalories,
          exercises: this.getExercises(di).controls.map((exCtrl, ei) => {
            const e = exCtrl.value;
            return { title: e.title, numberOfSets: e.numberOfSets, numberOfReps: e.numberOfReps,
                     restTime: e.restTime, exerciseNumber: ei, exerciseUrl: e.exerciseUrl || undefined };
          })
        };
      })
    };

    this.api.assignTrainingProgram(this.clientId, body).subscribe({
      next: () => {
        this.savingTraining.set(false);
        this.trainingSaved.set(true);
        setTimeout(() => this.trainingSaved.set(false), 2500);
        // Reload client to get new programId
        this.api.getClientById(this.clientId).subscribe(c => {
          this.client.set(c);
          if (c.trainingProgramId) this.loadTraining(c.trainingProgramId);
        });
      },
      error: () => this.savingTraining.set(false)
    });
  }

  // ── Delete training ───────────────────────────────────────────────
  deleteTraining(): void {
    const pid = this.client()?.trainingProgramId;
    if (!pid) return;
    this.savingTraining.set(true);
    this.api.deleteTrainingProgram(this.clientId, pid).subscribe({
      next: () => {
        this.training.set(null);
        this.trainingForm = this.fb.group({ days: this.fb.array([]) });
        this.client.update(c => c ? { ...c, trainingProgramId: null } : c);
        this.savingTraining.set(false);
      },
      error: () => this.savingTraining.set(false)
    });
  }

  // ── Save diet ─────────────────────────────────────────────────────
  saveDiet(): void {
    if (this.dietForm.invalid) { this.dietForm.markAllAsTouched(); return; }
    this.savingDiet.set(true);

    const fv = this.dietForm.value;
    const body: DietProgramRequest = {
      title: fv.title,
      description: fv.description,
      days: this.dietDaysArray.controls.map((dayCtrl, di) => {
        const d = dayCtrl.value;
        return {
          dayOfWeek:          d.dayOfWeek,
          totalCaloriesInDay: d.totalCaloriesInDay,
          totalProteinInDay:  d.totalProteinInDay,
          totalCarbsInDay:    d.totalCarbsInDay,
          totalFatsInDay:     d.totalFatsInDay,
          meals: this.getDietMeals(di).controls.map(mCtrl => {
            const m = mCtrl.value;
            return { description: m.description, calories: m.calories, protein: m.protein,
                     carbs: m.carbs, fats: m.fats,
                     timeToEat: m.timeToEat ? m.timeToEat + ':00' : undefined };
          })
        };
      })
    };

    this.api.assignDietProgram(this.clientId, body).subscribe({
      next: () => {
        this.savingDiet.set(false);
        this.dietSaved.set(true);
        setTimeout(() => this.dietSaved.set(false), 2500);
        this.api.getClientById(this.clientId).subscribe(c => {
          this.client.set(c);
          if (c.dietProgramId) this.loadDiet(c.dietProgramId);
        });
      },
      error: () => this.savingDiet.set(false)
    });
  }

  // ── Delete diet ───────────────────────────────────────────────────
  deleteDiet(): void {
    const pid = this.client()?.dietProgramId;
    if (!pid) return;
    this.savingDiet.set(true);
    this.api.deleteDietProgram(this.clientId, pid).subscribe({
      next: () => {
        this.diet.set(null);
        this.dietForm = this.fb.group({
          title: ['', Validators.required], description: ['', Validators.required], days: this.fb.array([])
        });
        this.client.update(c => c ? { ...c, dietProgramId: null } : c);
        this.savingDiet.set(false);
      },
      error: () => this.savingDiet.set(false)
    });
  }
}

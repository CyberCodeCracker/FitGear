import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormBuilder, Validators, ReactiveFormsModule, FormArray, FormGroup } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { MockDataService } from '../../../core/services/mock-data.service';
import { ClientResponse, TrainingDay, DietDay, Exercise, Meal } from '../../../core/models/models';

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
          [breadcrumbs]="[{label:'Clients', route:'/coach/clients'}, {label: clientName}]">
        </app-navbar>

        <main class="flex-1 p-6 space-y-6 animate-fade-in" *ngIf="client">

          <!-- Client header card -->
          <div class="card flex flex-col sm:flex-row items-start sm:items-center gap-5">
            <div class="w-16 h-16 rounded-full bg-gradient-to-br from-accent/30 to-accent/10 flex items-center justify-center text-accent text-2xl font-bold flex-shrink-0">
              {{ client.firstName[0] }}{{ client.lastName[0] }}
            </div>
            <div class="flex-1">
              <h2 class="text-xl font-bold text-white">{{ client.firstName }} {{ client.lastName }}</h2>
              <p class="text-muted">Client #{{ client.id }}</p>
              <div class="flex flex-wrap gap-2 mt-2">
                <span class="badge badge-blue"><i class="fa-solid fa-ruler-vertical"></i> {{ client.height }} cm</span>
                <span class="badge badge-green"><i class="fa-solid fa-weight-scale"></i> {{ client.weight }} kg</span>
                <span class="badge"
                      [class.badge-green]="client.bodyFatPercentage < 18"
                      [class.badge-yellow]="client.bodyFatPercentage >= 18 && client.bodyFatPercentage < 25"
                      [class.badge-red]="client.bodyFatPercentage >= 25">
                  <i class="fa-solid fa-percent"></i> {{ client.bodyFatPercentage }}% BF
                </span>
              </div>
            </div>
            <div class="flex gap-2">
              <button (click)="activeTab.set('training')" class="btn-secondary btn-sm">
                <i class="fa-solid fa-dumbbell"></i> Training
              </button>
              <button (click)="activeTab.set('diet')" class="btn-secondary btn-sm">
                <i class="fa-solid fa-bowl-food"></i> Diet
              </button>
            </div>
          </div>

          <!-- Tabs -->
          <div class="flex gap-1 border border-white/10 rounded-xl p-1 bg-card w-fit">
            <button *ngFor="let t of tabs" (click)="activeTab.set(t.key)"
                    class="px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200"
                    [class.bg-accent]="activeTab() === t.key"
                    [class.text-black]="activeTab() === t.key"
                    [class.text-gray-400]="activeTab() !== t.key"
                    [class.hover:text-white]="activeTab() !== t.key">
              <i class="fa-solid mr-2" [class]="t.icon"></i>{{ t.label }}
            </button>
          </div>

          <!-- OVERVIEW TAB -->
          <ng-container *ngIf="activeTab() === 'overview'">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 animate-slide-up">
              <!-- Training summary -->
              <div class="card space-y-3">
                <h3 class="section-title flex items-center gap-2">
                  <i class="fa-solid fa-dumbbell text-accent"></i> Current Training Program
                </h3>
                <div *ngFor="let day of data.trainingProgram.trainingDays"
                     class="flex items-center justify-between px-3 py-2.5 rounded-lg bg-card-2/60 hover:bg-card-2 transition-colors">
                  <div>
                    <p class="text-sm font-medium text-white">{{ day.dayOfWeek }}</p>
                    <p class="text-xs text-gray-500">{{ day.focus }}</p>
                  </div>
                  <span class="badge badge-green">{{ day.exercises.length }} exercises</span>
                </div>
                <button (click)="activeTab.set('training')" class="btn-ghost btn-sm w-full">
                  <i class="fa-solid fa-pen-to-square"></i> Edit Program
                </button>
              </div>
              <!-- Diet summary -->
              <div class="card space-y-3">
                <h3 class="section-title flex items-center gap-2">
                  <i class="fa-solid fa-bowl-food text-warning"></i> Current Diet Plan
                </h3>
                <div class="px-3 py-2 rounded-lg bg-card-2/60">
                  <p class="text-sm font-semibold text-white">{{ data.dietProgram.title }}</p>
                  <p class="text-xs text-gray-500 mt-0.5">{{ data.dietProgram.description }}</p>
                </div>
                <div *ngFor="let meal of data.dietProgram.days[0].meals"
                     class="flex justify-between items-center px-3 py-2 rounded hover:bg-white/5">
                  <span class="text-sm text-gray-300">{{ meal.name }}</span>
                  <span class="text-sm font-semibold text-warning">{{ meal.calories }} kcal</span>
                </div>
                <button (click)="activeTab.set('diet')" class="btn-ghost btn-sm w-full">
                  <i class="fa-solid fa-pen-to-square"></i> Edit Diet Plan
                </button>
              </div>
            </div>
          </ng-container>

          <!-- TRAINING TAB -->
          <ng-container *ngIf="activeTab() === 'training'">
            <div class="card space-y-6 animate-slide-up">
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
                    <div class="flex items-center justify-between">
                      <div class="flex items-center gap-3">
                        <div class="w-8 h-8 rounded-lg bg-accent/15 flex items-center justify-center">
                          <i class="fa-solid fa-calendar-day text-accent text-xs"></i>
                        </div>
                        <div class="flex gap-3">
                          <input formControlName="dayOfWeek" class="form-input w-32" placeholder="Day">
                          <input formControlName="focus" class="form-input flex-1" placeholder="Focus / muscle group">
                        </div>
                      </div>
                      <button type="button" (click)="removeDay(di)" class="btn-danger btn-sm">
                        <i class="fa-solid fa-trash"></i>
                      </button>
                    </div>

                    <!-- Exercises -->
                    <div formArrayName="exercises" class="space-y-2 pl-4 border-l border-white/10">
                      <div *ngFor="let ex of getExercises(di).controls; let ei = index" [formGroupName]="ei"
                           class="grid grid-cols-12 gap-2 items-center">
                        <input formControlName="name"   class="form-input col-span-4" placeholder="Exercise name">
                        <input formControlName="sets"   class="form-input col-span-2" placeholder="Sets" type="number">
                        <input formControlName="reps"   class="form-input col-span-2" placeholder="Reps" type="number">
                        <input formControlName="weight" class="form-input col-span-3" placeholder="Weight kg" type="number">
                        <button type="button" (click)="removeExercise(di, ei)" class="col-span-1 btn-icon text-danger hover:bg-danger/10">
                          <i class="fa-solid fa-xmark"></i>
                        </button>
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

          <!-- DIET TAB -->
          <ng-container *ngIf="activeTab() === 'diet'">
            <div class="card space-y-6 animate-slide-up">
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
                    <div class="flex items-center justify-between">
                      <div class="flex items-center gap-3">
                        <div class="w-8 h-8 rounded-lg bg-warning/15 flex items-center justify-center">
                          <i class="fa-solid fa-utensils text-warning text-xs"></i>
                        </div>
                        <input formControlName="dayLabel" class="form-input w-52" placeholder="Day label">
                      </div>
                      <button type="button" (click)="removeDietDay(di)" class="btn-danger btn-sm">
                        <i class="fa-solid fa-trash"></i>
                      </button>
                    </div>
                    <div formArrayName="meals" class="space-y-2 pl-4 border-l border-white/10">
                      <div *ngFor="let meal of getDietMeals(di).controls; let mi = index" [formGroupName]="mi"
                           class="grid grid-cols-12 gap-2 items-center">
                        <input formControlName="name"     class="form-input col-span-3" placeholder="Meal">
                        <input formControlName="calories" class="form-input col-span-2" placeholder="kcal" type="number">
                        <input formControlName="protein"  class="form-input col-span-2" placeholder="Prot g" type="number">
                        <input formControlName="carbs"    class="form-input col-span-2" placeholder="Carb g" type="number">
                        <input formControlName="fat"      class="form-input col-span-2" placeholder="Fat g" type="number">
                        <button type="button" (click)="removeMeal(di, mi)" class="col-span-1 btn-icon text-danger hover:bg-danger/10">
                          <i class="fa-solid fa-xmark"></i>
                        </button>
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
  data   = inject(MockDataService);
  route  = inject(ActivatedRoute);
  fb     = inject(FormBuilder);

  client!: ClientResponse;
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

  get clientName(): string { return this.client ? `${this.client.firstName} ${this.client.lastName}` : '…'; }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.client = this.data.clients.find(c => c.id === id) ?? this.data.clients[0];
    this.buildForms();
  }

  buildForms(): void {
    // Training form pre-filled
    this.trainingForm = this.fb.group({
      days: this.fb.array(this.data.trainingProgram.trainingDays.map((d: TrainingDay) => this.fb.group({
        dayOfWeek: [d.dayOfWeek],
        focus:     [d.focus],
        exercises: this.fb.array(d.exercises.map((e: Exercise) => this.fb.group({
          name: [e.name], sets: [e.sets], reps: [e.reps], weight: [e.weight ?? ''],
        }))),
      }))),
    });
    // Diet form pre-filled
    this.dietForm = this.fb.group({
      title:       [this.data.dietProgram.title],
      description: [this.data.dietProgram.description],
      days: this.fb.array(this.data.dietProgram.days.map((d: DietDay) => this.fb.group({
        dayLabel: [d.dayLabel],
        meals: this.fb.array(d.meals.map((m: Meal) => this.fb.group({
          name: [m.name], calories: [m.calories], protein: [m.protein], carbs: [m.carbs], fat: [m.fat],
        }))),
      }))),
    });
  }

  get daysArray()     { return this.trainingForm.get('days') as FormArray; }
  get dietDaysArray() { return this.dietForm.get('days') as FormArray; }

  getExercises(di: number) { return (this.daysArray.at(di) as FormGroup).get('exercises') as FormArray; }
  getDietMeals(di: number) { return (this.dietDaysArray.at(di) as FormGroup).get('meals') as FormArray; }

  addDay()            { this.daysArray.push(this.fb.group({ dayOfWeek: [''], focus: [''], exercises: this.fb.array([]) })); }
  removeDay(i: number)  { this.daysArray.removeAt(i); }
  addExercise(di: number)   { this.getExercises(di).push(this.fb.group({ name: [''], sets: [3], reps: [10], weight: [''] })); }
  removeExercise(di: number, ei: number) { this.getExercises(di).removeAt(ei); }

  addDietDay()          { this.dietDaysArray.push(this.fb.group({ dayLabel: [''], meals: this.fb.array([]) })); }
  removeDietDay(i: number) { this.dietDaysArray.removeAt(i); }
  addMeal(di: number)   { this.getDietMeals(di).push(this.fb.group({ name: [''], calories: [0], protein: [0], carbs: [0], fat: [0] })); }
  removeMeal(di: number, mi: number) { this.getDietMeals(di).removeAt(mi); }

  saveTraining(): void { this.trainingSaved.set(true); setTimeout(() => this.trainingSaved.set(false), 2500); }
  saveDiet():    void  { this.dietSaved.set(true);    setTimeout(() => this.dietSaved.set(false), 2500);    }
}

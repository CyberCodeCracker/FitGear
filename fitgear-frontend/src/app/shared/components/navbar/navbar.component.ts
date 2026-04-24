import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

export interface Breadcrumb { label: string; route?: string; }

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="sticky top-0 z-20 bg-bg/80 backdrop-blur-md border-b border-white/5 px-6 py-3 flex items-center justify-between">
      <!-- Back button + Breadcrumb / title -->
      <div class="flex items-center gap-3">
        <a *ngIf="backRoute" [routerLink]="backRoute"
           class="w-8 h-8 rounded-lg bg-card-2 hover:bg-card-2/80 border border-white/10
                  flex items-center justify-center text-gray-400 hover:text-white transition-all"
           title="Go back">
          <i class="fa-solid fa-arrow-left text-xs"></i>
        </a>
        <div class="flex items-center gap-2">
          <h1 class="text-white font-semibold text-lg">{{ title }}</h1>
          <ng-container *ngIf="breadcrumbs?.length">
            <ng-container *ngFor="let crumb of breadcrumbs; let last = last">
              <i class="fa-solid fa-chevron-right text-gray-600 text-xs"></i>
              <a *ngIf="crumb.route && !last" [routerLink]="crumb.route"
                 class="text-sm text-gray-400 hover:text-white transition-colors">{{ crumb.label }}</a>
              <span *ngIf="!crumb.route || last" class="text-sm text-gray-400">{{ crumb.label }}</span>
            </ng-container>
          </ng-container>
        </div>
      </div>

      <!-- Right slot -->
      <div class="flex items-center gap-3">
        <ng-content></ng-content>
        <div class="w-2 h-2 rounded-full bg-accent animate-pulse" title="Connected"></div>
      </div>
    </header>
  `
})
export class NavbarComponent {
  @Input() title = '';
  @Input() breadcrumbs?: Breadcrumb[];

  /** The back arrow goes to the immediate parent — last breadcrumb that has a route */
  get backRoute(): string | undefined {
    if (!this.breadcrumbs?.length) return undefined;
    // Walk from the end and find the last crumb with a route (skip the final label-only crumb)
    for (let i = this.breadcrumbs.length - 1; i >= 0; i--) {
      if (this.breadcrumbs[i].route) return this.breadcrumbs[i].route;
    }
    return undefined;
  }
}

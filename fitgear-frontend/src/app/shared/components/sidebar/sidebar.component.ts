import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { filter } from 'rxjs/operators';

interface NavLink { label: string; icon: string; route: string; }

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <aside class="fixed inset-y-0 left-0 z-30 flex flex-col bg-card border-r border-white/5
                  transition-all duration-300 ease-in-out"
           [class.w-64]="!collapsed"
           [class.w-16]="collapsed">

      <!-- Logo -->
      <div class="flex items-center gap-3 px-4 py-5 border-b border-white/5 min-h-[65px]">
        <div class="w-8 h-8 rounded-lg bg-accent flex items-center justify-center flex-shrink-0">
          <i class="fa-solid fa-dumbbell text-black text-xs"></i>
        </div>
        <span class="font-bold text-lg gradient-text whitespace-nowrap overflow-hidden transition-all duration-200"
              [class.w-0]="collapsed" [class.opacity-0]="collapsed" [class.w-auto]="!collapsed" [class.opacity-100]="!collapsed">
          FitGear
        </span>
      </div>

      <!-- Role badge -->
      <div class="px-3 py-3 border-b border-white/5" *ngIf="!collapsed">
        <span class="badge" [class.badge-green]="auth.isCoach()" [class.badge-blue]="auth.isClient()">
          <i class="fa-solid" [class.fa-shield-halved]="auth.isCoach()" [class.fa-user]="auth.isClient()"></i>
          {{ auth.isCoach() ? 'Coach Portal' : 'Client Portal' }}
        </span>
      </div>

      <!-- Nav -->
      <nav class="flex-1 overflow-y-auto py-4 space-y-1 px-2">
        <a *ngFor="let link of links"
           [routerLink]="link.route"
           class="sidebar-link"
           [class.active]="isActive(link.route)"
           [title]="collapsed ? link.label : ''">
          <i class="fa-solid text-sm flex-shrink-0 w-5 text-center" [class]="link.icon"></i>
          <span class="truncate transition-all duration-200 whitespace-nowrap overflow-hidden"
                [class.w-0]="collapsed" [class.opacity-0]="collapsed">
            {{ link.label }}
          </span>
        </a>
      </nav>

      <!-- User + Logout -->
      <div class="border-t border-white/5 p-3 space-y-1">
        <div class="flex items-center gap-3 px-2 py-2" *ngIf="!collapsed">
          <div class="w-8 h-8 rounded-full bg-accent/20 flex items-center justify-center flex-shrink-0">
            <span class="text-accent text-xs font-bold">{{ initials }}</span>
          </div>
          <div class="overflow-hidden">
            <p class="text-sm font-medium text-white truncate">{{ auth.user()?.firstName }} {{ auth.user()?.lastName }}</p>
            <p class="text-xs text-gray-500 truncate">{{ auth.user()?.email }}</p>
          </div>
        </div>
        <button (click)="auth.logout()" class="sidebar-link w-full text-danger hover:text-danger hover:bg-danger/10"
                [title]="collapsed ? 'Logout' : ''">
          <i class="fa-solid fa-right-from-bracket text-sm flex-shrink-0 w-5 text-center"></i>
          <span [class.hidden]="collapsed">Logout</span>
        </button>
      </div>

      <!-- Collapse toggle -->
      <button (click)="collapsed = !collapsed"
              class="absolute -right-3 top-20 w-6 h-6 rounded-full bg-card border border-white/10
                     flex items-center justify-center hover:border-accent/40 transition-all">
        <i class="fa-solid text-gray-400 text-xs transition-transform duration-200"
           [class.fa-chevron-left]="!collapsed" [class.fa-chevron-right]="collapsed"></i>
      </button>
    </aside>

    <!-- Spacer to push content right -->
    <div [class.w-64]="!collapsed" [class.w-16]="collapsed" class="flex-shrink-0 transition-all duration-300"></div>
  `
})
export class SidebarComponent implements OnInit {
  auth = inject(AuthService);
  private router = inject(Router);

  collapsed = false;
  currentRoute = '';

  get links(): NavLink[] {
    return this.auth.isCoach() ? [
      { label: 'Dashboard',   icon: 'fa-gauge-high',   route: '/coach/dashboard'  },
      { label: 'My Clients',  icon: 'fa-users',        route: '/coach/clients'    },
    ] : [
      { label: 'Dashboard',   icon: 'fa-gauge-high',   route: '/client/dashboard' },
      { label: 'Coaches',     icon: 'fa-medal',        route: '/client/coaches'   },
      { label: 'Progress',    icon: 'fa-chart-line',   route: '/client/progress'  },
    ];
  }

  get initials(): string {
    const u = this.auth.user();
    if (!u) return '?';
    return ((u.firstName?.[0] ?? '') + (u.lastName?.[0] ?? '')).toUpperCase();
  }

  isActive(route: string): boolean { return this.currentRoute.startsWith(route); }

  ngOnInit(): void {
    this.currentRoute = this.router.url;
    this.router.events.pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: any) => this.currentRoute = e.urlAfterRedirects);
  }
}

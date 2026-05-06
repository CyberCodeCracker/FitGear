import { Component, inject, signal, computed, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { AdminApiService } from '../../../core/services/admin-api.service';
import { AdminUserResponse } from '../../../core/models/models';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarComponent, NavbarComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>
      <div class="flex-1 flex flex-col min-w-0">

        <app-navbar title="User Management">
          <span class="text-muted text-sm">
            {{ totalElements() }} user{{ totalElements() !== 1 ? 's' : '' }}
          </span>
        </app-navbar>

        <main class="flex-1 p-6 space-y-4 animate-fade-in">

          <!-- Filters -->
          <div class="card">
            <div class="flex flex-col sm:flex-row gap-3">

              <!-- Search -->
              <div class="relative flex-1">
                <i class="fa-solid fa-magnifying-glass absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm"></i>
                <input
                  type="text"
                  class="form-input pl-9 w-full"
                  placeholder="Search by name or email…"
                  [ngModel]="searchQuery()"
                  (ngModelChange)="onSearchInput($event)">
                <button *ngIf="searchQuery()"
                        (click)="searchQuery.set('')"
                        class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300">
                  <i class="fa-solid fa-xmark text-sm"></i>
                </button>
              </div>

              <!-- Role dropdown -->
              <div class="relative">
                <i class="fa-solid fa-filter absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm pointer-events-none"></i>
                <select class="form-input pl-9 pr-8 appearance-none cursor-pointer min-w-[160px]"
                        [ngModel]="roleFilter()"
                        (ngModelChange)="onRoleChange($event)">
                  <option value="">All roles</option>
                  <option value="CLIENT">Client</option>
                  <option value="COACH">Coach</option>
                  <option value="ADMIN">Admin</option>
                </select>
                <i class="fa-solid fa-chevron-down absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 text-xs pointer-events-none"></i>
              </div>

              <!-- Active filter chips -->
              <div *ngIf="roleFilter() || searchQuery()" class="flex items-center gap-2 sm:border-l sm:border-white/10 sm:pl-3">
                <span class="text-xs text-muted">{{ filteredUsers().length }} result{{ filteredUsers().length !== 1 ? 's' : '' }}</span>
                <button (click)="clearFilters()" class="btn-ghost btn-sm text-xs">
                  <i class="fa-solid fa-rotate-left"></i> Clear
                </button>
              </div>

            </div>
          </div>

          <!-- Table card -->
          <div class="card space-y-4">

            <!-- Skeleton -->
            <div *ngIf="loading()" class="space-y-2">
              <div *ngFor="let s of [1,2,3,4,5,6,7,8]"
                   class="h-14 bg-card-2 rounded-lg animate-pulse"></div>
            </div>

            <div *ngIf="!loading()" class="table-wrapper">
              <table class="data-table">
                <thead>
                  <tr>
                    <th>User</th>
                    <th>Email</th>
                    <th>Type</th>
                    <th>Status</th>
                    <th>Joined</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let u of filteredUsers()">
                    <!-- Name + initials -->
                    <td>
                      <div class="flex items-center gap-3">
                        <div class="w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0"
                             [ngStyle]="avatarStyle(u.userType)">
                          {{ u.firstName[0] }}{{ u.lastName[0] }}
                        </div>
                        <span class="font-medium text-white">{{ u.firstName }} {{ u.lastName }}</span>
                      </div>
                    </td>

                    <!-- Email -->
                    <td class="text-muted text-sm">{{ u.email }}</td>

                    <!-- Type badge -->
                    <td>
                      <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold"
                            [ngStyle]="typeBadgeStyle(u.userType)">
                        <i class="fa-solid text-[10px]" [ngStyle]="{'color': typeColor(u.userType)}"
                           [class.fa-dumbbell]="u.userType === 'COACH'"
                           [class.fa-user]="u.userType === 'CLIENT'"
                           [class.fa-user-shield]="u.userType === 'ADMIN'"></i>
                        {{ u.userType }}
                      </span>
                    </td>

                    <!-- Status -->
                    <td>
                      <div class="flex flex-col items-start gap-1">
                        <span class="badge"
                              [class.badge-green]="u.accountEnabled"
                              [class.badge-red]="!u.accountEnabled">
                          {{ u.accountEnabled ? 'Enabled' : 'Disabled' }}
                        </span>
                        <span *ngIf="u.accountLocked" class="badge badge-yellow">Locked</span>
                      </div>
                    </td>

                    <!-- Joined date -->
                    <td class="text-muted text-sm">
                      {{ u.createdAt | date:'dd MMM yyyy' }}
                    </td>

                    <!-- Actions -->
                    <td>
                      <div class="flex items-center gap-2">
                        <button (click)="toggleLock(u)"
                                [disabled]="actionId() === u.id"
                                class="btn-ghost btn-sm"
                                [title]="u.accountLocked ? 'Unlock' : 'Lock'">
                          <i class="fa-solid"
                             [class.fa-lock-open]="u.accountLocked"
                             [class.fa-lock]="!u.accountLocked"></i>
                        </button>

                        <button (click)="toggleEnable(u)"
                                [disabled]="actionId() === u.id"
                                class="btn-ghost btn-sm"
                                [title]="u.accountEnabled ? 'Disable' : 'Enable'">
                          <i class="fa-solid"
                             [class.fa-eye-slash]="u.accountEnabled"
                             [class.fa-eye]="!u.accountEnabled"></i>
                        </button>

                        <button (click)="confirmDelete(u)"
                                [disabled]="actionId() === u.id"
                                class="btn-ghost btn-sm text-red-400 hover:text-red-300"
                                title="Delete">
                          <i class="fa-solid fa-trash-can"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>

              <p *ngIf="filteredUsers().length === 0" class="text-center text-muted py-10">
                <i class="fa-solid fa-users text-2xl block mb-2"></i>No users found.
              </p>
            </div>

            <!-- Pagination -->
            <div *ngIf="!loading() && totalPages() > 1"
                 class="flex items-center justify-between pt-2 border-t border-white/5">
              <button class="btn-ghost btn-sm"
                      [disabled]="currentPage() === 0"
                      (click)="goToPage(currentPage() - 1)">
                <i class="fa-solid fa-chevron-left"></i> Prev
              </button>
              <span class="text-muted text-sm">
                Page {{ currentPage() + 1 }} / {{ totalPages() }}
              </span>
              <button class="btn-ghost btn-sm"
                      [disabled]="currentPage() + 1 >= totalPages()"
                      (click)="goToPage(currentPage() + 1)">
                Next <i class="fa-solid fa-chevron-right"></i>
              </button>
            </div>

          </div>

          <!-- Delete confirm modal -->
          <div *ngIf="deleteTarget()"
               class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
            <div class="card max-w-sm w-full mx-4 space-y-4">
              <h3 class="text-white font-bold text-lg flex items-center gap-2">
                <i class="fa-solid fa-triangle-exclamation text-red-400"></i> Delete User
              </h3>
              <p class="text-muted text-sm">
                Are you sure you want to delete
                <span class="text-white font-medium">{{ deleteTarget()?.firstName }} {{ deleteTarget()?.lastName }}</span>?
                This action cannot be undone.
              </p>
              <div class="flex gap-3 justify-end">
                <button class="btn-ghost btn-sm" (click)="deleteTarget.set(null)">Cancel</button>
                <button class="btn-sm bg-red-500/20 text-red-400 border border-red-500/30 hover:bg-red-500/30 rounded-lg px-4"
                        [disabled]="actionId() === deleteTarget()?.id"
                        (click)="deleteUser()">
                  <i class="fa-solid fa-spinner fa-spin" *ngIf="actionId() === deleteTarget()?.id"></i>
                  Delete
                </button>
              </div>
            </div>
          </div>

        </main>
      </div>
    </div>
  `
})
export class AdminUsersComponent implements OnInit, OnDestroy {
  private api     = inject(AdminApiService);
  private destroy$ = new Subject<void>();

  loading       = signal(true);
  users         = signal<AdminUserResponse[]>([]);
  currentPage   = signal(0);
  totalPages    = signal(0);
  totalElements = signal(0);
  actionId      = signal<number | null>(null);
  deleteTarget  = signal<AdminUserResponse | null>(null);

  searchQuery = signal('');
  roleFilter  = signal('');

  // keep filteredUsers as an alias — results now come straight from backend
  filteredUsers = computed(() => this.users());

  private searchInput$ = new Subject<string>();

  onSearchInput(value: string): void {
    this.searchQuery.set(value);
    this.searchInput$.next(value);
  }

  onRoleChange(value: string): void {
    this.roleFilter.set(value);
    this.fetch(0);
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.roleFilter.set('');
    this.fetch(0);
  }

  ngOnInit(): void {
    // debounce text search — wait 1 s of silence then call backend
    this.searchInput$.pipe(
      debounceTime(1000),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => this.fetch(0));

    this.fetch(0);
  }

  ngOnDestroy(): void { this.destroy$.next(); this.destroy$.complete(); }

  fetch(page: number): void {
    this.loading.set(true);
    this.api.getUsers(page, 20, this.searchQuery(), this.roleFilter()).subscribe({
      next: res => {
        this.users.set(res.content);
        this.currentPage.set(res.number);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  goToPage(page: number): void { this.fetch(page); }

  toggleLock(user: AdminUserResponse): void {
    this.actionId.set(user.id);
    this.api.toggleLock(user.id).subscribe({
      next: updated => {
        this.users.update(list => list.map(u => u.id === updated.id ? updated : u));
        this.actionId.set(null);
      },
      error: () => this.actionId.set(null)
    });
  }

  toggleEnable(user: AdminUserResponse): void {
    this.actionId.set(user.id);
    this.api.toggleEnable(user.id).subscribe({
      next: updated => {
        this.users.update(list => list.map(u => u.id === updated.id ? updated : u));
        this.actionId.set(null);
      },
      error: () => this.actionId.set(null)
    });
  }

  confirmDelete(user: AdminUserResponse): void { this.deleteTarget.set(user); }

  deleteUser(): void {
    const target = this.deleteTarget();
    if (!target) return;
    this.actionId.set(target.id);
    this.api.deleteUser(target.id).subscribe({
      next: () => {
        this.users.update(list => list.filter(u => u.id !== target.id));
        this.totalElements.update(n => n - 1);
        this.deleteTarget.set(null);
        this.actionId.set(null);
      },
      error: () => { this.deleteTarget.set(null); this.actionId.set(null); }
    });
  }

  typeColor(type: string): string {
    if (type === 'COACH') return '#4ade80';
    if (type === 'ADMIN') return '#c084fc';
    return '#60a5fa';
  }

  typeBadgeStyle(type: string): Record<string, string> {
    if (type === 'COACH') return { background: 'rgba(34,197,94,0.15)',  color: '#4ade80', border: '1px solid rgba(34,197,94,0.3)'  };
    if (type === 'ADMIN') return { background: 'rgba(168,85,247,0.15)', color: '#c084fc', border: '1px solid rgba(168,85,247,0.3)' };
    return                       { background: 'rgba(59,130,246,0.15)', color: '#60a5fa', border: '1px solid rgba(59,130,246,0.3)'  };
  }

  avatarStyle(type: string): Record<string, string> {
    if (type === 'COACH') return { background: 'rgba(34,197,94,0.2)',  color: '#4ade80' };
    if (type === 'ADMIN') return { background: 'rgba(168,85,247,0.2)', color: '#c084fc' };
    return                       { background: 'rgba(59,130,246,0.2)', color: '#60a5fa' };
  }
}

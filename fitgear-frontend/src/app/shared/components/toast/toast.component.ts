import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed bottom-6 right-6 z-50 flex flex-col gap-3 pointer-events-none">
      <div *ngFor="let t of toast.toasts()"
           class="flex items-center gap-3 px-4 py-3 rounded-xl shadow-xl border text-sm font-medium
                  animate-slide-up pointer-events-auto cursor-pointer max-w-sm"
           [class]="t.type === 'success'
             ? 'bg-accent/15 border-accent/30 text-accent'
             : 'bg-danger/15 border-danger/30 text-danger'"
           (click)="toast.remove(t.id)">
        <i class="fa-solid flex-shrink-0"
           [class.fa-circle-check]="t.type === 'success'"
           [class.fa-circle-exclamation]="t.type === 'error'"></i>
        <span class="flex-1 text-white">{{ t.message }}</span>
        <i class="fa-solid fa-xmark text-gray-400 hover:text-white text-xs flex-shrink-0"></i>
      </div>
    </div>
  `
})
export class ToastComponent {
  toast = inject(ToastService);
}

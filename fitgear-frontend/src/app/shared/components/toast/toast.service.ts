import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private counter = 0;
  toasts = signal<Toast[]>([]);

  success(message: string): void { this.add(message, 'success'); }
  error(message: string): void   { this.add(message, 'error');   }

  private add(message: string, type: 'success' | 'error'): void {
    const id = ++this.counter;
    this.toasts.update(t => [...t, { id, message, type }]);
    setTimeout(() => this.remove(id), 4000);
  }

  remove(id: number): void {
    this.toasts.update(t => t.filter(x => x.id !== id));
  }
}

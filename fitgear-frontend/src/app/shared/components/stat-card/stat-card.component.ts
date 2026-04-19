import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="stat-card animate-fade-in">
      <div class="flex items-start justify-between">
        <div class="flex-1">
          <p class="stat-label">{{ label }}</p>
          <p class="stat-value mt-1">{{ value }}<span *ngIf="unit" class="text-sm font-normal text-gray-400 ml-1">{{ unit }}</span></p>
        </div>
        <div class="w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0"
             [style.background]="iconBg">
          <i class="fa-solid text-lg" [class]="icon" [style.color]="iconColor"></i>
        </div>
      </div>
      <div *ngIf="change !== undefined" class="flex items-center gap-1 mt-2">
        <span [class]="change >= 0 ? 'stat-change-pos' : 'stat-change-neg'">
          <i class="fa-solid text-xs" [class.fa-arrow-up]="change >= 0" [class.fa-arrow-down]="change < 0"></i>
          {{ Math.abs(change) }}{{ changeUnit }}
        </span>
        <span class="text-xs text-gray-500">vs last month</span>
      </div>
    </div>
  `
})
export class StatCardComponent {
  @Input() label  = '';
  @Input() value: string | number = '';
  @Input() unit   = '';
  @Input() icon   = 'fa-circle';
  @Input() iconColor = '#22C55E';
  @Input() iconBg    = 'rgba(34,197,94,0.15)';
  @Input() change?: number;
  @Input() changeUnit = '';
  Math = Math;
}

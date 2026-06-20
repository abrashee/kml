// // src/app/shared/components/alert.ts
import { Component, inject } from '@angular/core';
import { AlertService } from '../../../core/services/alert.service';

@Component({
  selector: 'app-alert',
  standalone: true,
  template: `
    <div class="alert-container">
      @for (alert of alertService.alerts(); track alert.id) {
        <div class="alert alert-{{ alert.type }}">
          <span>{{ alert.message }}</span>
          <button class="alert-close" (click)="alertService.dismiss(alert.id)">×</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .alert-container {
      position: fixed; top: 16px; right: 16px; z-index: 9999;
      display: flex; flex-direction: column; gap: 8px; max-width: 400px;
    }
    .alert {
      display: flex; align-items: center; justify-content: space-between;
      padding: 12px 16px; border-radius: 6px; font-size: 14px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    }
    .alert-error   { background: #fee2e2; color: #991b1b; border: 1px solid #fca5a5; }
    .alert-success { background: #dcfce7; color: #166534; border: 1px solid #86efac; }
    .alert-warning { background: #fef3c7; color: #92400e; border: 1px solid #fcd34d; }
    .alert-info    { background: #dbeafe; color: #1e40af; border: 1px solid #93c5fd; }
    .alert-close {
      background: none; border: none; cursor: pointer;
      font-size: 18px; line-height: 1; padding: 0 0 0 12px; opacity: 0.7;
    }
    .alert-close:hover { opacity: 1; }
  `]
})
export class AlertComponent {
  protected alertService = inject(AlertService);
}

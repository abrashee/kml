// // src/app/shared/components/empty-state.ts
import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  template: `
    <div class="empty-state">
      <div class="empty-icon">📭</div>
      <p class="empty-message">{{ message }}</p>
      @if (showRetry) {
        <button class="btn btn-outline" (click)="retry.emit()">Retry</button>
      }
    </div>
  `,
  styles: [`
    .empty-state { display: flex; flex-direction: column; align-items: center; padding: 60px 20px; }
    .empty-icon  { font-size: 48px; margin-bottom: 16px; }
    .empty-message { color: var(--text-muted); margin: 0 0 16px; font-size: 16px; }
  `]
})
export class EmptyStateComponent {
  @Input() message = 'No data found';
  @Input() showRetry = false;
  @Output() retry = new EventEmitter<void>();
}

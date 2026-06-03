import { Component, Input, Output, EventEmitter, signal } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  template: `
    @if (open()) {
      <div class="dialog-overlay" (click)="cancel()">
        <div class="dialog-box" (click)="$event.stopPropagation()">
          <h3 class="dialog-title">{{ title }}</h3>
          <p class="dialog-message">{{ message }}</p>
          <div class="dialog-actions">
            <button class="btn btn-outline" (click)="cancel()">Cancel</button>
            <button class="btn btn-danger" (click)="confirm()">Confirm</button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .dialog-overlay {
      position: fixed; inset: 0; background: rgba(0,0,0,0.5);
      display: flex; align-items: center; justify-content: center; z-index: 1000;
    }
    .dialog-box { background: white; border-radius: 8px; padding: 24px; max-width: 400px; width: 90%; }
    .dialog-title   { margin: 0 0 12px; font-size: 18px; }
    .dialog-message { margin: 0 0 24px; color: var(--text-muted); }
    .dialog-actions { display: flex; gap: 8px; justify-content: flex-end; }
  `]
})
export class ConfirmDialogComponent {
  @Input() title = 'Confirm';
  @Input() message = 'Are you sure?';
  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  open = signal(false);

  show(): void { this.open.set(true); }

  confirm(): void {
    this.open.set(false);
    this.confirmed.emit();
  }

  cancel(): void {
    this.open.set(false);
    this.cancelled.emit();
  }
}

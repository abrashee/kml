import { Injectable, signal } from '@angular/core';

export interface AlertMessage {
  id: number;
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
}

@Injectable({ providedIn: 'root' })
export class AlertService {
  alerts = signal<AlertMessage[]>([]);
  private nextId = 0;

  show(message: string, type: AlertMessage['type'] = 'error') {
    const id = ++this.nextId;
    this.alerts.update(a => [...a, { id, type, message }]);
    setTimeout(() => this.dismiss(id), 5000);
  }

  dismiss(id: number) {
    this.alerts.update(a => a.filter(x => x.id !== id));
  }
}

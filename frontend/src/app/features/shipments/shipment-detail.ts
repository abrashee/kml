import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ShipmentService } from '../../core/services/shipment.service';
import { AuthService } from '../../core/auth/auth.service';
import { Shipment, ShipmentHistory } from '../../core/models/shipment.model';
import { DateFormatPipe } from '../../shared/pipes/date-format.pipe';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner';

@Component({
  selector: 'app-shipment-detail',
  standalone: true,
  imports: [RouterLink, FormsModule, DateFormatPipe, LoadingSpinnerComponent],
  template: `
    <div class="page-header">
      <h1>Shipment Detail</h1>
      <a routerLink="/shipments" class="btn btn-outline">← Back</a>
    </div>

    @if (loading()) {
      <app-loading-spinner />
    } @else if (shipment(); as s) {
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px">
        <div class="card">
          <h3 style="margin:0 0 16px">Shipment Info</h3>
          <dl class="detail-list">
            <dt>Tracking #</dt>    <dd>{{ s.trackingNumber }}</dd>
            <dt>Status</dt>        <dd><span class="badge badge-info">{{ s.status }}</span></dd>
            <dt>Carrier</dt>       <dd>{{ s.carrier }}</dd>
            <dt>Recipient</dt>     <dd>{{ s.recipientAddress }}</dd>
            <dt>Order Code</dt>    <dd>{{ s.orderCode }}</dd>
            <dt>Created</dt>       <dd>{{ s.createdAt | dateFormat }}</dd>
          </dl>

          @if (canManage()) {
            <div style="margin-top:20px">
              <label class="form-label">Update Status</label>
              <div style="display:flex;gap:8px">
                <select class="form-control" [(ngModel)]="newStatus">
                  @for (st of statusOptions; track st) { <option [value]="st">{{ st }}</option> }
                </select>
                <button class="btn btn-primary" (click)="updateStatus()">Update</button>
              </div>
            </div>
          }
        </div>

        <div class="card">
          <h3 style="margin:0 0 16px">History</h3>
          @for (h of history(); track h.id) {
            <div class="timeline-item">
              <div class="timeline-status">{{ h.status }}</div>
              <div class="timeline-note">{{ h.note }}</div>
              <div class="timeline-date">{{ h.createdAt | dateFormat }}</div>
            </div>
          }
          @if (!history().length) {
            <p style="color:var(--text-muted)">No history yet.</p>
          }
        </div>
      </div>
    }
  `,
  styles: [`
    .detail-list { display: grid; grid-template-columns: 130px 1fr; gap: 8px; font-size: 14px; }
    dt { font-weight: 600; color: var(--text-muted); font-size: 13px; }
    dd { margin: 0; }
    .timeline-item { padding: 12px 0; border-bottom: 1px solid var(--border); }
    .timeline-item:last-child { border-bottom: none; }
    .timeline-status { font-weight: 600; font-size: 13px; color: var(--primary); }
    .timeline-note   { font-size: 14px; margin: 4px 0; }
    .timeline-date   { font-size: 12px; color: var(--text-muted); }
  `]
})
export class ShipmentDetailComponent implements OnInit {
  private shipmentService = inject(ShipmentService);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);

  loading = signal(true);
  shipment = signal<Shipment | null>(null);
  history = signal<ShipmentHistory[]>([]);
  newStatus = '';
  statusOptions = ['PENDING', 'IN_TRANSIT', 'DELIVERED', 'RETURNED', 'CANCELLED'];

  canManage(): boolean {
    const role = this.authService.currentUser()?.role;
    return role === 'ADMIN' || role === 'MANAGER';
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.shipmentService.getById(id).subscribe({
      next: s => { this.shipment.set(s); this.newStatus = s.status; this.loading.set(false); },
      error: () => this.loading.set(false)
    });
    this.shipmentService.getHistory(id).subscribe(h => this.history.set(h));
  }

  updateStatus(): void {
    const id = this.shipment()?.id;
    if (!id) return;
    this.shipmentService.updateStatus(id, this.newStatus).subscribe(s => this.shipment.set(s));
  }
}

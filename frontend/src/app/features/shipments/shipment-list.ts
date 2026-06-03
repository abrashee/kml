import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ShipmentService } from '../../core/services/shipment.service';
import { Shipment } from '../../core/models/shipment.model';
import { Page } from '../../core/models/user.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state';
import { DateFormatPipe } from '../../shared/pipes/date-format.pipe';

@Component({
  selector: 'app-shipment-list',
  standalone: true,
  imports: [RouterLink, FormsModule, LoadingSpinnerComponent, EmptyStateComponent, DateFormatPipe],
  template: `
    <div class="page-header">
      <h1>Shipments</h1>
    </div>

    <div class="card">
      <div class="search-bar">
        <select class="form-control" [(ngModel)]="statusFilter" (ngModelChange)="load()" style="max-width:200px">
          <option value="">All Statuses</option>
          @for (s of statusOptions; track s) { <option [value]="s">{{ s }}</option> }
        </select>
      </div>

      @if (loading()) {
        <app-loading-spinner />
      } @else if (!page()?.content?.length) {
        <app-empty-state message="No shipments found" [showRetry]="true" (retry)="load()" />
      } @else {
        <table>
          <thead>
            <tr>
              <th>Tracking #</th>
              <th>Recipient</th>
              <th>Status</th>
              <th>Carrier</th>
              <th>Order Code</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (s of page()!.content; track s.id) {
              <tr>
                <td>{{ s.trackingNumber }}</td>
                <td>{{ s.recipientAddress }}</td>
                <td><span class="badge badge-info">{{ s.status }}</span></td>
                <td>{{ s.carrier }}</td>
                <td>{{ s.orderCode }}</td>
                <td>{{ s.createdAt | dateFormat }}</td>
                <td>
                  <a [routerLink]="['/shipments', s.id]" class="btn btn-outline btn-sm">View</a>
                </td>
              </tr>
            }
          </tbody>
        </table>

        <div class="pagination">
          <button class="btn btn-outline btn-sm" [disabled]="currentPage() === 0" (click)="goPage(currentPage() - 1)">Prev</button>
          <span>Page {{ currentPage() + 1 }} of {{ page()?.totalPages ?? 1 }}</span>
          <button class="btn btn-outline btn-sm" [disabled]="currentPage() >= (page()?.totalPages ?? 1) - 1" (click)="goPage(currentPage() + 1)">Next</button>
        </div>
      }
    </div>
  `
})
export class ShipmentListComponent implements OnInit {
  private shipmentService = inject(ShipmentService);

  loading = signal(true);
  page = signal<Page<Shipment> | null>(null);
  currentPage = signal(0);
  statusFilter = '';
  statusOptions = ['PENDING', 'IN_TRANSIT', 'DELIVERED', 'RETURNED', 'CANCELLED'];

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.shipmentService.getAll(this.currentPage(), 20, this.statusFilter).subscribe({
      next: p => { this.page.set(p); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  goPage(p: number): void { this.currentPage.set(p); this.load(); }
}

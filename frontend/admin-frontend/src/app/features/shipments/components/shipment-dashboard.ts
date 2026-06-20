import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ShipmentService } from '../services/shipment.service';
import { Shipment, ShipmentStatus } from '../models/shipment.model';
import { Page } from '../../users/models/user.model';

@Component({
  selector: 'app-shipment-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-header" style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px;">
      <div>
        <h1>Shipment Dashboard</h1>
        <p class="text-muted">Read-only shipment tracking and manifest status overview.</p>
      </div>
      <button class="btn btn-outline" (click)="loadShipments()">↻ Refresh</button>
    </div>

    <div class="filters-barcard">
      <div class="search-input-wrapper">
        <input
          type="text"
          class="form-control"
          placeholder="Search by tracking, address, or order ref..."
          [ngModel]="searchQuery()"
          (ngModelChange)="onSearchChange($event)"
        />
      </div>
      <div class="filter-select-wrapper">
        <select class="form-control" [ngModel]="statusFilter()" (ngModelChange)="onStatusChange($event)">
          <option [ngValue]="null">All Statuses</option>
          <option value="PENDING">Pending</option>
          <option value="IN_TRANSIT">In Transit</option>
          <option value="DELIVERED">Delivered</option>
          <option value="RETURNED">Returned</option>
        </select>
      </div>
    </div>

    <div class="card">
      @if (loading()) {
        <div class="state">Loading shipment records...</div>
      } @else if (!page()?.content?.length) {
        <div class="empty">No shipments match the current filters.</div>
      } @else {
        <table class="data-table">
          <thead>
            <tr>
              <th>Shipment ID</th>
              <th>Tracking</th>
              <th>Destination</th>
              <th>Status</th>
              <th>Order Ref</th>
              <th>Facility</th>
              <th>Updated</th>
            </tr>
          </thead>
          <tbody>
            @for (s of page()!.content; track s.id) {
              <tr>
                <td class="mono-id">{{ s.id }}</td>
                <td class="fw-bold">{{ s.tracking }}</td>
                <td>{{ s.address }}</td>
                <td>
                  <span class="status-badge" [class]="s.status.toLowerCase()">{{ s.status }}</span>
                </td>
                <td>{{ s.orderId ? '#' + s.orderId : 'N/A' }}</td>
                <td>{{ s.carrierInfo || 'Unassigned Carrier' }}</td>
                <td>{{ s.updatedAt | date:'short' }}</td>
              </tr>
            }
          </tbody>
        </table>
      }
    </div>

    @if ((page()?.totalPages ?? 0) > 1) {
      <div class="pagination-footer">
        <span class="page-indicator">Page {{ currentPage() + 1 }} of {{ page()?.totalPages || 1 }}</span>
        <div class="pagination-buttons">
          <button class="btn btn-outline" [disabled]="currentPage() === 0" (click)="changePage(-1)">Prev</button>
          <button class="btn btn-outline" [disabled]="currentPage() + 1 >= (page()?.totalPages || 1)" (click)="changePage(1)">Next</button>
        </div>
      </div>
    }
  `,
  styles: [
    `
      .card { background: white; border: 1px solid var(--border); border-radius: 12px; padding: 16px; }
      .filters-barcard { background: white; border: 1px solid var(--border); border-radius: 12px; padding: 14px; display: flex; gap: 16px; margin-bottom: 16px; }
      .search-input-wrapper { flex: 2; }
      .filter-select-wrapper { flex: 1; min-width: 180px; max-width: 260px; }
      .form-control { width: 100%; padding: 10px 12px; border: 1px solid var(--border); border-radius: 10px; font-size: 14px; }
      .data-table { width: 100%; border-collapse: collapse; text-align: left; }
      .data-table th, .data-table td { padding: 14px 12px; border-bottom: 1px solid var(--border); vertical-align: middle; }
      .data-table thead { background: #f8fafc; }
      .mono-id { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace; font-size: 13px; color: #0f172a; }
      .fw-bold { font-weight: 700; }
      .status-badge { display: inline-flex; align-items: center; justify-content: center; min-width: 90px; padding: 6px 10px; border-radius: 999px; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.02em; }
      .status-badge.pending { background: #fef3c7; color: #92400e; }
      .status-badge.in_transit { background: #eff6ff; color: #1d4ed8; }
      .status-badge.delivered { background: #dcfce7; color: #166534; }
      .status-badge.returned { background: #fee2e2; color: #991b1b; }
      .page-indicator { font-size: 13px; color: #64748b; }
      .pagination-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 16px; }
      .pagination-buttons { display: flex; gap: 10px; }
      .btn-outline { background: transparent; border: 1px solid #cbd5e1; color: #1f2937; border-radius: 10px; padding: 8px 14px; cursor: pointer; }
      .btn-outline:disabled { opacity: 0.45; cursor: not-allowed; }
      .state, .empty { text-align: center; padding: 32px; color: #64748b; }
      .text-muted { color: #64748b; font-size: 13px; margin: 8px 0 0 0; }
    `
  ]
})
export class ShipmentDashboardComponent implements OnInit {
  private shipmentService = inject(ShipmentService);

  loading = signal(true);
  page = signal<Page<Shipment> | null>(null);
  statusFilter = signal<ShipmentStatus | null>(null);
  searchQuery = signal<string>('');
  currentPage = signal<number>(0);
  debouncedSearchQuery = signal<string>('');
  private searchDebounceTimer: any;

  ngOnInit(): void {
    this.loadShipments();
  }

  loadShipments(): void {
    this.loading.set(true);
    this.shipmentService.getShipments(
      this.currentPage(),
      25,
      this.statusFilter() || undefined,
      this.debouncedSearchQuery() || undefined
    ).subscribe({
      next: (p) => {
        this.page.set(p);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Dashboard out-of-sync:', err);
        this.loading.set(false);
      }
    });
  }

  onStatusChange(status: ShipmentStatus | null): void {
    this.statusFilter.set(status);
    this.currentPage.set(0);
    this.loadShipments();
  }

  onSearchChange(query: string): void {
    this.searchQuery.set(query);
    this.currentPage.set(0);

    clearTimeout(this.searchDebounceTimer);
    this.searchDebounceTimer = setTimeout(() => {
      this.debouncedSearchQuery.set(query);
      this.loadShipments();
    }, 500);
  }

  changePage(delta: number): void {
    const nextPage = this.currentPage() + delta;
    if (nextPage >= 0 && nextPage < (this.page()?.totalPages || 1)) {
      this.currentPage.set(nextPage);
      this.loadShipments();
    }
  }
}

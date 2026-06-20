// src/app/features/dashboards/compoenents/worker-dashboard.component.ts
import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';
import { WarehouseService } from '../../warehouses/services/warehouse.service';
import { Warehouse, StorageUnit } from '../../warehouses/models/warehouse.model';

import { switchMap, catchError, of, forkJoin, map } from 'rxjs';

@Component({
  selector: 'app-worker-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-header">
      <h1>Welcome back, {{ userName() }}</h1>
      <p class="text-muted">Your daily operational dashboard.</p>
    </div>

    @if (loading()) {
      <div class="state-loading">Loading your assignment coordinates...</div>
    } @else {

      @if (!warehouse()) {
        <div class="card alert-card warning">
          <div class="alert-icon">⏳</div>
          <div class="alert-content">
            <h3>Awaiting Assignment</h3>
            <p>Your account is active, but you have not been assigned to a warehouse yet. A logistics manager will review your profile shortly.</p>
          </div>
        </div>
      }
      @else {
        <div class="assignment-grid">

          <div class="card assignment-card">
            <h3 class="card-title">Facility Location</h3>
            <div class="metric-value">{{ warehouse()?.name }}</div>
            <p class="text-muted">{{ warehouse()?.location }}</p>
            <p class="text-muted">{{ warehouse()?.address }}</p>
          </div>

          <div class="card assignment-card" [class.highlight]="!assignedUnit()">
            <h3 class="card-title">Storage Zone</h3>
            @if (assignedUnit()) {
              <!-- <div class="metric-value text-success">{{ assignedUnit()?.name }}</div> -->
              <p class="text-muted">Zone Capacity: {{ assignedUnit()?.capacity }} units</p>
              <div class="status-badge mt-2">Status: {{ assignedUnit()?.status }}</div>
            } @else {
              <div class="metric-value text-pending">Pending Zone</div>
              <p class="text-muted">You are stationed at {{ warehouse()?.name }}, but have not been pinned to a specific storage rack.</p>
            }
          </div>

        </div>

        <h2 class="section-title">Quick Actions</h2>
        <div class="action-grid">
          <button class="btn btn-action" [disabled]="!assignedUnit()">
            <span class="icon">📦</span>
            Log Incoming Inventory
          </button>
          <button class="btn btn-action" [disabled]="!assignedUnit()">
            <span class="icon">📋</span>
            View Picking Tasks
          </button>
          <button class="btn btn-action" [disabled]="!assignedUnit()">
            <span class="icon">⚠️</span>
            Report Maintenance
          </button>
        </div>
      }
    }
  `,
  styles: [`
    .assignment-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 24px; margin-bottom: 32px; }
    .assignment-card { padding: 24px; border: 1px solid var(--border); border-radius: 8px; background: white; }
    .assignment-card.highlight { border-color: #f59e0b; background: #fffbeb; }

    .card-title { font-size: 14px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.05em; margin: 0 0 12px 0; }
    .metric-value { font-size: 24px; font-weight: 700; color: #0f172a; margin-bottom: 4px; }
    .text-success { color: #059669; }
    .text-pending { color: #d97706; }
    .text-muted { color: #64748b; font-size: 14px; margin: 0; }

    .alert-card { display: flex; gap: 16px; padding: 24px; border-radius: 8px; align-items: flex-start; }
    .alert-card.warning { background: #fffbeb; border: 1px solid #fcd34d; }
    .alert-icon { font-size: 24px; }
    .alert-content h3 { margin: 0 0 8px 0; color: #92400e; font-size: 18px; }
    .alert-content p { margin: 0; color: #b45309; line-height: 1.5; }

    .status-badge { display: inline-block; background: #e2e8f0; color: #475569; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: bold; }
    .mt-2 { margin-top: 12px; }

    .section-title { font-size: 18px; margin: 0 0 16px 0; color: #334155; }
    .action-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; }
    .btn-action { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 24px; background: white; border: 1px solid var(--border); border-radius: 8px; cursor: pointer; transition: all 0.2s; font-weight: 500; color: #334155; }
    .btn-action:hover:not(:disabled) { border-color: #3b82f6; color: #3b82f6; box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1); }
    .btn-action:disabled { opacity: 0.5; cursor: not-allowed; background: #f8fafc; }
    .btn-action .icon { font-size: 28px; margin-bottom: 12px; }
  `]
})
export class WorkerDashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private warehouseService = inject(WarehouseService);

  loading = signal(true);

  // Hydrated entity state
  warehouse = signal<Warehouse | null>(null);
  assignedUnit = signal<StorageUnit | null>(null);

  // Derived user state
  userName = computed(() => this.authService.currentUser()?.name || 'Worker');
  private userContext = computed(() => this.authService.currentUser() as any);

  ngOnInit(): void {
    const context = this.userContext();

    if (!context || !context.warehouseId) {
      // User has no warehouse assignment yet.
      this.loading.set(false);
      return;
    }

    this.hydrateLogistics(context.warehouseId, context.storageId);
  }

private hydrateLogistics(warehouseId: number, storageId?: number): void {
    this.loading.set(true);

    this.warehouseService.getById(warehouseId).pipe(
      switchMap(wh => {
        // If there's a storageId, fetch units alongside the warehouse
        if (storageId) {
          return this.warehouseService.getStorageUnits(warehouseId).pipe(
            map(units => {
              const unit = units.find(u => u.id === storageId) || null;
              return { warehouse: wh, unit: unit };
            })
          );
        }
        // Otherwise, just return the warehouse with no assigned unit
        return of({ warehouse: wh, unit: null });
      }),
      catchError(() => {
        // Handle errors gracefully without breaking the stream
        return of({ warehouse: null, unit: null });
      })
    ).subscribe({
      next: (result) => {
        if (result.warehouse) this.warehouse.set(result.warehouse);
        if (result.unit) this.assignedUnit.set(result.unit);
        this.loading.set(false);
      }
    });
  }
}

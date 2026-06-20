import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormsModule } from '@angular/forms';
import { OrderService, Order } from '../services/order.service';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-order-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  template: `
    <div class="page-header" style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px;">
      <div>
        <h1>Order Pipeline</h1>
        <p class="text-muted">Fulfillment tracking and worker assignment.</p>
      </div>
      @if (canFilterAndRefresh()) {
        <button class="btn btn-outline" (click)="loadOrders()">↻ Refresh Pipeline</button>
      }
    </div>

    @if (showAssignPanel()) {
      <div class="card panel-container">
        <h3 style="margin-top: 0;">Assign Worker to Order: {{ editingOrder()?.code }}</h3>
        <form [formGroup]="assignForm" (ngSubmit)="onAssignSubmit()">
          <div style="max-width: 300px;">
            <label class="form-label">Worker ID *</label>
            <input type="number" class="form-control" formControlName="workerId" placeholder="Enter Worker ID..." />
          </div>
          <div style="margin-top: 20px; display: flex; gap: 8px;">
            <button type="submit" class="btn btn-primary" [disabled]="assignForm.invalid || loading()">Confirm Assignment</button>
            <button type="button" class="btn btn-outline" (click)="closeAssignPanel()">Cancel</button>
          </div>
        </form>
      </div>
    }

    @if (canFilterAndRefresh()) {
      <div class="filters-barcard">
        <div class="search-input-wrapper">
          <input
            type="text"
            class="form-control"
            placeholder="Search by Order Code..."
            [(ngModel)]="searchQuery"
            (input)="triggerSearch()"
          />
        </div>

        <div class="filter-select-wrapper">
          <select class="form-control" [(ngModel)]="selectedStatus" (change)="loadOrders()">
            <option [ngValue]="null">All Statuses</option>
            <option value="PENDING">Pending</option>
            <option value="PROCESSING">Processing</option>
            <option value="SHIPPED">Shipped</option>
          </select>
        </div>
      </div>
    }

    <div class="card">
      @if (loading()) {
        <div class="state">Syncing pipeline...</div>
      } @else if (!orders().length) {
        <div class="empty">No active orders match the current criteria.</div>
      } @else {
        <table class="data-table">
          <thead>
            <tr>
              <th>Order ID</th>
              <th>Code</th>
              <th>Status</th>
              <th>Facility</th>
              <th>Assigned Worker</th>
              <th>Date Created</th>
              @if (canAssignWorker()) {
                <th>Actions</th>
              }
            </tr>
          </thead>
          <tbody>
            @for (order of orders(); track order.id) {
              <tr>
                <td class="sku-text">ID: {{ order.id }}</td>
                <td class="fw-bold">{{ order.code }}</td>
                <td>
                  <span class="status-badge" [class]="(order.statusName || 'pending').toLowerCase()">
                    {{ order.statusName || 'N/A' }}
                  </span>
                </td>
                <td>{{ order.warehouseId ? 'WH-' + order.warehouseId : 'Unassigned Location' }}</td>
                <td>
                  @if (order.assignedWorkerId) {
                    Worker #{{ order.assignedWorkerId }}
                  } @else {
                    <span class="text-muted">Unassigned</span>
                  }
                </td>
                <td>{{ order.createdAt | date:'short' }}</td>

                @if (canAssignWorker()) {
                  <td>
                    <button class="btn btn-sm btn-outline" (click)="openAssignMode(order)">
                      {{ order.assignedWorkerId ? 'Reassign' : 'Assign' }}
                    </button>
                  </td>
                }
              </tr>
            }
          </tbody>
        </table>
      }
    </div>
  `,
  styles: [`
    .panel-container { background: #f8fafc; border-left: 4px solid #3b82f6; margin-bottom: 24px; padding: 20px; }
    .form-label { display: block; margin-bottom: 6px; font-weight: 500; font-size: 13px; color: #475569; }
    .form-control { width: 100%; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px; box-sizing: border-box; font-size: 14px; }
    .filters-barcard { background: white; border: 1px solid var(--border); border-radius: 8px; padding: 12px; display: flex; gap: 16px; margin-bottom: 16px; }
    .search-input-wrapper { flex: 2; }
    .filter-select-wrapper { flex: 1; min-width: 150px; max-width: 250px; }
    .data-table { width: 100%; border-collapse: collapse; text-align: left; }
    .data-table th, .data-table td { padding: 12px; border-bottom: 1px solid var(--border); vertical-align: middle; }
    .sku-text { font-family: monospace; font-weight: bold; color: #0f172a; background: #f1f5f9; padding: 4px 8px; border-radius: 4px; font-size: 13px; }
    .fw-bold { font-weight: 600; }
    .text-muted { color: #64748b; font-size: 12px; }
    .state, .empty { text-align: center; padding: 24px; color: #64748b; }
    .status-badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; text-transform: uppercase; }
    .status-badge.pending { background: #fffbeb; color: #b45309; }
    .status-badge.processing { background: #eff6ff; color: #1d4ed8; }
    .status-badge.shipped { background: #f0fdf4; color: #15803d; }
  `]
})
export class OrderDashboardComponent implements OnInit {
  private fb = inject(FormBuilder);
  private orderService = inject(OrderService);
  private authService = inject(AuthService);

  orders = signal<Order[]>([]);
  loading = signal(true);

  showAssignPanel = signal(false);
  editingOrder = signal<Order | null>(null);

  searchQuery = '';
  selectedStatus: string | null = null;

  private filterWarehouseId: number | null = null;
  private filterWorkerId: number | null = null;

  private userContext = computed(() => this.authService.currentUser() as any);
  userRole = computed(() => this.userContext()?.role || this.userContext()?.userRole);

  isAdmin = computed(() => this.userRole() === 'ADMIN');
  isManager = computed(() => this.userRole() === 'MANAGER');
  isWorker = computed(() => this.userRole() === 'WORKER');

  canFilterAndRefresh = computed(() => this.isAdmin() || this.isManager() || this.isWorker());
  canAssignWorker = computed(() => this.isAdmin() || this.isManager());

  assignForm = this.fb.group({
    workerId: [null as number | null, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    // Structural client-side boundary enforcement
    if (this.isManager()) {
      this.filterWarehouseId = this.userContext()?.warehouseId || null;
    } else if (this.isWorker()) {
      this.filterWarehouseId = this.userContext()?.warehouseId || null;
      this.filterWorkerId = this.userContext()?.id || null;
    }

    this.loadOrders();
  }

  loadOrders(): void {
    this.loading.set(true);

    this.orderService.getOrders(
      0, 50,
      this.filterWarehouseId,
      this.filterWorkerId,
      this.searchQuery.trim() || undefined,
      this.selectedStatus
    ).subscribe({
      next: (response: any) => {
        void response;
        const items = response?.content || response || [];
        this.orders.set(items);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load anonymized pipeline records', err);
        this.loading.set(false);
      }
    });
  }

  private searchTimeout: any;
  triggerSearch(): void {
    if (this.searchTimeout) clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => this.loadOrders(), 300);
  }

  openAssignMode(order: Order): void {
    this.editingOrder.set(order);
    this.assignForm.reset({ workerId: order.assignedWorkerId || null });
    this.showAssignPanel.set(true);
  }

  closeAssignPanel(): void {
    this.showAssignPanel.set(false);
    this.editingOrder.set(null);
  }

  onAssignSubmit(): void {
    const order = this.editingOrder();
    if (this.assignForm.invalid || !order) return;

    this.loading.set(true);
    const workerId = this.assignForm.value.workerId!;

    this.orderService.assignWorker(order.id, workerId).subscribe({
      next: () => {
        this.loadOrders();
        this.closeAssignPanel();
      },
      error: () => this.loading.set(false)
    });
  }
}

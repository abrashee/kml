import { Component, inject, signal, OnInit } from '@angular/core';
import { InventoryService } from '../../core/services/inventory.service';
import { OrderService } from '../../core/services/order.service';
import { ShipmentService } from '../../core/services/shipment.service';
import { WarehouseService } from '../../core/services/warehouse.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <div class="page-header">
      <h1>Dashboard</h1>
    </div>

    <div class="dashboard-grid">
      <div class="card summary-card">
        <div class="summary-icon">📦</div>
        <div class="summary-info">
          <div class="summary-label">Total Inventory Items</div>
          <div class="summary-value">
            @if (loadingInventory()) { — } @else { {{ inventoryCount() }} }
          </div>
        </div>
      </div>

      <div class="card summary-card">
        <div class="summary-icon">🛒</div>
        <div class="summary-info">
          <div class="summary-label">Open Orders</div>
          <div class="summary-value">
            @if (loadingOrders()) { — } @else { {{ ordersCount() }} }
          </div>
        </div>
      </div>

      <div class="card summary-card">
        <div class="summary-icon">🚚</div>
        <div class="summary-info">
          <div class="summary-label">Active Shipments</div>
          <div class="summary-value">
            @if (loadingShipments()) { — } @else { {{ shipmentsCount() }} }
          </div>
        </div>
      </div>

      <div class="card summary-card">
        <div class="summary-icon">🏭</div>
        <div class="summary-info">
          <div class="summary-label">Total Warehouses</div>
          <div class="summary-value">
            @if (loadingWarehouses()) { — } @else { {{ warehousesCount() }} }
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 20px; }
    .summary-card   { display: flex; align-items: center; gap: 16px; padding: 24px; }
    .summary-icon   { font-size: 36px; }
    .summary-label  { font-size: 13px; color: var(--text-muted); margin-bottom: 4px; }
    .summary-value  { font-size: 32px; font-weight: 700; color: var(--text); }
  `]
})
export class DashboardComponent implements OnInit {
  private inventoryService = inject(InventoryService);
  private orderService = inject(OrderService);
  private shipmentService = inject(ShipmentService);
  private warehouseService = inject(WarehouseService);

  inventoryCount  = signal(0);
  ordersCount     = signal(0);
  shipmentsCount  = signal(0);
  warehousesCount = signal(0);

  loadingInventory  = signal(true);
  loadingOrders     = signal(true);
  loadingShipments  = signal(true);
  loadingWarehouses = signal(true);

  ngOnInit(): void {
    this.inventoryService.getAll(0, 1).subscribe({
      next: r => { this.inventoryCount.set(r.totalElements); this.loadingInventory.set(false); },
      error: () => this.loadingInventory.set(false)
    });
    this.orderService.getAll(0, 1).subscribe({
      next: r => { this.ordersCount.set(r.totalElements); this.loadingOrders.set(false); },
      error: () => this.loadingOrders.set(false)
    });
    this.shipmentService.getAll(0, 1).subscribe({
      next: r => { this.shipmentsCount.set(r.totalElements); this.loadingShipments.set(false); },
      error: () => this.loadingShipments.set(false)
    });
    this.warehouseService.getAll(0, 1).subscribe({
      next: r => { this.warehousesCount.set(r.totalElements); this.loadingWarehouses.set(false); },
      error: () => this.loadingWarehouses.set(false)
    });
  }
}

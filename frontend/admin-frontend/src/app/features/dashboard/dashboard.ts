// src/app/features/dashboard/dashboard.component.ts
import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { ForecastService } from '../inventory/services/forecast.service';
import { OrderService } from '../orders/services/order.service';
import { WarehouseService } from '../warehouses/services/warehouse.service';
import { ShipmentService } from '../shipments/services/shipment.service';
import { AuthService } from '../../core/auth/auth.service';
import { ForecastResult } from '../inventory/models/inventory.model';
import { Shipment } from '../shipments/models/shipment.model';
import { Warehouse } from '../warehouses/models/warehouse.model';
import { Page } from '../users/models/user.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, TitleCasePipe],
  template: `
    <div class="dashboard-wrapper">

      <div class="page-header">
        <h1>Welcome back, {{ role() | titlecase }}</h1>
        <p class="text-muted">
          Operational Telemetry & Distribution Control Center
        </p>
      </div>

      @if (!canSeeInventory()) {
        <div class="card status-banner animate-fade-in">
          <div class="banner-icon">📋</div>
          <div>
            <h3>Your Active Queue Pipeline</h3>
            <p class="text-muted">
              Your profile is optimized for floor execution.
              Check Orders or Shipments for active work queues.
            </p>
          </div>
        </div>
      }

      @if (canSeeInventory()) {

        <div class="metrics-grid">
          <div class="metric-card">
            <span class="metric-label">Open Orders</span>
            <span class="metric-value">{{ orderSummary().totalOrders }}</span>
          </div>
          <div class="metric-card">
            <span class="metric-label">Warehouses / Units</span>
            <span class="metric-value">{{ warehouseSummary().warehouseCount }} / {{ warehouseSummary().storageUnitCount }}</span>
          </div>
          <div class="metric-card">
            <span class="metric-label">Shipments</span>
            <span class="metric-value">{{ shipmentSummary().totalShipments }}</span>
          </div>
          <div class="metric-card warning">
            <span class="metric-label">In Transit</span>
            <span class="metric-value">{{ shipmentSummary().inTransit }}</span>
          </div>
        </div>

        <div class="charts-grid">
          <div class="card chart-panel">
            <div class="chart-header">
              <div>
                <h3>Order Throughput</h3>
                <p class="text-muted">Current pipeline by status bucket.</p>
              </div>
            </div>
            <div class="chart-bars">
              @for (point of orderGraph(); track point.label) {
                <div class="chart-column">
                  <div class="bar-stack">
                    <div class="bar background" [style.height.%]="100"></div>
                    <div class="bar actual" [style.height.%]="point.height"></div>
                  </div>
                  <span class="bar-label">{{ point.label }}</span>
                </div>
              }
            </div>
          </div>

          <div class="card chart-panel">
            <div class="chart-header">
              <div>
                <h3>Shipment Flow</h3>
                <p class="text-muted">Shipment volume by fulfillment state.</p>
              </div>
            </div>
            <div class="chart-bars">
              @for (point of shipmentGraph(); track point.label) {
                <div class="chart-column">
                  <div class="bar-stack">
                    <div class="bar background" [style.height.%]="100"></div>
                    <div class="bar actual" [style.height.%]="point.height"></div>
                  </div>
                  <span class="bar-label">{{ point.label }}</span>
                </div>
              }
            </div>
          </div>

          <div class="card chart-panel">
            <div class="chart-header">
              <div>
                <h3>Warehouse Capacity</h3>
                <p class="text-muted">Storage unit capacity utilization by facility.</p>
              </div>
            </div>
            <div class="chart-bars">
              @for (point of capacityGraph(); track point.label) {
                <div class="chart-column">
                  <div class="bar-stack">
                    <div class="bar background" [style.height.%]="100"></div>
                    <div class="bar capacity" [style.height.%]="point.height"></div>
                  </div>
                  <span class="bar-label">{{ point.label }}</span>
                </div>
              }
            </div>
          </div>
        </div>

        @if (forecast()) {
          <div class="card forecast-panel">
            <div class="forecast-header">
              <div>
                <h3>Demand Forecast</h3>
                <p class="text-muted">
                  4-week rolling demand with a 10% holiday buffer.
                </p>
              </div>
              <div class="forecast-badge">AI Autopilot</div>
            </div>
            <div class="forecast-grid">
              <div class="forecast-card">
                <span class="metric-label">Average Weekly Demand</span>
                <span class="metric-value">{{ forecast()!.averageWeeklyDemand }}</span>
              </div>
              <div class="forecast-card">
                <span class="metric-label">Buffered Demand</span>
                <span class="metric-value">{{ forecast()!.holidayBufferedDemand }}</span>
              </div>
              <div class="forecast-card accent">
                <span class="metric-label">Predicted Weekly Reorder</span>
                <span class="metric-value">{{ forecast()!.predictedWeeklyDemand }}</span>
              </div>
            </div>
          </div>
        }
      }

    </div>
  `,
  styles: [`
    .dashboard-wrapper {
      width: 100%;
    }

    .page-header {
      margin-bottom: 28px;
    }

    .text-muted {
      color: #64748b;
      font-size: 14px;
      margin-top: 4px;
    }

    .status-banner {
      display: flex;
      gap: 16px;
      align-items: center;
      background: #f8fafc;
      border-left: 4px solid #3b82f6;
      padding: 20px;
    }

    .banner-icon {
      font-size: 32px;
    }

    .metrics-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 16px;
      margin-bottom: 24px;
    }

    .charts-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 16px;
      margin-bottom: 24px;
    }

    .chart-panel {
      padding: 20px;
    }

    .chart-bars {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(70px, 1fr));
      gap: 12px;
      align-items: end;
      min-height: 180px;
    }

    .chart-column {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
    }

    .bar-stack {
      position: relative;
      width: 42px;
      height: 160px;
      display: flex;
      align-items: end;
      justify-content: center;
    }

    .bar {
      position: absolute;
      bottom: 0;
      width: 100%;
      border-radius: 8px 8px 0 0;
      transition: height 0.2s ease;
    }

    .bar.background {
      background: #f8fafc;
      border: 1px solid #e2e8f0;
    }

    .bar.actual {
      background: linear-gradient(180deg, #3b82f6, #1d4ed8);
      width: 70%;
      z-index: 2;
    }

    .bar.capacity {
      background: linear-gradient(180deg, #10b981, #059669);
      width: 70%;
      z-index: 2;
    }

    .bar.shipment {
      background: linear-gradient(180deg, #8b5cf6, #7c3aed);
      width: 70%;
      z-index: 2;
    }

    .bar-label {
      font-size: 12px;
      color: #64748b;
      text-align: center;
    }

    .forecast-panel {
      margin-bottom: 24px;
      padding: 20px;
    }

    .forecast-header {
      display: flex;
      justify-content: space-between;
      gap: 12px;
      align-items: flex-start;
      margin-bottom: 16px;
    }

    .forecast-badge {
      background: #e0f2fe;
      color: #0369a1;
      border-radius: 999px;
      padding: 6px 10px;
      font-size: 12px;
      font-weight: 700;
    }

    .forecast-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
      gap: 12px;
    }

    .forecast-card {
      background: #fff;
      border: 1px solid var(--border);
      border-radius: 10px;
      padding: 16px;
      display: flex;
      flex-direction: column;
    }

    .forecast-card.accent {
      background: #eff6ff;
      border-color: #bfdbfe;
    }

    .metric-card {
      background: white;
      border: 1px solid var(--border);
      border-radius: 8px;
      padding: 20px;
      display: flex;
      flex-direction: column;
    }

    .metric-card.warning {
      border-left: 4px solid #f59e0b;
    }

    .metric-label {
      font-size: 13px;
      font-weight: 500;
      color: #64748b;
      text-transform: uppercase;
    }

    .metric-value {
      font-size: 28px;
      font-weight: 700;
      color: #0f172a;
      margin-top: 4px;
    }

  `]
})
export class DashboardComponent implements OnInit {
  private forecastService = inject(ForecastService);
  private orderService = inject(OrderService);
  private warehouseService = inject(WarehouseService);
  private shipmentService = inject(ShipmentService);
  private authService = inject(AuthService);

  role = computed(() => this.authService.currentUser()?.role);
  canSeeInventory = computed(() => this.role() === 'ADMIN' || this.role() === 'MANAGER');
  orderSummary = signal({ totalOrders: 0 });
  warehouseSummary = signal({ warehouseCount: 0, storageUnitCount: 0 });
  orderGraph = signal<Array<{ label: string; height: number }>>([]);
  shipmentGraph = signal<Array<{ label: string; height: number }>>([]);
  capacityGraph = signal<Array<{ label: string; height: number }>>([]);
  shipmentSummary = signal({ totalShipments: 0, inTransit: 0 });
  forecast = signal<ForecastResult | null>(null);

  ngOnInit(): void {
    this.loadForecast();
    this.loadOperationalSummaries();
  }

  loadOperationalSummaries(): void {
    this.orderService.getOrders(0, 50, null, null, undefined, null).subscribe({
      next: page => {
        this.orderSummary.set({ totalOrders: page.totalElements ?? page.content?.length ?? 0 });
        this.updateOrderGraph(page.content ?? []);
      }
    });

    this.shipmentService.getShipments(0, 50, undefined, undefined).subscribe({
      next: page => {
        const shipments = page.content ?? [];
        const buckets = [
          { label: 'Pending', count: 0 },
          { label: 'In Transit', count: 0 },
          { label: 'Delivered', count: 0 },
          { label: 'Returned', count: 0 }
        ];

        shipments.forEach((shipment: Shipment) => {
          const status = String(shipment.status || '').toUpperCase();
          if (status.includes('PENDING')) buckets[0].count++;
          else if (status.includes('TRANSIT')) buckets[1].count++;
          else if (status.includes('DELIVER')) buckets[2].count++;
          else if (status.includes('RETURN')) buckets[3].count++;
        });

        const max = Math.max(1, ...buckets.map(bucket => bucket.count));
        this.shipmentGraph.set(
          buckets.map(bucket => ({
            label: bucket.label,
            height: Math.max(10, (bucket.count / max) * 100)
          }))
        );

        this.shipmentSummary.set({
          totalShipments: page.totalElements ?? shipments.length,
          inTransit: buckets[1].count
        });
      }
    });

    this.warehouseService.getAll(0, 50).subscribe({
      next: page => {
        this.warehouseSummary.set({
          warehouseCount: page.totalElements ?? page.content?.length ?? 0,
          storageUnitCount: 0
        });

        const warehouses = page.content ?? [];
        if (!warehouses.length) {
          this.capacityGraph.set([]);
          return;
        }

        const firstWarehouses = warehouses.slice(0, 5);
        let remaining = firstWarehouses.length;
        const points = firstWarehouses.map(w => ({ label: w.name, height: Math.max(20, 100 - remaining * 12) }));
        this.capacityGraph.set(points);

        firstWarehouses.forEach(warehouse => {
          this.warehouseService.getStorageUnits(warehouse.id).subscribe({
            next: units => {
              const current = this.warehouseSummary();
              this.warehouseSummary.set({
                ...current,
                storageUnitCount: current.storageUnitCount + units.length
              });
            }
          });
        });
      }
    });
  }

  loadForecast(): void {
    this.forecastService.getWeeklyDemandForecast(1).subscribe({
      next: result => this.forecast.set(result),
      error: () => this.forecast.set(null)
    });
  }

  private updateOrderGraph(orders: any[]): void {
    const buckets = [
      { label: 'Pending', count: 0 },
      { label: 'Routing', count: 0 },
      { label: 'Packed', count: 0 },
      { label: 'Shipped', count: 0 }
    ];

    orders.forEach(order => {
      const status = String(order.statusName || order.status || '').toUpperCase();
      if (status.includes('PENDING')) buckets[0].count++;
      else if (status.includes('ROUT')) buckets[1].count++;
      else if (status.includes('PACK')) buckets[2].count++;
      else if (status.includes('SHIP')) buckets[3].count++;
    });

    const max = Math.max(1, ...buckets.map(bucket => bucket.count));
    this.orderGraph.set(
      buckets.map(bucket => ({
        label: bucket.label,
        height: Math.max(10, (bucket.count / max) * 100)
        }))
    );
  }
}

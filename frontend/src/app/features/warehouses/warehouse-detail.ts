import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { WarehouseService } from '../../core/services/warehouse.service';
import { Warehouse, StorageUnit } from '../../core/models/warehouse.model';
import { DateFormatPipe } from '../../shared/pipes/date-format.pipe';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner';

@Component({
  selector: 'app-warehouse-detail',
  standalone: true,
  imports: [RouterLink, DateFormatPipe, LoadingSpinnerComponent],
  template: `
    <div class="page-header">
      <h1>Warehouse Detail</h1>
      <a routerLink="/warehouses" class="btn btn-outline">← Back</a>
    </div>

    @if (loading()) {
      <app-loading-spinner />
    } @else if (warehouse(); as w) {
      <div class="card" style="margin-bottom:20px">
        <h3 style="margin:0 0 8px">{{ w.name }}</h3>
        <p style="color:var(--text-muted);margin:0 0 4px">{{ w.address }}</p>
        <p style="color:var(--text-muted);margin:0;font-size:13px">Created: {{ w.createdAt | dateFormat }}</p>
      </div>

      <div class="card">
        <h3 style="margin:0 0 16px">Storage Units ({{ storageUnits().length }})</h3>
        @if (storageUnits().length) {
          <table>
            <thead>
              <tr>
                <th>Code</th>
                <th>Type</th>
                <th>Inventory Item</th>
                <th>Quantity</th>
              </tr>
            </thead>
            <tbody>
              @for (unit of storageUnits(); track unit.id) {
                <tr>
                  <td>{{ unit.code }}</td>
                  <td>{{ unit.type }}</td>
                  <td>{{ unit.inventoryItemName ?? '—' }}</td>
                  <td>{{ unit.quantity ?? '—' }}</td>
                </tr>
              }
            </tbody>
          </table>
        } @else {
          <p style="color:var(--text-muted)">No storage units assigned.</p>
        }
      </div>
    }
  `
})
export class WarehouseDetailComponent implements OnInit {
  private warehouseService = inject(WarehouseService);
  private route = inject(ActivatedRoute);

  loading = signal(true);
  warehouse = signal<Warehouse | null>(null);
  storageUnits = signal<StorageUnit[]>([]);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.warehouseService.getLayout(id).subscribe({
      next: layout => {
        this.warehouse.set(layout.warehouse);
        this.storageUnits.set(layout.storageUnits);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}

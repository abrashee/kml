// src/app/features/warehouse/warehouse-detail.component.ts
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { WarehouseService } from '../services/warehouse.service';
import { AuthService } from '../../../core/auth/auth.service';
import { Warehouse, StorageUnit } from '../models/warehouse.model';

@Component({
  selector: 'app-warehouse-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    @if (loading()) {
      <div class="state">Loading warehouse data...</div>
    } @else if (warehouse()) {

      <div class="page-header" style="margin-bottom: 16px;">
        <a routerLink="/warehouses" class="text-muted" style="text-decoration: none; font-size: 14px;">&larr; Back to List</a>
      </div>

      <div class="card summary-card">
        <div class="summary-header">
          <h1 style="margin: 0;">{{ warehouse()!.name }}</h1>
          <span class="badge bg-success">{{ warehouse()!.status || 'ACTIVE' }}</span>
        </div>

        <div class="summary-grid">
          <div class="summary-item">
            <span class="summary-label">Address / Location</span>
            <span class="summary-value">{{ warehouse()!.address }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">Storage Units</span>
            <span class="summary-value">{{ units().length }} Assigned</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">Manager / Owner</span>
            <span class="summary-value" [class.text-muted]="!warehouse()!.ownerName">
              {{ warehouse()!.ownerName || 'Pending DTO Update' }}
            </span>
          </div>
          <div class="summary-item">
            <span class="summary-label">System ID</span>
            <span class="summary-value text-muted">WH-{{ warehouse()!.id | number:'3.0' }}</span>
          </div>
        </div>
      </div>

      <div class="storage-section">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <h2>Storage Units</h2>
          @if (canManageUnits()) {
            <button class="btn btn-outline" (click)="showForm.set(!showForm())">
              {{ showForm() ? 'Cancel' : '+ Add Storage Unit' }}
            </button>
          }
        </div>

        @if (showForm() && canManageUnits()) {
          <div class="card" style="margin-bottom: 24px; background: #f8fafc;">
            <form [formGroup]="unitForm" (ngSubmit)="onAddUnit()" style="display: flex; gap: 16px; align-items: flex-end;">
              <div style="flex: 1;">
                <label class="form-label">Unit Code / Identifier</label>
                <input class="form-control" formControlName="code" placeholder="e.g., Zone-A-Rack-12" />
              </div>
              <div style="flex: 1;">
                <label class="form-label">Capacity</label>
                <input type="number" class="form-control" formControlName="capacity" min="1" />
              </div>
              <button type="submit" class="btn btn-primary" [disabled]="unitForm.invalid || savingUnit()">
                Save Unit
              </button>
            </form>
          </div>
        }

        @if (units().length === 0) {
          <div class="empty card">No storage units configured for this warehouse yet.</div>
        } @else {
          <div class="inventory-grid">
            @for (unit of units(); track unit.id) {
              <div class="inventory-card">
                <div class="fw-bold">{{ unit.code }}</div>
                <div style="font-size: 13px; color: #64748b; margin-top: 4px;">
                  Capacity: {{ unit.capacity }}
                </div>
                <div style="margin-top: 12px;">
                  <span class="badge bg-success">
                    {{ unit.status || 'AVAILABLE' }}
                  </span>
                </div>
              </div>
            }
          </div>
        }
      </div>
    }
  `,
  styles: [`
    /* New Summary Card Styles */
    .summary-card { margin-bottom: 32px; background: #ffffff; border-left: 4px solid #0f172a; }
    .summary-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .summary-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 24px; }
    .summary-item { display: flex; flex-direction: column; gap: 4px; }
    .summary-label { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; }
    .summary-value { font-size: 15px; font-weight: 500; color: #0f172a; }

    /* Existing Styles */
    .text-muted { color: #64748b; }
    .inventory-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
    .inventory-card { padding: 16px; border: 1px solid var(--border); border-radius: 8px; background: white; }
    .badge { padding: 4px 8px; border-radius: 12px; font-size: 11px; font-weight: bold; }
    .bg-success { background: #dcfce7; color: #166534; }
    .form-control { padding: 8px; border: 1px solid var(--border); border-radius: 6px; width: 100%; box-sizing: border-box; }
    .form-label { font-size: 14px; font-weight: 500; margin-bottom: 8px; display: block;}
  `]
})
export class WarehouseDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  private warehouseService = inject(WarehouseService);
  private authService = inject(AuthService);

  warehouseId = signal<number>(0);
  // Using an intersection type here to temporarily allow ownerName until you update your base model
  warehouse = signal<(Warehouse & { ownerName?: string }) | null>(null);
  units = signal<StorageUnit[]>([]);

  loading = signal(true);
  showForm = signal(false);
  savingUnit = signal(false);

  canManageUnits = computed(() => {
    const role = this.authService.currentUser()?.role;
    return role === 'ADMIN' || role === 'MANAGER';
  });

  unitForm = this.fb.group({
    code: ['', Validators.required],
    capacity: [100, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.warehouseId.set(id);
      this.loadData(id);
    }
  }

  loadData(id: number): void {
    this.loading.set(true);
    this.warehouseService.getById(id).subscribe({
      next: (wh) => {
        this.warehouse.set(wh);
        this.loadUnits(id);
      },
      error: () => this.loading.set(false)
    });
  }

  loadUnits(id: number): void {
    this.warehouseService.getStorageUnits(id).subscribe({
      next: (units) => {
        this.units.set(units);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  onAddUnit(): void {
    if (this.unitForm.invalid) return;
    this.savingUnit.set(true);

    this.warehouseService.addStorageUnit({
      code: this.unitForm.value.code!,
      warehouseId: this.warehouseId(),
      capacity: this.unitForm.value.capacity!
    }).subscribe({
      next: (newUnit) => {
        this.units.update(current => [...current, newUnit]);
        this.showForm.set(false);
        this.unitForm.reset({ capacity: 100 });
        this.savingUnit.set(false);
      },
      error: () => this.savingUnit.set(false)
    });
  }
}

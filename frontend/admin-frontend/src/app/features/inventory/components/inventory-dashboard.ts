// src/app/features/inventory/components/inventory-dashboard.ts
import { Component, inject, signal, OnInit, OnDestroy, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import { InventoryService } from '../services/inventory.service';
import { WarehouseService } from '../../warehouses/services/warehouse.service';
import { AuthService } from '../../../core/auth/auth.service';
import { InventoryItem, CreateInventoryRequest, StorageUnit } from '../models/inventory.model';
import { Warehouse } from '../../warehouses/models/warehouse.model';

@Component({
  selector: 'app-inventory-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  template: `
    <div class="page-header" style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px;">
      <div>
        <h1>Inventory Control</h1>
        <p class="text-muted">Real-time SKU visibility and stock management.</p>
      </div>
      @if (canProvision()) {
        <button class="btn btn-primary" (click)="openAddMode()">+ Provision SKU</button>
      }
    </div>

    @if (showPanel()) {
      <div class="card panel-container">
        <h3 style="margin-top: 0;">{{ editingItem() ? 'Restock Inventory' : 'Provision New Inventory Entry' }}</h3>

        @if (errorMessage()) {
          <div class="alert-danger">
            <strong>Allocation Failed:</strong> {{ errorMessage() }}
          </div>
        }

        @if (!editingItem()) {
          <form [formGroup]="createForm" (ngSubmit)="onCreateSubmit()">
            <div class="form-grid">
              <div>
                <label class="form-label">SKU Reference *</label>
                <input class="form-control" formControlName="sku" placeholder="e.g. KML-EL-9042" />
              </div>
              <div>
                <label class="form-label">Target Warehouse *</label>
                <select class="form-control" formControlName="warehouseId" (change)="onWarehouseSelectionChange()">
                  <option [ngValue]="null" disabled>Select Facility...</option>
                  @for (wh of warehouses(); track wh.id) {
                    <option [ngValue]="wh.id">{{ wh.name }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="form-label">Physical Layout Node / Storage Unit *</label>
                <select class="form-control" formControlName="preferredStorageUnitId">
                  <option [ngValue]="null">Available Capacity</option>
                  @for (unit of storageUnits(); track unit.id) {
                    <option [ngValue]="unit.id">
                      {{ unit.code }} (Left: {{ unit.remainingCapacity || unit.capacity }})
                    </option>
                  }
                </select>
              </div>
              <div>
                <label class="form-label">Initial Physical Count *</label>
                <input type="number" class="form-control" formControlName="initialQuantity" />
              </div>
            </div>
            <div style="margin-top: 20px; display: flex; gap: 8px;">
              <button type="submit" class="btn btn-primary" [disabled]="createForm.invalid || loading()">Save Entry</button>
              <button type="button" class="btn btn-outline" (click)="closePanel()">Cancel</button>
            </div>
          </form>
        } @else {
          <form [formGroup]="restockForm" (ngSubmit)="onRestockSubmit()">
            <div style="margin-bottom: 16px; padding: 12px; background: #fff; border-radius: 6px; border: 1px solid #e2e8f0;">
              <strong>SKU:</strong> {{ editingItem()?.sku }} <br/>
              <strong>Current Physical Stock:</strong> {{ editingItem()?.quantity }} units
            </div>
            <div style="max-width: 300px;">
              <label class="form-label">Quantity to Add *</label>
              <input type="number" class="form-control" formControlName="quantity" placeholder="Enter amount to add..." />
            </div>
            <div style="margin-top: 20px; display: flex; gap: 8px;">
              <button type="submit" class="btn btn-primary" [disabled]="restockForm.invalid || loading()">Confirm Restock</button>
              <button type="button" class="btn btn-outline" (click)="closePanel()">Cancel</button>
            </div>
          </form>
        }
      </div>
    }

    <div class="filters-barcard">
      <div class="search-input-wrapper">
          <input
          type="text"
          class="form-control"
          placeholder="Search by SKU or Name..."
          [(ngModel)]="searchQuery"
          (ngModelChange)="searchChanged($event)"
        />
      </div>

      @if (canFilterFacilities()) {
        <div class="facility-select-wrapper">
          <select class="form-control" [(ngModel)]="selectedWarehouseId" (change)="loadInventory()">
            <option [ngValue]="null">All Facilities</option>
            @for (wh of warehouses(); track wh.id) {
              <option [ngValue]="wh.id">{{ wh.name }}</option>
            }
          </select>
        </div>
      }
    </div>

    <div class="card">
      @if (loading()) {
        <div class="state">Querying inventory ledgers...</div>
      } @else if (!filteredItems().length) {
        <div class="empty">No inventory items matched the current criteria.</div>
      } @else {
        <table class="data-table">
          <thead>
            <tr>
              <th>SKU Code</th>
              <th>Item Identity</th>
              <th>Facility</th>
              <th>Stock Status</th>
              @if (canRestockAndDelete()) {
                <th>Actions</th>
              }
            </tr>
          </thead>
          <tbody>
            @for (item of filteredItems(); track item.id) {
              <tr>
                <td class="sku-text">{{ item.sku }}</td>
                <td>
                  <div class="fw-bold">{{ item.name }}</div>
                  <div class="text-muted">
                    Reorder {{ item.reorderThreshold ?? 10 }} · Safety {{ item.safetyStockLevel ?? 20 }}
                  </div>
                </td>
                <td>{{ item.warehouseName || 'WH-' + item.warehouseId }}</td>
                <td>
                  <div class="stock-metrics">
                    <span class="stock-block total" [class.alert]="item.quantity < (item.reorderThreshold ?? 10)">
                      <strong>{{ item.quantity }}</strong> units
                    </span>
                  </div>
                </td>
                @if (canRestockAndDelete()) {
                  <td>
                    <div style="display: flex; gap: 8px;">
                      <button class="btn btn-sm btn-outline" (click)="openRestockMode(item)">Restock</button>
                      <button class="btn btn-sm btn-danger-outline" (click)="deleteItem(item)">Delete</button>
                    </div>
                  </td>
                }
              </tr>
            }
          </tbody>
        </table>

        <div class="pagination-bar">
          <button
            class="btn btn-sm btn-outline"
            [disabled]="currentPage() === 0"
            (click)="goToPage(currentPage() - 1)">
            Previous
          </button>

          <span class="page-info">
            Page {{ currentPage() + 1 }} / {{ totalPages() }}
          </span>

          <button
            class="btn btn-sm btn-outline"
            [disabled]="totalPages() === 0 || currentPage() >= totalPages() - 1"
            (click)="goToPage(currentPage() + 1)">
            Next
          </button>
        </div>
      }
    </div>
  `,
  styles: [`
    .panel-container { background: #f8fafc; border-left: 4px solid #3b82f6; margin-bottom: 24px; padding: 20px; }
    .form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; }
    .form-label { display: block; margin-bottom: 6px; font-weight: 500; font-size: 13px; color: #475569; }
    .form-control { width: 100%; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px; box-sizing: border-box; font-size: 14px; }
    .alert-danger { background: #fef2f2; color: #991b1b; border: 1px solid #fca5a5; padding: 12px; border-radius: 6px; margin-bottom: 16px; font-size: 14px; }
    .filters-barcard { background: white; border: 1px solid var(--border); border-radius: 8px; padding: 12px; display: flex; gap: 16px; margin-bottom: 16px; }
    .search-input-wrapper { flex: 2; }
    .facility-select-wrapper { flex: 1; }
    .data-table { width: 100%; border-collapse: collapse; text-align: left; }
    .data-table th, .data-table td { padding: 12px; border-bottom: 1px solid var(--border); vertical-align: middle; }
    .sku-text { font-family: monospace; font-weight: bold; color: #0f172a; background: #f1f5f9; padding: 4px 8px; border-radius: 4px; font-size: 13px; }
    .fw-bold { font-weight: 600; }
    .stock-metrics { display: flex; gap: 8px; font-size: 12px; }
    .stock-block { padding: 4px 8px; border-radius: 4px; border: 1px solid transparent; }
    .stock-block.total { background: #f8fafc; border-color: #cbd5e1; color: #334155; }
    .stock-block.total.alert { background: #fef2f2; border-color: #fecaca; color: #991b1b; }
    .text-muted { color: #64748b; font-size: 12px; }
    .state, .empty { text-align: center; padding: 24px; color: #64748b; }
    .btn-danger-outline { border: 1px solid #ef4444; color: #ef4444; background: transparent; cursor: pointer; padding: 4px 12px; border-radius: 4px; font-size: 13px;}
    .btn-danger-outline:hover { background: #fef2f2; }
    .pagination-bar { margin-top: 16px; display: flex; justify-content: flex-end; gap: 12px; align-items: center; }
    .page-info { font-size: 14px; color: #64748b; }
    @media (max-width: 768px) {
      .page-header,
      .filters-barcard,
      .pagination-bar {
        flex-direction: column;
        align-items: stretch;
      }
      .form-grid {
        grid-template-columns: 1fr;
      }
      .data-table {
        display: block;
        overflow-x: auto;
        white-space: nowrap;
      }
      .panel-container {
        padding: 16px;
      }
      .btn, .btn-sm {
        width: 100%;
        justify-content: center;
      }
    }
  `]
})
export class InventoryDashboardComponent implements OnInit, OnDestroy {
  private fb = inject(FormBuilder);
  private inventoryService = inject(InventoryService);
  private warehouseService = inject(WarehouseService);
  private authService = inject(AuthService);

  items = signal<InventoryItem[]>([]);
  warehouses = signal<Warehouse[]>([]);
  storageUnits = signal<StorageUnit[]>([]);
  loading = signal(true);
  showPanel = signal(false);
  editingItem = signal<InventoryItem | null>(null);
  errorMessage = signal<string | null>(null);
  totalPages = signal(0);

  searchQuery = '';
  selectedWarehouseId: number | null = null;
  currentPage = signal(0);
  private searchInput$ = new Subject<string>();
  private destroy$ = new Subject<void>();

  private userContext = computed(() => this.authService.currentUser() as any);
  userRole = computed(() => this.userContext()?.role);

  isAdmin = computed(() => this.userRole() === 'ADMIN');
  isManager = computed(() => this.userRole() === 'MANAGER');
  isWorker = computed(() => this.userRole() === 'WORKER');

  canFilterFacilities = computed(() => this.isAdmin());
  canProvision = computed(() => this.isAdmin());
  canRestockAndDelete = computed(() => this.isAdmin() || this.isManager());

  filteredItems = computed(() => {
    const rawItems = this.items();
    if (this.isAdmin()) {
      return this.selectedWarehouseId
        ? rawItems.filter(i => i.warehouseId === this.selectedWarehouseId)
        : rawItems;
    }
    return rawItems.filter(i => i.warehouseId === this.userContext()?.warehouseId);
  });

  // ⚡ UPDATED: Removed name control parameter array assignment path elements
  createForm = this.fb.group({
    sku: ['', Validators.required],
    warehouseId: [null as number | null, Validators.required],
    preferredStorageUnitId: [null as number | null],
    initialQuantity: [0, [Validators.required, Validators.min(0)]]
  });

  restockForm = this.fb.group({
    quantity: [1, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    this.searchInput$
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => this.loadInventory(0));

    if (this.isAdmin()) {
      this.loadWarehouses();
    } else {
      this.selectedWarehouseId = this.userContext()?.warehouseId || null;
      this.createForm.patchValue({ warehouseId: this.selectedWarehouseId });
      this.onWarehouseSelectionChange();
    }
    this.loadInventory(0);
  }

  loadWarehouses(): void {
    this.warehouseService.getAll(0, 100).subscribe({
      next: p => this.warehouses.set(p.content)
    });
  }

  onWarehouseSelectionChange(): void {
    const targetWarehouseId = this.createForm.value.warehouseId;
    this.createForm.patchValue({ preferredStorageUnitId: null });

    if (!targetWarehouseId) {
      this.storageUnits.set([]);
      return;
    }

    this.inventoryService.getStorageUnitsByWarehouse(targetWarehouseId).subscribe({
      next: (units) => this.storageUnits.set(units),
      error: () => this.storageUnits.set([])
    });
  }

  loadInventory(pageIndex = this.currentPage()): void {
    this.loading.set(true);
    this.currentPage.set(pageIndex);
    this.inventoryService.getInventory(
      pageIndex, 50,
      this.searchQuery.trim() || undefined,
      this.selectedWarehouseId
    ).subscribe({
      next: (page) => {
        this.items.set(page.content);
        this.totalPages.set(page.totalPages ?? 0);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  searchChanged(value: string): void {
    this.searchQuery = value;
    this.currentPage.set(0);
    this.searchInput$.next(value);
  }

  openAddMode(): void {
    this.errorMessage.set(null);
    this.editingItem.set(null);
    this.createForm.reset({
      sku: '', preferredStorageUnitId: null, initialQuantity: 0,
      warehouseId: this.isAdmin() ? null : this.selectedWarehouseId
    });
    this.storageUnits.set([]);
    if (!this.isAdmin()) this.onWarehouseSelectionChange();
    this.showPanel.set(true);
  }

  openRestockMode(item: InventoryItem): void {
    this.errorMessage.set(null);
    this.editingItem.set(item);
    this.restockForm.reset({ quantity: 1 });
    this.showPanel.set(true);
  }

  closePanel(): void {
    this.showPanel.set(false);
    this.editingItem.set(null);
    this.errorMessage.set(null);
  }

  onCreateSubmit(): void {
    if (this.createForm.invalid) return;
    this.loading.set(true);
    this.errorMessage.set(null);

    const form = this.createForm.value;
    const storageUnitId = form.preferredStorageUnitId || this.storageUnits()[0]?.id;

    if (!storageUnitId) {
      this.loading.set(false);
      this.errorMessage.set('Select a storage unit before provisioning inventory.');
      return;
    }

    // ⚡ CLEANED: Drops the name property mapping bound layer
    const request: CreateInventoryRequest = {
      ownerUserId: Number(this.userContext()?.id || 1),
      sku: form.sku!,
      name: form.sku!,
      quantity: form.initialQuantity!,
      warehouseId: form.warehouseId!,
      storageUnitId
    };

    this.inventoryService.createItem(request).subscribe({
      next: () => {
        this.loadInventory(this.currentPage());
        this.closePanel();
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || "An unexpected fault occurred during provisioning.");
      }
    });
  }

  onRestockSubmit(): void {
    const item = this.editingItem();
    if (this.restockForm.invalid || !item) return;
    this.loading.set(true);
    this.errorMessage.set(null);

    const deltaAmount = this.restockForm.value.quantity!;

    this.inventoryService.updateQuantity(item.id, deltaAmount).subscribe({
      next: () => {
        this.loadInventory(this.currentPage());
        this.closePanel();
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || "Failed to update allocation logs.");
      }
    });
  }

  goToPage(pageIndex: number): void {
    if (pageIndex < 0 || pageIndex >= this.totalPages()) {
      return;
    }

    this.loadInventory(pageIndex);
  }

  deleteItem(item: InventoryItem): void {
    if (item.quantity > 0) {
      alert("Operational Lockout: Empty physical unit lines to zero before deleting item records.");
      return;
    }

    if (confirm(`Confirm permanent erasure of SKU node configuration: ${item.sku}?`)) {
      this.inventoryService.deleteItem(item.id).subscribe({
        next: () => this.loadInventory(), // ⚡ FIX: Changed 'not' to 'next'
        error: (err) => alert(err.error?.message || "Deletion request rejected.")
      });
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.searchInput$.complete();
  }
}

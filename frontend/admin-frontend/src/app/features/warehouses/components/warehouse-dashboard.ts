// src/app/features/warehouse/warehouse-list.component.ts
import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { WarehouseService } from '../services/warehouse.service';
import { AuthService } from '../../../core/auth/auth.service';
import { Warehouse } from '../models/warehouse.model';
import { Page } from '../../users/models/user.model';

@Component({
  selector: 'app-warehouse-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-header">
      <div>
        <h1>Warehouses</h1>
        <p class="text-muted">Manage global fulfillment centers</p>
      </div>

      @if (isAdmin()) {
        <button class="btn btn-primary" routerLink="/warehouses/new">+ New Warehouse</button>
      }
    </div>

    <div class="card">
      @if (loading()) {
        <div class="state">Loading facilities...</div>
      } @else if (!page()?.content?.length) {
        <div class="empty">No warehouses found in the system.</div>
      } @else {
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Location</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (wh of page()!.content; track wh.id) {
              <tr>
                <td>#{{ wh.id }}</td>
                <td class="fw-bold">{{ wh.name }}</td>
                <td>{{ wh.location }}</td>
                <td>
                  <span class="badge" [class.bg-success]="wh.status === 'ACTIVE'" [class.bg-warning]="wh.status === 'INACTIVE'">
                    {{ wh.status }}
                  </span>
                </td>
                <td>
                  <a [routerLink]="['/warehouses', wh.id]" class="btn btn-sm btn-outline">Manage Units</a>
                </td>
              </tr>
            }
          </tbody>
        </table>
      }
    </div>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .data-table { width: 100%; border-collapse: collapse; text-align: left; }
    .data-table th, .data-table td { padding: 12px; border-bottom: 1px solid var(--border); }
    .fw-bold { font-weight: 600; }
    .badge { padding: 4px 8px; border-radius: 12px; font-size: 11px; font-weight: bold; }
    .bg-success { background: #dcfce7; color: #166534; }
    .bg-warning { background: #fef08a; color: #854d0e; }
  `]
})
export class WarehouseDashboardComponent implements OnInit {
  private warehouseService = inject(WarehouseService);
  private authService = inject(AuthService);

  loading = signal(true);
  page = signal<Page<Warehouse> | null>(null);

  // Strict check: Only ADMIN can create.
  // (Note: The route itself should also be guarded for ADMIN/MANAGER)
  isAdmin = signal(this.authService.currentUser()?.role === 'ADMIN');

  ngOnInit(): void {
    this.loadWarehouses();
  }

  loadWarehouses(): void {
    this.loading.set(true);
    this.warehouseService.getAll(0, 50).subscribe({
      next: (p) => {
        this.page.set(p);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}

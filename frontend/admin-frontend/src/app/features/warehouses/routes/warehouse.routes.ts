// src/app/features/warehouse/warehouse.routes.ts
import { Routes } from '@angular/router';
import { roleGuard } from '../../../core/guards/role.guard';

export const warehouseRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('../components/warehouse-dashboard').then(m => m.WarehouseDashboardComponent),
    canActivate: [roleGuard], data: { roles: ['ADMIN'] }
  },
  {
    path: 'new',
    loadComponent: () => import('../components/warehouse-form').then(m => m.WarehouseFormComponent),
    canActivate: [roleGuard], data: { roles: ['ADMIN'] }
  },
  {
    path: ':id',
    loadComponent: () => import('../components/warehouse-detail').then(m => m.WarehouseDetailComponent),
    canActivate: [roleGuard], data: { roles: ['ADMIN', 'MANAGER'] }
  }
];

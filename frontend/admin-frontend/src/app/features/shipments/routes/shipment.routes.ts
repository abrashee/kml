// src/app/features/warehouse/warehouse.routes.ts
import { Routes } from '@angular/router';
import { roleGuard } from '../../../core/guards/role.guard';

export const shipmentRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('../components/shipment-dashboard').then(m => m.ShipmentDashboardComponent),
    canActivate: [roleGuard], data: { roles: ['ADMIN', 'MANAGER'] }
  },
];

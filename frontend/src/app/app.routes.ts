import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/login/login').then(m => m.LoginComponent)
  },
  {
    path: '',
    loadComponent: () => import('./layout/shell/shell').then(m => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard').then(m => m.DashboardComponent)
      },
      {
        path: 'inventory',
        loadComponent: () => import('./features/inventory/inventory-list').then(m => m.InventoryListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER', 'USER'] }
      },
      {
        path: 'inventory/new',
        loadComponent: () => import('./features/inventory/inventory-form').then(m => m.InventoryFormComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] }
      },
      {
        path: 'inventory/:id/edit',
        loadComponent: () => import('./features/inventory/inventory-form').then(m => m.InventoryFormComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] }
      },
      {
        path: 'orders',
        loadComponent: () => import('./features/orders/order-list').then(m => m.OrderListComponent)
      },
      {
        path: 'orders/new',
        loadComponent: () => import('./features/orders/order-form').then(m => m.OrderFormComponent)
      },
      {
        path: 'orders/:id/edit',
        loadComponent: () => import('./features/orders/order-form').then(m => m.OrderFormComponent)
      },
      {
        path: 'shipments',
        loadComponent: () => import('./features/shipments/shipment-list').then(m => m.ShipmentListComponent)
      },
      {
        path: 'shipments/:id',
        loadComponent: () => import('./features/shipments/shipment-detail').then(m => m.ShipmentDetailComponent)
      },
      {
        path: 'warehouses',
        loadComponent: () => import('./features/warehouses/warehouse-list').then(m => m.WarehouseListComponent)
      },
      {
        path: 'warehouses/:id',
        loadComponent: () => import('./features/warehouses/warehouse-detail').then(m => m.WarehouseDetailComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('./features/users/user-list').then(m => m.UserListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'users/new',
        loadComponent: () => import('./features/users/user-form').then(m => m.UserFormComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      }
    ]
  },
  { path: '**', redirectTo: '' }
];

import { Routes } from '@angular/router';
import { roleGuard } from './core/guards/role.guard';
import { ShellComponent } from '../app/layout/shell/shell';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    canActivate: [roleGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard')
            .then(m => m.DashboardComponent),
        data: { roles: ['ADMIN', 'MANAGER'] }
      },

      {
        path: 'worker-dashboard',
        loadComponent: () =>
          import('./features/dashboard/components/worker-dashboard.component')
            .then(m => m.WorkerDashboardComponent),
        data: { roles: ['WORKER'] }
      },
      {
        path: 'inventory',
        loadComponent: () =>
          import('./features/inventory/components/inventory-dashboard')
            .then(m => m.InventoryDashboardComponent),
        data: { roles: ['ADMIN', 'MANAGER'] }
      },

      {
        path: 'orders',
        loadComponent: () =>
          import('./features/orders/components/order-dashboard')
            .then(m => m.OrderDashboardComponent),
        data: { roles: ['ADMIN', 'MANAGER', 'WORKER'] }
      },

      {
        path: 'shipments',
        loadComponent: () =>
          import('./features/shipments/components/shipment-dashboard')
            .then(m => m.ShipmentDashboardComponent),
        data: { roles: ['ADMIN', 'MANAGER'] }
      },
      {
        path: 'warehouses',
        loadChildren: () =>
          import('./features/warehouses/routes/warehouse.routes')
            .then(m => m.warehouseRoutes),
        data: { roles: ['ADMIN', 'MANAGER'] }
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/components/profile.component')
            .then(m => m.ProfileComponent),
        data: { roles: ['ADMIN', 'MANAGER', 'WORKER', 'CUSTOMER'] }
      },
        {
        path: 'users',
        loadComponent: () =>
          import('./features/users/components/staff-list.component')
            .then(m => m.StaffListComponent),
        data: { roles: ['ADMIN', 'MANAGER', 'WORKER', 'CUSTOMER'] }
      }
    ]
  },

  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login').then(m => m.LoginComponent)
  },

  {
    path: 'unauthorized',
    loadComponent: () =>
      import('./core/components/unauthorized.component')
        .then(m => m.UnauthorizedComponent)
  },

  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
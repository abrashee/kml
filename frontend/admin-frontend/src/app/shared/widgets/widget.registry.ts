// src/app/shared/widgets/widget.register.ts
import { InventoryDashboardComponent } from '../../features/inventory/components/inventory-dashboard';

import { OrderDashboardComponent } from '../../features/orders/components/order-dashboard';

import { ShipmentDashboardComponent } from '../../features/shipments/components/shipment-dashboard';

import { WarehouseDashboardComponent } from '../../features/warehouses/components/warehouse-dashboard';
import { WarehouseDetailComponent } from '../../features/warehouses/components/warehouse-detail';

import { UserListComponent } from '../../features/users/components/user-list';

import { DashboardWidget } from './widget.model';

export const WIDGET_REGISTRY: DashboardWidget[] = [
  {
    id: 'inventory-list',
    component: InventoryDashboardComponent,
    capabilities: ['inventory:read']
  },

  {
    id: 'order-list',
    component: OrderDashboardComponent,
    capabilities: ['orders:read']
  },

  {
    id: 'shipment-list',
    component: ShipmentDashboardComponent,
    capabilities: ['shipments:read']
  },
  {
    id: 'warehouse-list',
    component: WarehouseDashboardComponent,
    capabilities: ['warehouse:read']
  },
  {
    id: 'warehouse-detail',
    component: WarehouseDetailComponent,
    capabilities: ['warehouse:read']
  },

  {
    id: 'user-list',
    component: UserListComponent,
    capabilities: ['users:manage']
  }
];

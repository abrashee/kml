// src/app/core/auth/role-capabilities.ts
export type Capability =
  | 'inventory:read'
  | 'inventory:write'
  | 'orders:read'
  | 'orders:write'
  | 'shipments:read'
  | 'shipments:write'
  | 'warehouse:read'
  | 'warehouse:write'
  | 'users:manage';

export const ROLE_CAPABILITIES: Record<string, Capability[]> = {
  // Customers have no access to the Operations App
  CUSTOMER: [],

  // Workers only see their assigned tasks
  WORKER: [
    'orders:read',
    'shipments:read'
  ],

  // Managers are warehouse-scoped
  MANAGER: [
    'inventory:read',
    'inventory:write',
    'orders:read',
    'orders:write',
    'shipments:read',
    'shipments:write',
    'warehouse:read'
  ],

  // Admins have global operational authority
  ADMIN: [
    'inventory:read',
    'inventory:write',
    'orders:read',
    'orders:write',
    'shipments:read',
    'shipments:write',
    'warehouse:read',
    'warehouse:write',
    'users:manage'
  ]
};

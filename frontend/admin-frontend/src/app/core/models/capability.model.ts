// // src/ app / core / models / capability.model.ts
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
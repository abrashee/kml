export interface InventoryItem {
  id: number;
  sku: string;
  name: string;
  quantity: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateInventoryRequest {
  sku: string;
  name: string;
  quantity: number;
}

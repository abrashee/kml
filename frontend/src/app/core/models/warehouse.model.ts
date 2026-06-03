export interface Warehouse {
  id: number;
  name: string;
  address: string;
  createdAt: string;
}

export interface StorageUnit {
  id: number;
  code: string;
  type: string;
  warehouseId: number;
  inventoryItemId?: number;
  inventoryItemName?: string;
  quantity?: number;
}

export interface WarehouseLayout {
  warehouse: Warehouse;
  storageUnits: StorageUnit[];
}

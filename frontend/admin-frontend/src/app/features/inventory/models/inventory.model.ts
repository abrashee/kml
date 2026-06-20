// src/app/features/inventory/models/inventory.model.ts
export interface StorageUnit {
  id: number;
  code: string;
  capacity: number;
  warehouseId: number;
  remainingCapacity: number;
}

export interface InventoryItem {
  id: number;
  sku: string;       // Delegates cleanly to the product SKU shortcut mapping
  name: string;      // Delegates cleanly to the product identifier name mapping
  quantity: number;
  warehouseId: number;
  warehouseName?: string;
  storageUnitId: number;
  reorderThreshold?: number;
  safetyStockLevel?: number;
}

export interface CreateInventoryRequest {
  ownerUserId: number;
  sku: string;       // The unique left-anchored token identifier
  name?: string;
  quantity: number;  // The operational count being allocated
  warehouseId: number;
  storageUnitId: number;
  reorderThreshold?: number;
  safetyStockLevel?: number;
}

export interface InventoryQuantityUpdateRequest {
  delta: number;
}

export interface ForecastResult {
  productId: number;
  averageWeeklyDemand: number;
  holidayBufferedDemand: number;
  predictedWeeklyDemand: number;
  weeksAnalyzed: number;
  windowStart: string;
  windowEnd: string;
}

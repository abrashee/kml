// src/app/features/warehouse/models/warehouse.model.ts

export interface Warehouse {
  id: number;
  name: string;
  location: string;
  address: string;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
}

export interface StorageUnit {
name?: string;
  id: number;
  warehouseId: number;
  code: string; // Changed from 'name' to 'code'
  capacity: number;
  status?: 'AVAILABLE' | 'FULL' | 'MAINTENANCE'; // Optional since backend doesn't currently return this
}

export interface CreateWarehouseRequest {
  ownerUserId: number;
  name: string;
  address: string;
}

export interface CreateStorageUnitRequest {
  code: string;        // Changed from 'name'
  warehouseId: number; // Added to match backend DTO
  capacity: number;
}

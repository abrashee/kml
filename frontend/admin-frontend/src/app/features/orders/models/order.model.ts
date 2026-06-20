// src/app/features/orders/models/order.model.ts

export type OrderStatus = 'pending' | 'processing' | 'ready_for_pickup' | 'shipped' | 'delivered' | 'cancelled';

export interface OrderItem {
  inventoryId: number;
  sku?: string;
  name?: string;
  quantity: number;
}

export interface OperationalOrder {
  id: number;
  code: string; // Kept for the form reference
  warehouseId?: number;
  warehouseName?: string;
  assignedWorkerId?: number;
  assignedWorkerName?: string;
  status?: OrderStatus;
  items: OrderItem[];
  totalItems: number;
  shipmentId?: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrderRequest {
  code: string;
  status: OrderStatus;
  items: { inventoryId: number; quantity: number }[];
}

export interface UpdateOrderStatusRequest {
  status: OrderStatus;
}

export interface AssignOrderRequest {
  workerId: number;
}
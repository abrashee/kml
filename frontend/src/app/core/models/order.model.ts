export interface OrderItem {
  id?: number;
  inventoryItemId: number;
  quantity: number;
  unitPrice: number;
}

export interface Order {
  id: number;
  code: string;
  status: string;
  items: OrderItem[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrderRequest {
  code: string;
  statusId: number;
  items: OrderItem[];
}

export interface OrderStatus {
  id: number;
  name: string;
}

export interface Shipment {
  id: number;
  trackingNumber: string;
  recipientAddress: string;
  status: string;
  carrier: string;
  orderId: number;
  orderCode: string;
  createdAt: string;
  updatedAt: string;
}

export interface ShipmentHistory {
  id: number;
  status: string;
  note: string;
  createdAt: string;
}

export interface CreateShipmentRequest {
  trackingNumber: string;
  recipientAddress: string;
  carrier: string;
  orderId: number;
}

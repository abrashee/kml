// src/app/features/shipments/models/shipment.ts
export type ShipmentStatus = 'PENDING' | 'IN_TRANSIT' | 'DELIVERED' | 'RETURNED';

export interface RoutePlan {
  shipmentId: number;
  warehouseSequence: string[];
  routeCoordinates: string[];
  estimatedDeliveryTime: string;
  estimatedCost: number;
}

export interface Shipment {
  id: number;
  tracking: string;
  carrierInfo: string;
  address: string;
  status: ShipmentStatus;
  createdAt: string;
  updatedAt: string;
  version: number;
  orderId: number;
  lastRoutePlan?: RoutePlan;
}
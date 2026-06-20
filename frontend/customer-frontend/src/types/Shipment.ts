// src/types/Shipment.ts

export type ShipmentStatus = 'PENDING' | 'IN_TRANSIT' | 'DELIVERED' | 'RETURNED';

export interface RoutePlan {
  shipmentId: number;
  warehouseSequence: string[];
  routeCoordinates: string[];
  estimatedDeliveryTime: string;
  estimatedCost: number;
}

export interface CustomerShipment {
  id: number;
  tracking: string;
  carrierInfo: string;
  address: string;
  status: ShipmentStatus;
  createdAt: string;
  updatedAt: string;
  orderId: number;
  lastRoutePlan?: RoutePlan; // Embedded Gemini API tracking route plan
}

export interface SimulationState {
  shipmentId: number;
  currentLocation: string;
  currentLoad: number;
  timestamp: string;
  eventType: 'LOCATION_UPDATE' | 'DELAY' | 'LOAD_CHANGE';
}
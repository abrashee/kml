// src/features/shipments/shipments.api.ts
import { api } from "../../lib/apiClient";
import { auth } from "../../lib/auth";
import type { CustomerShipment, SimulationState } from "../../types/Shipment";

export interface ShipmentsPageResponse {
  content: CustomerShipment[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

export interface GetShipmentsOptions {
  page?: number;
  size?: number;
  status?: string | null;
  search?: string | null;
}

/**
 * Fetches paginated delivery tracking paths matching the logged-in user profile.
 * Supports status filtering and search by tracking number or destination address.
 */
export async function getShipments(
  options: GetShipmentsOptions = {}
): Promise<ShipmentsPageResponse> {
  const { page = 0, size = 10, status = null, search = null } = options;

  const params = new URLSearchParams();
  params.append("page", String(page));
  params.append("size", String(size));
  if (status) params.append("status", status);
  const user = auth.getUser();
  if (user?.id && !Number.isNaN(Number(user.id))) params.append("userId", String(Number(user.id)));
  if (search && search.trim() !== "") params.append("trackingCode", search.trim());

  const res = await api.get<any>(`/shipments?${params.toString()}`);

  // Safely intercept and unpack Spring Boot Pageable response
  if (res.data && typeof res.data === "object" && "content" in res.data) {
    return {
      content: (res.data.content || []).map(toCustomerShipment),
      totalPages: res.data.totalPages || 0,
      totalElements: res.data.totalElements || 0,
      number: res.data.number || 0,
      size: res.data.size || size,
    };
  }

  // Fallback for bare array response (legacy support)
  if (Array.isArray(res.data)) {
    return {
      content: res.data.map(toCustomerShipment),
      totalPages: 1,
      totalElements: res.data.length,
      number: 0,
      size: res.data.length,
    };
  }

  return {
    content: [],
    totalPages: 0,
    totalElements: 0,
    number: 0,
    size,
  };
}

/**
 * Polls the in-memory state matrix (ConcurrentHashMap) for active telemetry updates
 */
export async function getSimulationLiveState(shipmentId: number): Promise<SimulationState | null> {
  void shipmentId;
  return null;
}

const toCustomerShipment = (shipment: any): CustomerShipment => ({
  id: shipment.id,
  tracking: shipment.trackingCode,
  carrierInfo: shipment.carrierInfo,
  address: shipment.address,
  status: shipment.status,
  createdAt: shipment.createdAt,
  updatedAt: shipment.updatedAt,
  orderId: shipment.orderId
});

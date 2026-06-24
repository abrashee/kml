// src/features/orders/orders.api.ts
import { api } from "../../lib/apiClient";
import { auth } from "../../lib/auth";
import type { CustomerOrder } from "../../types/Order";

export interface OrdersPageResponse {
  content: CustomerOrder[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

export interface GetOrdersOptions {
  page?: number;
  size?: number;
}

export async function getOrders(options: GetOrdersOptions = {}): Promise<OrdersPageResponse> {
  const { page = 0, size = 10 } = options;
  const user = auth.getUser();
  const params: Record<string, string | number> = { page, size };
  if (user?.id && !Number.isNaN(Number(user.id))) {
    params.userId = Number(user.id);
  }
  const res = await api.get<any>("/orders", { params });

  if (res.data && typeof res.data === "object" && "content" in res.data) {
    return {
      content: (res.data.content || []).map(toCustomerOrder),
      totalPages: res.data.totalPages || 0,
      totalElements: res.data.totalElements || 0,
      number: res.data.number || 0,
      size: res.data.size || size,
    };
  }

  if (Array.isArray(res.data)) {
    return {
      content: res.data.map(toCustomerOrder),
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
 * LIFECYCLE: CREATE
 * Kept signature identical to prevent breaking existing component layouts.
 * Maps the resolved inventory identifier cleanly to the backend payload.
 */
export async function createOrder(
  inventoryItemId: string | number, // ⚡ Renamed internally for clarity, signature remains safe
  quantity: number,
  unitPrice: number
): Promise<CustomerOrder> {
  const generatedCode = `ORD-${Math.random().toString(36).substring(2, 10).toUpperCase()}`;
  const user = auth.getUser();
  const userId = user?.id && !Number.isNaN(Number(user.id)) ? Number(user.id) : 1;

  const res = await api.post("/orders", {
    code: generatedCode,
    userId,
    items: [
      {
        sku: String(inventoryItemId),
        quantity: quantity,
        priceAtOrder: unitPrice
      }
    ]
  });
  return toCustomerOrder(res.data);
}

export async function updateOrder(id: string | number, payload: { code: string; statusId: number; items: any[] }) {
  const res = await api.patch(`/orders/${id}/items`, {
    items: payload.items
  });
  return toCustomerOrder(res.data);
}

export async function cancelOrder(orderId: string): Promise<any> {
  const res = await api.patch(`/orders/${orderId}/status/CANCELLED`, {});
  return res.data;
}

const toCustomerOrder = (order: any): CustomerOrder => {
  const items = Array.isArray(order.items) ? order.items : [];
  const subtotal = items.reduce(
    (sum: number, item: any) => sum + Number(item.priceAtOrder ?? 0) * Number(item.quantity ?? 0),
    0
  );

  return {
    orderId: String(order.id),
    orderNumber: order.code,
    orderDate: order.createdAt,
    status: toCustomerStatus(order.status),
    customerId: String(order.userId),
    customerName: `Customer #${order.userId}`,
    customerEmail: "",
    shippingAddress: {
      street: "",
      houseNo: "",
      city: "",
      zipCode: "",
      country: ""
    },
    items: items.map((item: any) => ({
      productId: item.sku,
      productName: item.sku,
      quantity: item.quantity,
      unitPrice: Number(item.priceAtOrder ?? 0),
      lineTotal: Number(item.priceAtOrder ?? 0) * Number(item.quantity ?? 0)
    })),
    subtotal,
    grandTotal: subtotal,
    createdAt: order.createdAt,
    updatedAt: order.updatedAt,
    createdBy: String(order.userId)
  };
};

const toCustomerStatus = (status: string): CustomerOrder["status"] => {
  switch (status) {
    case "ROUTED":
    case "PARTIALLY_FULFILLED":
      return "Processing";
    case "COMPLETED":
      return "Delivered";
    case "CANCELLED":
      return "Cancelled";
    case "PENDING":
    default:
      return "Pending";
  }
};

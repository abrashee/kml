// src / features / products.api.ts
import { api } from "../../lib/apiClient";

export type ProductItem = {
  id: number; // Matches backend Long identity keys
  sku: string;
  name: string;
  quantity: number;
  price: number;
  inventoryItemId?: number;
};

const toProductItem = (item: any): ProductItem => ({
  id: item.id,
  inventoryItemId: item.id,
  sku: item.sku,
  name: item.name || item.sku,
  quantity: item.quantity ?? 0,
  price: item.price ?? 0
});

export const getProducts = async (page = 0, limit = 50, search?: string) => {
  const params: Record<string, string | number> = { page, size: limit };
  if (search && search.trim() !== "") {
    params.sku = search.trim();
  }
  const response = await api.get("/inventories", { params });
  const data = response.data?.content || response.data || [];
  return Array.isArray(data) ? data.map(toProductItem) : data;
};

export async function getProductById(id: string): Promise<ProductItem> {
  const res = await api.get(`/inventories/${id}`);
  return toProductItem(res.data);
}

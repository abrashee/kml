// src / features / products.api.ts
import { api } from "../../lib/apiClient";

export type ProductItem = {
  id: number;
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

export type ProductPage = {
  items: ProductItem[];
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
};

export const getProducts = async (page = 0, limit = 50, search?: string): Promise<ProductPage> => {
  const normalizedSearch = search?.trim() ?? "";
  const hasSearch = normalizedSearch.length > 0;

  const endpoint = hasSearch ? "/products/search" : "/products";
  const params: Record<string, string | number> = { page, size: limit };

  if (hasSearch) {
    params.q = normalizedSearch;
  }

  const response = await api.get(endpoint, { params });
  const payload = response.data;
  const content = Array.isArray(payload?.content) ? payload.content : [];

  return {
    items: content.map(toProductItem),
    page: payload?.number ?? page,
    size: payload?.size ?? limit,
    totalPages: payload?.totalPages ?? 0,
    totalElements: payload?.totalElements ?? 0
  };
};

export async function getProductById(id: string): Promise<ProductItem> {
  const res = await api.get(`/products/${id}`);
  return toProductItem(res.data);
}
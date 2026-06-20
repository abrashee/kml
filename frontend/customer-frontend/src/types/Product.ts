// src / types / Product.ts
export type Product = {
  id: string;
  name: string;
  price: number;
  image?: string;
  sku?: string;
  quantity?: number;
  inventoryItemId?: number | string; // ⚡ SAFE ALIGNMENT: Added optionally for the split table backend schema
};
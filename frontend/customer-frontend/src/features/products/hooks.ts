// src / features / hooks.ts
import { useQuery } from "@tanstack/react-query";
import { getProducts } from "./products.api";

export function useProducts(page = 0, search = "") {
  return useQuery({
    queryKey: ["products", page, search],
    queryFn: () => getProducts(page, 50, search),
    staleTime: 60000,
  });
}

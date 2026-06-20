// src/features/shipments/hooks/useShipmentSearch.ts
import { useState, useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import { getShipments } from "../shipments.api";
import type { ShipmentsPageResponse } from "../shipments.api";
import type { GetShipmentsOptions } from "../shipments.api";
import type { CustomerShipment } from "../../../types/Shipment";

interface UseShipmentSearchOptions {
  pageSize?: number;
  debounceMs?: number;
}

interface UseShipmentSearchResult {
  // Data
  shipments: CustomerShipment[];
  totalPages: number;
  totalElements: number;

  // State
  currentPage: number;
  statusFilter: string | null;
  searchQuery: string;
  isLoading: boolean;
  isError: boolean;
  error: Error | null;

  // Actions
  setPage: (page: number) => void;
  setStatusFilter: (status: string | null) => void;
  setSearchQuery: (query: string) => void;
  refetch: () => Promise<ShipmentsPageResponse>;
}

/**
 * Custom hook for managing shipment search with pagination, filtering, and debounced search.
 * Integrates with TanStack Query for efficient data fetching and caching.
 */
export function useShipmentSearch(
  options: UseShipmentSearchOptions = {}
): UseShipmentSearchResult {
  const { pageSize = 10, debounceMs = 500 } = options;
  // Internal state for immediate UI responsiveness
  const [currentPage, setCurrentPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState("");

  // Debounced search that triggers actual API call
  const [debouncedSearch, setDebouncedSearch] = useState("");

  // Debounce search input
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(searchQuery);
    }, debounceMs);

    return () => clearTimeout(timer);
  }, [searchQuery, debounceMs]);

  // Reset to page 0 when search or status changes
  useEffect(() => {
    setCurrentPage(0);
  }, [debouncedSearch, statusFilter]);

  // TanStack Query hook for fetching data
  const {
    data = {
      content: [],
      totalPages: 0,
      totalElements: 0,
      number: 0,
      size: pageSize,
    },
    isLoading,
    isError,
    error,
    refetch,
  } = useQuery<ShipmentsPageResponse>({
    queryKey: ["customerShipments", currentPage, statusFilter, debouncedSearch],
    queryFn: async () => {
      const options: GetShipmentsOptions = {
        page: currentPage,
        size: pageSize,
        status: statusFilter,
        search: debouncedSearch || null,
      };
      return getShipments(options);
    },
    staleTime: 1000 * 30, // 30 seconds
    gcTime: 1000 * 60 * 5, // 5 minutes (formerly cacheTime)
    retry: 1,
  });

  return {
    // Data
    shipments: data.content,
    totalPages: data.totalPages,
    totalElements: data.totalElements,

    // State
    currentPage,
    statusFilter,
    searchQuery,
    isLoading,
    isError,
    error: error as Error | null,

    // Actions
    setPage: (page: number) => {
      if (page >= 0 && page < data.totalPages) {
        setCurrentPage(page);
      }
    },
    setStatusFilter: (status: string | null) => {
      setStatusFilter(status);
    },
    setSearchQuery: (query: string) => {
      setSearchQuery(query);
    },
    refetch: async () => {
      const result = await refetch();
      return result.data || {
        content: [],
        totalPages: 0,
        totalElements: 0,
        number: 0,
        size: pageSize
      };
    },
  };
}

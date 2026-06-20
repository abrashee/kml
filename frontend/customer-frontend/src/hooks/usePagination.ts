// src / hooks / usePagination.ts
import { useState } from "react";

export function usePagination(initial = 0) {
  const [page, setPage] = useState(initial);

  return {
    page,
    next: () => setPage((p) => p + 1),
    prev: () => setPage((p) => Math.max(0, p - 1)),
  };
}
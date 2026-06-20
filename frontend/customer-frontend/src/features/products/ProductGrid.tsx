import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import type { ProductItem } from "./products.api";
import { useProducts } from "./hooks";
import { useDebounce } from "../../hooks/useDebounce";
import { VirtualizedCardGrid } from "../../components/common/VirtualizedCardGrid";

export default function ProductGrid() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");
  const debouncedSearch = useDebounce(searchQuery, 300);

  const { data: responseData, isLoading, error } = useProducts(0, debouncedSearch);

  const productArray: ProductItem[] = useMemo(
    () => (Array.isArray(responseData) ? responseData : responseData?.content ?? []),
    [responseData]
  );

  if (error) {
    return <div style={{ color: "var(--danger)", padding: "20px" }}>Error loading product catalog.</div>;
  }

  return (
    <div className="product-catalog-view" style={{ textAlign: "left" }}>
      <h2 style={{ fontSize: "20px", margin: "24px 0 4px 0", fontWeight: 700, color: "var(--text-strong)", letterSpacing: "-0.5px" }}>
        Explore Products
      </h2>
      <p style={{ margin: "0 0 24px 0", fontSize: "14px", color: "var(--text)", opacity: 0.6 }}>
        Browse high-grade hardware parts ready for instant global shipping.
      </p>
      <div style={{ marginBottom: "20px" }}>
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search products by SKU or name..."
          style={{
            width: "100%",
            maxWidth: "420px",
            padding: "12px 14px",
            borderRadius: "10px",
            border: "1px solid var(--border)",
            background: "var(--panel)",
            color: "var(--text-strong)"
          }}
        />
      </div>

      {isLoading ? (
        <div className="skeleton-grid">
          {[...Array(6)].map((_, i) => (
            <div key={i} className="skeleton" style={{ height: "220px", background: "var(--panel)", borderRadius: "var(--radius-md)", opacity: 0.2 }} />
          ))}
        </div>
      ) : productArray.length === 0 ? (
        <div style={{ textAlign: "center", padding: "40px", opacity: 0.5 }}>
          Our shelves are temporarily empty. Check back soon!
        </div>
      ) : (
        <VirtualizedCardGrid
          items={productArray}
          minCardWidth={240}
          cardHeight={220}
          gap={24}
          renderItem={(item) => (
            <div
              className="product-card"
              onClick={() => navigate(`/product/${item.id}`)}
              style={{ background: "var(--panel)", border: "1px solid var(--border)", padding: "20px", borderRadius: "var(--radius-md)", cursor: "pointer", height: "100%" }}
            >
              <div className="image-box" style={{ height: "140px", background: "rgba(255,255,255,0.02)", marginBottom: "16px", borderRadius: "var(--radius-sm)" }} />
              <h4 style={{ margin: "0 0 6px 0", color: "var(--text-strong)", fontSize: "16px", fontWeight: 600 }}>
                {item.name}
              </h4>
              <div style={{ fontSize: "12.5px", opacity: 0.4, marginBottom: "12px", fontFamily: "monospace" }}>
                SKU: {item.sku || "N/A"}
              </div>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", fontSize: "13.5px" }}>
                <span style={{ opacity: 0.6 }}>In Stock:</span>
                <span style={{ fontWeight: 600, color: item.quantity > 0 ? "var(--success)" : "var(--danger)" }}>
                  {item.quantity > 0 ? `${item.quantity} units` : "Out of Stock"}
                </span>
              </div>
            </div>
          )}
        />
      )}
    </div>
  );
}

// src / features / ProductDetail.tsx
import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import { useQuery, useMutation } from "@tanstack/react-query";
import { getProductById } from "./products.api";
import { createOrder } from "../orders/orders.api";

function StarRating({ count = 5 }) {
  return (
    <div style={{ display: "flex", gap: "2px", color: "#fbbf24" }}>
      {[...Array(5)].map((_, i) => (
        <svg key={i} xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill={i < count ? "currentColor" : "none"} stroke="currentColor" strokeWidth="2" style={{ width: "14px", height: "14px" }}>
          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
        </svg>
      ))}
    </div>
  );
}

export default function ProductDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [qty, setQty] = useState(1);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const mockImages = useMemo(
    () => [
      "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=600&q=80",
      "https://images.unsplash.com/photo-1578575437130-527eed3abbec?auto=format&fit=crop&w=600&q=80"
    ],
    []
  );

  const { data: rawProduct, isLoading, isError } = useQuery({
    queryKey: ["product", String(id)],
    queryFn: () => getProductById(id!),
    enabled: !!id,
    retry: false
  });

  // ⚡ FIX: Locally extends inferred ProductItem type to safely allow inventoryItemId
  // without breaking any global shared type/interface files.
  const product = rawProduct as typeof rawProduct & { inventoryItemId?: number | string };

  useEffect(() => {
    if (product?.name) document.title = product.name;
    return () => { document.title = "Storefront"; };
  }, [product]);

  const { mutate: placeNewOrder, isPending } = useMutation({
    mutationFn: () => {
      // ⚡ Target identity mapping securely addresses backend split database structures
      const targetInventoryItemId = product!.inventoryItemId || product!.id;
      return createOrder(targetInventoryItemId, qty, product!.price || 0);
    },
    onSuccess: () => {
      setErrorMsg(null);
      setSuccessMsg("Order placed successfully! Redirecting to your tracking page...");
      setTimeout(() => navigate("/orders"), 2000);
    },
    onError: (err: any) => {
      setSuccessMsg(null);
      setErrorMsg(err.response?.data?.message || "Could not complete order transaction.");
    }
  });

  if (isLoading) return <div style={{ padding: "40px", opacity: 0.5 }}>Loading product profile...</div>;
  if (isError || !product) return <div style={{ padding: "40px", textAlign: "center" }}>Product details currently unreachable.</div>;

  return (
    <div className="product-detail-stack" style={{ textAlign: "left" }}>
      <div className="detail-upper-block">
        <div className="carousel-container">
          <div className="carousel-viewport square-viewport">
            <img src={mockImages[0]} alt={product.name} className="square-fit-img" />
          </div>
        </div>

        <div className="meta-content-panel">
          <h2 style={{ fontSize: "24px", color: "var(--text-strong)" }}>{product.name}</h2>

          <div style={{ display: "flex", alignItems: "center", gap: "8px", margin: "8px 0 16px 0" }}>
            <StarRating count={4} />
            <span style={{ fontSize: "13px", opacity: 0.5 }}>(2 customer reviews)</span>
          </div>

          {/* Dynamic Price Render Integration */}
          <div className="price-tag-display" style={{ fontSize: "22px", fontWeight: 700, color: "var(--primary)", marginBottom: "16px" }}>
            ${product.price ? (product.price * qty).toFixed(2) : "0.00"}
            {qty > 1 && (
              <span style={{ fontSize: "14px", fontWeight: 400, color: "var(--text-muted)", marginLeft: "10px" }}>
                (${product.price.toFixed(2)} each)
              </span>
            )}
          </div>

          <p style={{ opacity: 0.7, fontSize: "14.5px", lineHeight: "1.6" }}>
            Premium industrial construction built for reliable performance under heavy configurations. Certified structural grade integrity and precision-engineered metrics.
          </p>

          {successMsg && <div style={{ color: "var(--success)", background: "rgba(34,197,94,0.1)", padding: "12px", borderRadius: "6px", margin: "16px 0" }}>{successMsg}</div>}
          {errorMsg && <div style={{ color: "var(--danger)", background: "rgba(239,68,68,0.1)", padding: "12px", borderRadius: "6px", margin: "16px 0" }}>{errorMsg}</div>}

          <div className="qty-picker" style={{ margin: "24px 0" }}>
            <label style={{ display: "block", marginBottom: "6px", fontSize: "13px", fontWeight: 600 }}>Quantity</label>
            <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
              <input
                type="number"
                min={1}
                max={product.quantity}
                value={qty}
                onChange={(e) => setQty(Math.max(1, Number(e.target.value)))}
                style={{ padding: "10px", borderRadius: "6px", width: "80px", background: "#0f1422", border: "1px solid var(--border)", color: "var(--text-strong)" }}
              />
              <span style={{ fontSize: "13px", opacity: 0.5 }}>({product.quantity} items available)</span>
            </div>
          </div>

          <button className="submit-btn" disabled={product.quantity <= 0 || isPending} onClick={() => placeNewOrder()}>
            {isPending ? "Processing Checkout..." : "Place Order Now"}
          </button>
        </div>
      </div>

      <div className="detail-lower-block" style={{ marginTop: "40px", borderTop: "1px solid var(--border)", paddingTop: "24px" }}>
        <div className="lower-panel-section">
          <h3>Specifications</h3>
          <div className="spec-row"><strong>Standard Weight</strong> <span>1.42 kg</span></div>
          <div className="spec-row"><strong>Material Compound</strong> <span>Reinforced Steel Alloy</span></div>
          <div className="spec-row"><strong>Safety Rating</strong> <span>Class III Certified</span></div>
        </div>

        <div className="lower-panel-section">
          <h3>Verified Buyer Reviews</h3>
          <div className="review-node" style={{ marginBottom: "16px" }}>
            <strong>Aaron V.</strong>
            <StarRating count={5} />
            <p style={{ opacity: 0.7, margin: "4px 0 0 0" }}>Outstanding item durability. Dimensions align precisely with my configuration setup.</p>
          </div>
        </div>
      </div>
    </div>
  );
}

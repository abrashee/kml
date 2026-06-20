// src/features/OrdersPage.tsx
import { useMemo, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useSearchParams } from "react-router-dom";
import { getOrders, cancelOrder, updateOrder } from "./orders.api";

export default function OrdersPage() {
  const queryClient = useQueryClient();
  const [searchParams] = useSearchParams();
  const [page, setPage] = useState(0);
  const pageSize = 10;

  const activeTab = searchParams.get("tab") === "history" ? "history" : "active";

  const { data: orderPage, isLoading } = useQuery({
    queryKey: ["orders", page, pageSize],
    queryFn: () => getOrders({ page, size: pageSize })
  });
  const orders = orderPage?.content ?? [];
  const totalPages = orderPage?.totalPages ?? 0;

  const cancelMutation = useMutation({
    mutationFn: cancelOrder,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["orders"] })
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string | number; payload: any }) => updateOrder(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["orders"] })
  });

  const { activeOrders, pastOrders } = useMemo(() => {
    return {
      activeOrders: orders.filter(
        (o: any) => o.statusName === "Pending" || o.statusId === 1 || o.status === "Pending"
      ),
      pastOrders: orders.filter(
        (o: any) => o.statusName !== "Pending" && o.statusId !== 1 && o.status !== "Pending"
      )
    };
  }, [orders]);

  if (isLoading) {
    return <div style={{ padding: "40px", opacity: 0.6, textAlign: "left" }}>Syncing order pipelines...</div>;
  }

  return (
    <div className="orders-viewport-canvas" style={{ textAlign: "left", padding: "20px" }}>
      <h2 style={{ fontSize: "22px", fontWeight: 700, color: "var(--text-strong)", marginBottom: "24px" }}>
        {activeTab === "active" ? "Current Orders" : "Order History"}
      </h2>

      {activeTab === "active" ? (
        <div className="table-container" style={{ background: "var(--panel)", borderRadius: "8px", border: "1px solid var(--border)", overflow: "hidden" }}>
          {activeOrders.length === 0 ? (
            <div style={{ padding: "32px", textAlign: "center", opacity: 0.5, fontSize: "14px" }}>No active pending fulfillment requests.</div>
          ) : (
            activeOrders.map((order: any) => (
              <OrderRow
                key={order.id}
                order={order}
                isActive={true}
                cancelMutation={cancelMutation}
                updateMutation={updateMutation}
              />
            ))
          )}
        </div>
      ) : (
        <div className="table-container" style={{ background: "var(--panel)", borderRadius: "8px", border: "1px solid var(--border)", overflow: "hidden" }}>
          {pastOrders.length === 0 ? (
            <div style={{ padding: "32px", textAlign: "center", opacity: 0.5, fontSize: "14px" }}>No prior historical transactions logged.</div>
          ) : (
            pastOrders.map((order: any) => (
              <OrderRow
                key={order.id}
                order={order}
                isActive={false}
                cancelMutation={cancelMutation}
                updateMutation={updateMutation}
              />
            ))
          )}
        </div>
      )}

      {totalPages > 1 && (
        <div style={{ display: "flex", justifyContent: "flex-end", alignItems: "center", gap: "12px", marginTop: "20px" }}>
          <button
            className="btn btn-outline"
            disabled={page === 0}
            onClick={() => setPage(current => Math.max(0, current - 1))}
          >
            Previous
          </button>
          <span style={{ color: "var(--text-muted)", fontSize: "14px" }}>
            Page {page + 1} of {totalPages}
          </span>
          <button
            className="btn btn-outline"
            disabled={page >= totalPages - 1}
            onClick={() => setPage(current => Math.min(totalPages - 1, current + 1))}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}

interface OrderRowProps {
  order: any;
  isActive: boolean;
  cancelMutation: any;
  updateMutation: any;
}

function OrderRow({ order, isActive, cancelMutation, updateMutation }: OrderRowProps) {
  // Gracefully handles older empty records by falling back safely to UI defaults
  const initialQty = order.items?.[0]?.quantity || 1;
  const unitPrice = order.items?.[0]?.priceAtOrder || 0;

  const [isEditing, setIsEditing] = useState(false);
  const [localQty, setLocalQty] = useState(initialQty);

  const calculatedTotal = isEditing
    ? localQty * unitPrice
    : (order.items?.reduce((sum: number, item: any) => sum + (item.priceAtOrder * item.quantity), 0) ?? 0);

  const handleSave = () => {
    // ROBUST FALLBACK ENGINE: Covers custom parameters, sub-items, and product entity contexts
    const extractedInventoryItemId =
      order.items?.[0]?.inventoryItemId ||
      order.inventoryItemId ||
      order.productId ||
      order.id; // Absolute fallback boundary to prevent null transmission

    const payload = {
      code: order.code,
      statusId: order.statusId || 1,
      items: [
        {
          inventoryItemId: extractedInventoryItemId,
          quantity: localQty,
          priceAtOrder: unitPrice
        }
      ]
    };

    updateMutation.mutate(
      { id: order.id, payload },
      {
        onSuccess: () => setIsEditing(false)
      }
    );
  };

  const handleCancelEdit = () => {
    setLocalQty(initialQty);
    setIsEditing(false);
  };

  return (
    <div style={{ display: "flex", justifyContent: "space-between", padding: "20px", borderBottom: "1px solid var(--border)", alignItems: "center" }}>
      <div>
        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          <span style={{ fontWeight: 700, fontFamily: "monospace", fontSize: "15px" }}>{order.code}</span>
          <span style={{
            fontSize: "11px",
            fontWeight: 600,
            padding: "2px 8px",
            borderRadius: "12px",
            backgroundColor: order.statusName === 'Cancelled' ? 'rgba(239,68,68,0.15)' : isActive ? 'rgba(245,158,11,0.15)' : 'rgba(59,130,246,0.15)',
            color: order.statusName === 'Cancelled' ? '#ff6b6b' : isActive ? '#f59e0b' : '#60a5fa'
          }}>
            {order.statusName || "Pending"}
          </span>
        </div>

        <div style={{ fontSize: "13px", opacity: 0.6, marginTop: "6px" }}>
          Ordered on: {new Date(order.createdAt).toLocaleDateString()} • Items: {isEditing ? localQty : (order.items?.length || 0)}
        </div>
      </div>

      <div style={{ display: "flex", alignItems: "center", gap: "24px" }}>
        <div style={{ textAlign: "right" }}>
          <div style={{ fontSize: "13px", opacity: 0.5 }}>Total Paid</div>
          <div style={{ fontWeight: 700, fontSize: "16px", color: isEditing ? "#f59e0b" : "var(--text-strong)" }}>
            ${calculatedTotal.toFixed(2)}
          </div>
        </div>

        {isActive && (
          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            {isEditing ? (
              <div style={{ display: "flex", alignItems: "center", gap: "6px", background: "rgba(255,255,255,0.02)", padding: "4px", borderRadius: "6px", border: "1px solid var(--border)" }}>
                <button
                  onClick={() => setLocalQty((q: number) => Math.max(1, q - 1))}
                  style={{ width: "28px", height: "28px", border: "none", background: "rgba(255,255,255,0.05)", color: "white", borderRadius: "4px", cursor: "pointer", fontWeight: "bold" }}
                >
                  -
                </button>
                <input
                  type="number"
                  value={localQty}
                  min="1"
                  onChange={(e) => setLocalQty(Math.max(1, Number(e.target.value)))}
                  style={{ width: "45px", height: "28px", background: "transparent", border: "none", color: "white", textAlign: "center", fontSize: "14px", fontWeight: 600 }}
                />
                <button
                  onClick={() => setLocalQty((q: number) => q + 1)}
                  style={{ width: "28px", height: "28px", border: "none", background: "rgba(255,255,255,0.05)", color: "white", borderRadius: "4px", cursor: "pointer", fontWeight: "bold" }}
                >
                  +
                </button>

                <button
                  onClick={handleSave}
                  disabled={updateMutation.isPending}
                  style={{ marginLeft: "6px", padding: "0 12px", height: "28px", background: "#f59e0b", border: "none", color: "black", borderRadius: "4px", cursor: "pointer", fontSize: "12px", fontWeight: 600 }}
                >
                  {updateMutation.isPending ? "..." : "Save"}
                </button>
                <button
                  onClick={handleCancelEdit}
                  style={{ padding: "0 8px", height: "28px", background: "transparent", border: "none", color: "var(--text-muted)", cursor: "pointer", fontSize: "12px" }}
                >
                  Cancel
                </button>
              </div>
            ) : (
              <>
                <button
                  onClick={() => setIsEditing(true)}
                  style={{ padding: "6px 12px", background: "rgba(255,255,255,0.05)", border: "1px solid var(--border)", color: "white", borderRadius: "4px", cursor: "pointer", fontSize: "13px" }}
                >
                  Edit Qty
                </button>
                <button
                  onClick={() => {
                    if (confirm("Are you sure you want to cancel this order?")) {
                      cancelMutation.mutate(order.id);
                    }
                  }}
                  style={{ padding: "6px 12px", background: "rgba(239,68,68,0.1)", border: "1px solid rgba(239,68,68,0.2)", color: "#ff6b6b", borderRadius: "4px", cursor: "pointer", fontSize: "13px" }}
                >
                  Cancel
                </button>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

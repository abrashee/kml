// src/features/components/ShipmentTrackingCard.tsx
import { useQuery } from "@tanstack/react-query";
import { getSimulationLiveState } from "./shipments.api";
import type { CustomerShipment } from "../../types/Shipment";

interface CardProps {
  shipment: CustomerShipment;
}

export default function ShipmentTrackingCard({ shipment }: CardProps) {
  // Long poll the in-memory state matrix for live simulation trucks every 3.5 seconds
  const { data: liveSim } = useQuery({
    queryKey: ["liveSimulationStatus", shipment.id],
    queryFn: () => getSimulationLiveState(shipment.id),
    refetchInterval: 3500,
    enabled: shipment.status === "IN_TRANSIT"
  });

  const getStatusColor = (status: string) => {
    switch (status) {
      case "DELIVERED": return { bg: "rgba(16,185,129,0.12)", txt: "#34d399", border: "#065f46" };
      case "IN_TRANSIT": return { bg: "rgba(56,189,248,0.12)", txt: "#38bdf8", border: "#0369a1" };
      case "RETURNED": return { bg: "rgba(239,68,68,0.12)", txt: "#f87171", border: "#991b1b" };
      default: return { bg: "rgba(148,163,184,0.12)", txt: "#94a3b8", border: "#334155" };
    }
  };

  const colors = getStatusColor(shipment.status);

  return (
    <div style={{ padding: "24px", borderRadius: "8px", background: "var(--panel)", border: "1px solid var(--border)", display: "flex", flexDirection: "column", gap: "16px" }}>

      {/* HEADER SECTION */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", flexWrap: "wrap", gap: "12px" }}>
        <div>
          <span style={{ fontSize: "11px", opacity: 0.5, textTransform: "uppercase", letterSpacing: "0.5px", fontWeight: 600 }}>Tracking ID</span>
          <h3 style={{ margin: "2px 0 0 0", fontFamily: "monospace", fontSize: "15px", color: "var(--text-strong)" }}>{shipment.tracking}</h3>
          <p style={{ margin: "4px 0 0 0", fontSize: "12px", opacity: 0.6 }}>Order Reference Associated: #{shipment.orderId} • Carrier Matrix: {shipment.carrierInfo || 'Global Logistics'}</p>
        </div>
        <span style={{ fontSize: "11px", fontWeight: 700, padding: "4px 10px", borderRadius: "4px", backgroundColor: colors.bg, color: colors.txt, border: `1px solid ${colors.border}` }}>
          {shipment.status}
        </span>
      </div>

      {/* DYNAMIC PIPELINE TELEMETRY PROGRESS METRIC */}
      {shipment.status === "IN_TRANSIT" && (
        <div style={{ padding: "12px 16px", background: "rgba(0,0,0,0.15)", borderRadius: "6px", border: "1px solid var(--border)" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: "6px" }}>
            <span style={{ fontSize: "12px", fontWeight: 600, color: "var(--text-strong)" }}>
              📍 Current Location Mid-Transit:
            </span>
            <span style={{ fontSize: "13px", color: "#38bdf8", fontWeight: 700, fontFamily: "monospace" }}>
              {liveSim ? liveSim.currentLocation : "Origin Shipping Dock"}
            </span>
          </div>
          {liveSim && (
            <p style={{ margin: "6px 0 0 0", fontSize: "11px", opacity: 0.4, textAlign: "right" }}>
              Live telemetry signal received: {new Date(liveSim.timestamp).toLocaleTimeString()}
            </p>
          )}
        </div>
      )}

      {/* GEMINI ESTIMATED TIME ROUTING DATA */}
      <div style={{ borderTop: "1px solid var(--border)", paddingTop: "16px" }}>
        {shipment.lastRoutePlan ? (
          <div>
            <div style={{ display: "flex", justifyContent: "space-between", fontSize: "13px", marginBottom: "10px", flexWrap: "wrap", gap: "8px" }}>
              <div><span style={{ opacity: 0.5 }}>Estimated Delivery Arrival:</span> <strong style={{ color: "var(--text-strong)" }}>{new Date(shipment.lastRoutePlan.estimatedDeliveryTime).toLocaleString()}</strong></div>
              <div style={{ color: "var(--success)", fontWeight: 600 }}>AI Optimized Path Routing Stream Enabled</div>
            </div>

            {/* PIPELINE NODE PATH VISUAL */}
            <div style={{ marginTop: "12px" }}>
              <span style={{ fontSize: "11px", opacity: 0.5, fontWeight: 600, display: "block", marginBottom: "6px", textTransform: "uppercase" }}>
                Delivery Milestone Stops Sequence
              </span>
              <div style={{ display: "flex", flexWrap: "wrap", gap: "8px", alignItems: "center" }}>
                {shipment.lastRoutePlan.warehouseSequence.map((node, index, arr) => (
                  <div key={index} style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <span style={{ padding: "4px 8px", background: "rgba(255,255,255,0.02)", border: "1px solid var(--border)", borderRadius: "4px", fontSize: "12px", fontFamily: "monospace", color: "var(--text-strong)" }}>
                      {node}
                    </span>
                    {index < arr.length - 1 && <span style={{ opacity: 0.3, fontSize: "12px" }}>➔</span>}
                  </div>
                ))}
              </div>
            </div>
          </div>
        ) : (
          <div style={{ fontSize: "13px", opacity: 0.5, fontStyle: "italic" }}>
            Calculating optimized route and delivery timeline projections via Gemini Core Engine...
          </div>
        )}
      </div>

    </div>
  );
}

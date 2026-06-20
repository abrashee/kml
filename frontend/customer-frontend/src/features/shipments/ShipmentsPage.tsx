// src/features/ShipmentsPage.tsx
import { useShipmentSearch } from "./hooks/useShipmentSearch";
import ShipmentTrackingCard from "./ShipmentTrackingCard";

const shipmentStatuses = [
  { label: "All Statuses", value: null },
  { label: "Pending (Warehouse Packaging)", value: "PENDING" },
  { label: "In Transit (Overland/Maritime/Air)", value: "IN_TRANSIT" },
  { label: "Delivered to Destination", value: "DELIVERED" },
  { label: "Returned to Origin Hub", value: "RETURNED" },
];

export default function ShipmentsPage() {
  const {
    shipments,
    totalPages,
    currentPage,
    statusFilter,
    searchQuery,
    isLoading,
    isError,
    error,
    setPage,
    setStatusFilter,
    setSearchQuery,
    refetch,
  } = useShipmentSearch({ pageSize: 10, debounceMs: 500 });

  const hasMore = currentPage < totalPages - 1;
  const hasPrevious = currentPage > 0;

  return (
    <div
      className="shipments-viewport-canvas"
      style={{
        textAlign: "left",
        padding: "24px",
        maxWidth: "1200px",
        margin: "0 auto",
      }}
    >
      {/* Header Section */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "24px",
        }}
      >
        <div>
          <h2
            style={{
              fontSize: "22px",
              margin: "0 0 4px 0",
              fontWeight: 700,
              color: "var(--text-strong)",
              letterSpacing: "-0.5px",
            }}
          >
            Delivery Tracker
          </h2>
          <p
            style={{
              margin: 0,
              fontSize: "14px",
              color: "var(--text)",
              opacity: 0.6,
            }}
          >
            Monitor real-time updates and AI routing paths for your active order deliveries.
          </p>
        </div>
        <button
          onClick={() => refetch()}
          style={{
            padding: "8px 14px",
            background: "var(--panel)",
            border: "1px solid var(--border)",
            color: "var(--text-strong)",
            borderRadius: "6px",
            cursor: "pointer",
            fontSize: "13px",
            fontWeight: 500,
          }}
        >
          ↻ Refresh Status
        </button>
      </div>

      {/* Filter Strip */}
      <div
        style={{
          display: "flex",
          gap: "12px",
          marginBottom: "24px",
          alignItems: "center",
          flexWrap: "wrap",
        }}
      >
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search by tracking number or address..."
          style={{
            flex: "1 1 250px",
            padding: "8px 12px",
            background: "var(--panel)",
            border: "1px solid var(--border)",
            color: "var(--text-strong)",
            borderRadius: "6px",
            fontSize: "13px",
            minWidth: "200px",
          }}
        />
        <select
          value={statusFilter || ""}
          onChange={(e) => setStatusFilter(e.target.value || null)}
          style={{
            flex: "0 1 200px",
            padding: "8px 12px",
            background: "var(--panel)",
            border: "1px solid var(--border)",
            color: "var(--text-strong)",
            borderRadius: "6px",
            fontSize: "13px",
          }}
        >
          {shipmentStatuses.map((status) => (
            <option key={status.value || "all"} value={status.value || ""}>
              {status.label}
            </option>
          ))}
        </select>
      </div>

      {/* Replace the shipments mapping block with this conditional gate */}
      {!isLoading && !isError && (
        <>
          <div
            className="ship-grid"
            style={{ display: "flex", flexDirection: "column", gap: "24px" }}
          >
            {(!shipments || shipments.length === 0) && (
              <div style={{ opacity: 0.5, padding: "40px", textAlign: "center" }}>
                No active shipment distribution paths matching your filter criteria.
              </div>
            )}

            {shipments?.map((shipment) => (
              <ShipmentTrackingCard key={shipment.id} shipment={shipment} />
            ))}
          </div>
        </>
      )}

      {/* Error State */}
      {isError && (
        <div
          style={{
            textAlign: "center",
            padding: "40px",
            color: "var(--danger)",
            background: "rgba(239, 68, 68, 0.02)",
            border: "1px solid rgba(239, 68, 68, 0.1)",
            borderRadius: "8px",
          }}
        >
          <h4 style={{ margin: "0 0 4px 0", fontWeight: 600 }}>
            Tracking Server Unreachable
          </h4>
          <p style={{ fontSize: "13.5px", opacity: 0.7, margin: "0 0 12px 0" }}>
            {error?.message ||
              "We are unable to sync with the warehouse telemetry nodes."}
          </p>
          <button
            onClick={() => refetch()}
            style={{
              padding: "6px 12px",
              background: "var(--panel)",
              border: "1px solid var(--border)",
              color: "var(--text-strong)",
              borderRadius: "4px",
              cursor: "pointer",
              fontSize: "12px",
            }}
          >
            Try Again
          </button>
        </div>
      )}

      {/* Content */}
      {!isLoading && !isError && (
        <>
          <div
            className="ship-grid"
            style={{ display: "flex", flexDirection: "column", gap: "24px" }}
          >
            {shipments.length === 0 && (
              <div
                style={{
                  opacity: 0.5,
                  padding: "40px",
                  background: "var(--panel)",
                  border: "1px dashed var(--border)",
                  borderRadius: "8px",
                  textAlign: "center",
                  fontSize: "14px",
                }}
              >
                No active shipment distribution paths matching your filter criteria.
              </div>
            )}

            {shipments.map((shipment) => (
              <ShipmentTrackingCard key={shipment.id} shipment={shipment} />
            ))}
          </div>

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                marginTop: "24px",
                paddingTop: "16px",
                borderTop: "1px solid var(--border)",
              }}
            >
              <span style={{ fontSize: "13px", color: "var(--text)" }}>
                Page {currentPage + 1} of {totalPages}
              </span>
              <div style={{ display: "flex", gap: "8px" }}>
                <button
                  disabled={!hasPrevious}
                  onClick={() => setPage(currentPage - 1)}
                  style={{
                    padding: "6px 12px",
                    background: "var(--panel)",
                    border: "1px solid var(--border)",
                    color: "var(--text-strong)",
                    borderRadius: "4px",
                    cursor: hasPrevious ? "pointer" : "not-allowed",
                    fontSize: "12px",
                    fontWeight: 500,
                    opacity: hasPrevious ? 1 : 0.4,
                  }}
                >
                  ← Previous
                </button>
                <button
                  disabled={!hasMore}
                  onClick={() => setPage(currentPage + 1)}
                  style={{
                    padding: "6px 12px",
                    background: "var(--panel)",
                    border: "1px solid var(--border)",
                    color: "var(--text-strong)",
                    borderRadius: "4px",
                    cursor: hasMore ? "pointer" : "not-allowed",
                    fontSize: "12px",
                    fontWeight: 500,
                    opacity: hasMore ? 1 : 0.4,
                  }}
                >
                  Next →
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}

// src/features/DashboardCards.tsx
import { useNavigate } from "react-router-dom";

export default function DashboardCards() {
  const navigate = useNavigate();

  return (
    <div className="card-row">
      {[
        {
          title: "Current Orders",
          desc: "Active fulfillment pipeline",
          path: "/orders?tab=active", // Added query parameter
          color: "blue",
        },
        {
          title: "Order History",
          desc: "Completed transactions",
          path: "/orders?tab=history", // Added query parameter
          color: "purple",
        },
        {
          title: "Track Shipments",
          desc: "Live logistics tracking",
          path: "/shipments",
          color: "green",
        },
      ].map((c) => (
        <div
          key={c.title}
          className="card"
          onClick={() => navigate(c.path)}
        >
          <div className={`card-glow ${c.color}`} />
          <h3>{c.title}</h3>
          <p>{c.desc}</p>
        </div>
      ))}
    </div>
  );
}
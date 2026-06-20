// src / components / backButton.tsx
import { useNavigate, useLocation } from "react-router-dom";

export default function Breadcrumbs() {
  const navigate = useNavigate();
  const location = useLocation();

  // Home/Product Grid page
  if (location.pathname === "/") {
    return null;
  }

  return (
    <div style={{ marginBottom: "20px" }}>
      <button
        className="buy-btn"
        style={{
          width: "auto",
          padding: "10px 18px",
          marginTop: "20px",
        }}
        onClick={() => navigate(-1)}
      >
        ← Back
      </button>
    </div>
  );
}

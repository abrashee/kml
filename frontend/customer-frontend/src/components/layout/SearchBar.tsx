import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function SearchBar() {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const [context, setContext] = useState("Products");
  const navigate = useNavigate();

  const submitSearch = () => {
    const q = query.trim();

    if (context === "Orders") {
      navigate("/orders");
      return;
    }

    if (context === "Shipments") {
      navigate("/shipments");
      return;
    }

    navigate(q ? `/?search=${encodeURIComponent(q)}` : "/");
  };

  return (
    <div className="search-shell">
      <div className="search-bar">
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") submitSearch();
          }}
          placeholder="Search product indices, logistics orders, tracking SKUs..."
        />

        <button className="ghost" onClick={() => setOpen(!open)}>
          {open ? "Hide Filters" : "Filters"}
        </button>

        <button className="primary" onClick={submitSearch}>Search</button>
      </div>

      <div className={`filter-panel ${open ? "open" : ""}`}>
        <div className="filter-group">
          <label>Category Context</label>
          <select value={context} onChange={(e) => setContext(e.target.value)}>
            <option>Products</option>
            <option>Orders</option>
            <option>Shipments</option>
          </select>
        </div>

        <div className="filter-group">
          <label>Sort Ledger</label>
          <select disabled title="Sorting is not available yet">
            <option>Newest Entries</option>
            <option>Price: Low to High</option>
            <option>Price: High to Low</option>
          </select>
        </div>
      </div>
    </div>
  );
}

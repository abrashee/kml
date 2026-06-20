// src / components /SearchBar.tsx
import { useState } from "react";

export default function SearchBar() {
  const [open, setOpen] = useState(false);

  return (
    <div className="search-shell">
      <div className="search-bar">
        <input placeholder="Search product indices, logistics orders, tracking SKUs..." />

        <button className="ghost" onClick={() => setOpen(!open)}>
          {open ? "Hide Filters" : "Filters"}
        </button>

        <button className="primary">Search</button>
      </div>

      <div className={`filter-panel ${open ? "open" : ""}`}>
        <div className="filter-group">
          <label>Category Context</label>
          <select>
            <option>All Assets</option>
            <option>Products</option>
            <option>Orders</option>
            <option>Shipments</option>
          </select>
        </div>

        <div className="filter-group">
          <label>Sort Ledger</label>
          <select>
            <option>Newest Entries</option>
            <option>Price: Low to High</option>
            <option>Price: High to Low</option>
          </select>
        </div>
      </div>
    </div>
  );
}
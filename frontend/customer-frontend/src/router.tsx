// src / routes.tsx
import { createBrowserRouter, Navigate } from "react-router-dom";
import AppLayout from "./layouts/AppLayout";
import PublicLayout from "./layouts/PublicLayout";
import Login from "./features/auth/Login";
import ProductGrid from "./features/products/ProductGrid";
import ProductDetail from "./features/products/ProductDetail";
import OrdersPage from "./features/orders/OrdersPage";
import ShipmentsPage from "./features/shipments/ShipmentsPage";
// FIX: Changed from 'import type' to a normal component import so it resolves at runtime
import AccountSettings from "./features/profile/AccountSettings";
import { auth } from "./lib/auth";
import type { JSX } from "react/jsx-runtime";

function ProtectedRoute({ children }: { children: JSX.Element }) {
  const token = auth.getToken();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

// Add this helper function at the top of your router file to find product details
// Note: In production, swap this local match with your real array or fetch state!
const getProductNameById = (id: string | undefined): string => {
  if (!id) return "Unknown Product";
  // Quick placeholder lookup logic — matches your actual ledger data key arrays
  const mockProducts: Record<string, string> = {
    "1": "Industrial Steel Valve",
    "2": "Hydraulic Pressure Gauge",
    "3": "Thermal Coupler Hub"
  };
  return mockProducts[id] || `Product SKU: ${id}`;
};

export const router = createBrowserRouter([
  // PUBLIC AREA
  {
    element: <PublicLayout />,
    children: [
      {
        path: "/login",
        element: <Login />,
      },
    ],
  },

  // APP AREA (AUTH REQUIRED)
{
    element: (
      <ProtectedRoute>
        <AppLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        path: "/",
        element: <ProductGrid />,
      },
      {
        path: "/product/:id",
        element: <ProductDetail />,
        // CHIP INJECTION: We append a dynamic handle function that reads the URL params directly
        handle: {
          crumb: (params: { id?: string }) => getProductNameById(params.id)
        }
      },
      {
        path: "/orders",
        element: <OrdersPage />,
      },
      {
        path: "/shipments",
        element: <ShipmentsPage />,
      },
      {
        path: "/settings",
        element: <AccountSettings />,
      },
      {
        path: "*",
        element: <Navigate to="/" replace />,
      },
    ],
  },
]);
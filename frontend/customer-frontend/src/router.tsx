// src / routes.tsx
import { Suspense, lazy } from "react";
import { createBrowserRouter, Navigate } from "react-router-dom";
import AppLayout from "./layouts/AppLayout";
import PublicLayout from "./layouts/PublicLayout";
import { auth } from "./lib/auth";
import type { JSX } from "react/jsx-runtime";

const Login = lazy(() => import("./features/auth/Login"));
const ProductGrid = lazy(() => import("./features/products/ProductGrid"));
const ProductDetail = lazy(() => import("./features/products/ProductDetail"));
const OrdersPage = lazy(() => import("./features/orders/OrdersPage"));
const ShipmentsPage = lazy(() => import("./features/shipments/ShipmentsPage"));
const AccountSettings = lazy(() => import("./features/profile/AccountSettings"));

function PageLoader({ children }: { children: JSX.Element }) {
  return <Suspense fallback={<div style={{ padding: "24px" }}>Loading...</div>}>{children}</Suspense>;
}

function ProtectedRoute({ children }: { children: JSX.Element }) {
  const token = auth.getToken();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

const getProductNameById = (id: string | undefined): string => {
  if (!id) return "Unknown Product";
  const mockProducts: Record<string, string> = {
    "1": "Industrial Steel Valve",
    "2": "Hydraulic Pressure Gauge",
    "3": "Thermal Coupler Hub"
  };
  return mockProducts[id] || `Product SKU: ${id}`;
};

export const router = createBrowserRouter([
  {
    element: <PublicLayout />,
    children: [
      {
        path: "/login",
        element: <PageLoader><Login /></PageLoader>,
      },
    ],
  },
  {
    element: (
      <ProtectedRoute>
        <AppLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        path: "/",
        element: <PageLoader><ProductGrid /></PageLoader>,
      },
      {
        path: "/product/:id",
        element: <PageLoader><ProductDetail /></PageLoader>,
        handle: {
          crumb: (params: { id?: string }) => getProductNameById(params.id)
        }
      },
      {
        path: "/orders",
        element: <PageLoader><OrdersPage /></PageLoader>,
      },
      {
        path: "/shipments",
        element: <PageLoader><ShipmentsPage /></PageLoader>,
      },
      {
        path: "/settings",
        element: <PageLoader><AccountSettings /></PageLoader>,
      },
      {
        path: "*",
        element: <Navigate to="/" replace />,
      },
    ],
  },
]);

// src / layouts / AppLayout.tsx
import { Outlet, useLocation } from "react-router-dom";
import Header from "../components/layout/Header";
import Footer from "../components/layout/Footer";
import Breadcrumbs from "../components/navigation/BackButton";
import SearchBar from "../components/layout/SearchBar";
import DashboardCards from "../features/dashboard/DashboardCards";
import { motion, AnimatePresence } from "framer-motion";

export default function AppLayout() {
  const location = useLocation();
  const pathname = location.pathname;

  const isProductGrid = pathname === "/";
  const isOrders = pathname.startsWith("/orders");
  const isShipments = pathname.startsWith("/shipments");
  const isSettings = pathname.startsWith("/settings");
  const isProductDetail = pathname.startsWith("/product/");

  return (
    <div id="root">
      <Header />

      <div className="app-content-wrapper">

        {/* CHROME LAYER (UI ABOVE MAIN CONTENT) */}
        <div className="layout-canvas">

          {/* 1) PRODUCT GRID */}
          {isProductGrid && (
            <>
              <DashboardCards />
              <SearchBar />
            </>
          )}

          {/* 2) ORDERS + SHIPMENTS */}
          {(isOrders || isShipments) && (
            <>
              <Breadcrumbs />
              <DashboardCards />
              <SearchBar />
            </>
          )}

          {/* 3) SETTINGS */}
          {isSettings && (
            <>
              <Breadcrumbs />
            </>
          )}

          {/* 4) PRODUCT DETAIL */}
          {isProductDetail && (
            <>
              <Breadcrumbs />
            </>
          )}

          {/* MAIN ROUTE CONTENT (THIS WAS MISSING BEFORE) */}
          <AnimatePresence mode="popLayout" initial={false}>
            <motion.main
              key={pathname}
              className="app-content"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              transition={{ duration: 0.2, ease: [0.16, 1, 0.3, 1] }}
            >
              <Outlet />
            </motion.main>
          </AnimatePresence>

        </div>
      </div>

      <Footer />
    </div>
  );
}
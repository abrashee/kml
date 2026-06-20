// src / layouts / PublicLayout.tsx
import { Outlet, useLocation } from "react-router-dom";
import Header from "../components/layout/Header";
import Footer from "../components/layout/Footer";
import { motion, AnimatePresence } from "framer-motion";

export default function PublicLayout() {
  const location = useLocation();

  return (
    <div id="root">
      <Header />

      <div className="app-content-wrapper">
        <AnimatePresence mode="popLayout" initial={false}>
          <motion.main
            key={location.pathname}
            className="layout-canvas app-content"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.2, ease: [0.16, 1, 0.3, 1] }}
          >
            <Outlet />
          </motion.main>
        </AnimatePresence>
      </div>

      <Footer />
    </div>
  );
}
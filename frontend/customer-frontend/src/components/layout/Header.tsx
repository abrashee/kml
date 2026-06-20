// src / components / Header.tsx
import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { auth } from "../../lib/auth";
import { api, apiOrigin } from "../../lib/apiClient"; // Added to fetch real-time data
import { motion, AnimatePresence } from "framer-motion";
import logoAsset from "../../assets/kml_logo.svg";

interface HeaderUserData {
  name: string;
  avatarUrl: string | null;
}

export default function Header() {
  const [open, setOpen] = useState(false);
  const [userData, setUserData] = useState<HeaderUserData>({ name: "", avatarUrl: null });
  const navigate = useNavigate();
  // const user = auth.getUser();
  const user = auth.getUser() as any;

  const menuRef = useRef<HTMLDivElement>(null);

  // Sync profile details cleanly from backend mapping
  const fetchLiveProfile = async () => {
    if (!user) return;
    try {
      const res = await api.get("/users/me");

      // Handle prefix checks safely for your avatar pathing
      let rawUrl = res.data.avatarUrl;
      if (rawUrl && !rawUrl.startsWith("http")) {
        rawUrl = `${apiOrigin}${rawUrl}`;
      }

      setUserData({
        name: res.data.name || user.name,
        avatarUrl: rawUrl || null,
      });
    } catch (err) {
      console.error("Header profile sync failed", err);
      // Fail-safe: fallback directly to baseline auth session info if API is sleeping
      setUserData({
        name: user?.name || "",
        avatarUrl: user?.avatar || null
      });
    }
  };

  useEffect(() => {
    // Initial load on component initialization
    fetchLiveProfile();

    // Hook up listener for live notifications from AccountSettings
    window.addEventListener("user-profile-updated", fetchLiveProfile);
    return () => {
      window.removeEventListener("user-profile-updated", fetchLiveProfile);
    };
  }, []);

  const logout = () => {
    setOpen(false);
    auth.logout();
    navigate("/login");
  };

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setOpen(false);
      }
    }

    if (open) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [open]);

  return (
    <header className="header">
      <div className="layout-canvas">
        <div className="header-inner">

          <div className="header-left" onClick={() => { setOpen(false); navigate("/"); }}>
            <div className="brand">
              <img
                src={logoAsset}
                alt="KML Logo"
                style={{
                  height: "30px",
                  width: "auto",
                  objectFit: "contain",
                  display: "block"
                }}
              />
              <span className="brand-text">KML Logistics</span>
            </div>
          </div>

          {user && (
            <div className="header-right" ref={menuRef}>
              <div className="user" onClick={() => setOpen(!open)}>
                {/* Fallback to premium placeholder layout if both live photo context data points are missing */}
                <img
                  src={userData.avatarUrl || "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80"}
                  alt="User Avatar"
                  style={{ objectFit: "cover" }}
                />
                <div className="user-meta">
                  <span className="name">{userData.name || user.name}</span>
                </div>
              </div>

              <AnimatePresence>
                {open && (
                  <motion.div
                    className="dropdown"
                    initial={{ opacity: 0, y: 8, scale: 0.95 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    exit={{ opacity: 0, y: 8, scale: 0.95 }}
                    transition={{ duration: 0.15, ease: "easeOut" }}
                  >
                    <button
                      onClick={() => {
                        setOpen(false);
                        navigate("/settings");
                      }}
                    >
                      Account Settings
                    </button>
                    <div style={{ height: "1px", background: "var(--border)", margin: "4px 0" }} />
                    <button
                      onClick={logout}
                      style={{ color: "var(--danger)" }}
                    >
                      Sign Out System
                    </button>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          )}

        </div>
      </div>
    </header>
  );
}

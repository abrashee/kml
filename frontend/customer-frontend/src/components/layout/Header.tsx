// src/components/Header.tsx
import { useState, useEffect, useRef } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { auth } from "../../lib/auth";
import { apiOrigin } from "../../lib/apiClient";
import { getCurrentUserProfile } from "../../features/profile/profile.api";
import { motion, AnimatePresence } from "framer-motion";
import logoAsset from "../../assets/kml_logo.svg";

interface HeaderUserData {
  name: string;
  avatarUrl: string | null;
}

export default function Header() {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const user = auth.getUser() as any;

  const menuRef = useRef<HTMLDivElement>(null);

  const { data: liveProfile } = useQuery({
    queryKey: ["current-user-profile"],
    queryFn: getCurrentUserProfile,
    enabled: Boolean(user),
  });

  let rawAvatarUrl = liveProfile?.avatarUrl || user?.avatar || null;
  if (rawAvatarUrl && !rawAvatarUrl.startsWith("http")) {
    rawAvatarUrl = `${apiOrigin}${rawAvatarUrl}`;
  }

  const userData: HeaderUserData = {
    name: liveProfile?.name || user?.name || "",
    avatarUrl: rawAvatarUrl,
  };

  useEffect(() => {
    const syncProfile = () => {
      queryClient.invalidateQueries({ queryKey: ["current-user-profile"] });
    };

    window.addEventListener("user-profile-updated", syncProfile);
    return () => {
      window.removeEventListener("user-profile-updated", syncProfile);
    };
  }, [queryClient]);

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
                {/* Fallback to local default-avatar.png inside the public directory */}
                <img
                  src={userData.avatarUrl || "/default-avatar.png"}
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
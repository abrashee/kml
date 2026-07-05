// src / features / Login.tsx
import { useState } from "react";
import { loginRequest, registerRequest } from "./auth.api";
import { useNavigate } from "react-router-dom";
import { auth } from "../../lib/auth";
import { motion, AnimatePresence } from "framer-motion";

type Mode = "login" | "register";

const cardEase = [0.16, 1, 0.3, 1] as const;

export default function Login() {
  const [mode, setMode] = useState<Mode>("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [fullName, setFullName] = useState("");
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async () => {
    const cleanUsername = username.trim();
    const cleanPassword = password.trim();

    if (!cleanUsername || !cleanPassword) {
      setErrorMsg("Username and password are required.");
      return;
    }

    setLoading(true);
    setErrorMsg(null);

    try {
      const responseData = await loginRequest({ username: cleanUsername, password: cleanPassword });
      auth.setToken(responseData.accessToken);

      try {
        const payloadBase64 = responseData.accessToken.split(".")[1];
        const claims = JSON.parse(atob(payloadBase64));
        const userId = claims.userId;
        if (!userId) throw new Error("Missing userId claim");
        auth.setUser({ id: String(userId), name: claims.name || claims.sub || cleanUsername, role: claims.role || "CUSTOMER" });
      } catch {
        auth.logout();
        throw new Error("Login succeeded but token did not include a valid userId");
      }
      navigate("/");
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || err.response?.data || "Invalid username or password.");
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async () => {
    const cleanUsername = username.trim();
    const cleanPassword = password.trim();
    const cleanFullName = fullName.trim();

    if (!cleanUsername || !cleanPassword || !cleanFullName) {
      setErrorMsg("All registration fields are required.");
      return;
    }

    setLoading(true);
    setErrorMsg(null);
    setSuccessMsg(null);

    try {
      await registerRequest({
        username: cleanUsername,
        password: cleanPassword,
        name: cleanFullName
      });

      setSuccessMsg("Account created successfully! You can now sign in.");
      setMode("login");
      setPassword("");
      setFullName("");
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || err.response?.data || "Registration failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-shell" style={{ position: "relative", minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", overflow: "hidden" }}>
      <motion.div
        className="login-bg-glow"
        animate={{
          scale: [1, 1.05, 1],
          opacity: [0.4, 0.5, 0.4]
        }}
        transition={{
          duration: 8,
          repeat: Infinity,
          ease: "easeInOut"
        }}
      />

      <motion.div
        className="login-card"
        layout
        initial={{ opacity: 0, y: 30, scale: 0.98 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.5, ease: cardEase }}
      >
        <div className="login-brand">
          Digital Storefront
        </div>

        <h2 style={{ margin: "4px 0 20px 0" }}>
          {mode === "login" ? "Sign In" : "Create Account"}
        </h2>

        <AnimatePresence mode="wait">
          {errorMsg && (
            <motion.div
              className="auth-alert error"
              initial={{ opacity: 0, height: 0, y: -10 }}
              animate={{ opacity: 1, height: "auto", y: 0 }}
              exit={{ opacity: 0, height: 0, y: -10 }}
              transition={{ duration: 0.25, ease: cardEase }}
              style={{ display: "flex", alignItems: "center", gap: "8px" }}
            >
              <span>✕</span> {errorMsg}
            </motion.div>
          )}
          {successMsg && (
            <motion.div
              className="auth-alert success"
              initial={{ opacity: 0, height: 0, y: -10 }}
              animate={{ opacity: 1, height: "auto", y: 0 }}
              exit={{ opacity: 0, height: 0, y: -10 }}
              transition={{ duration: 0.25, ease: cardEase }}
              style={{ display: "flex", alignItems: "center", gap: "8px" }}
            >
              <span>✓</span> {successMsg}
            </motion.div>
          )}
        </AnimatePresence>

        <div className="form-fields">
          <AnimatePresence initial={false} mode="popLayout">
            {mode === "register" && (
              <motion.div
                className="form-group"
                initial={{ opacity: 0, x: -15, height: 0, marginBottom: 0 }}
                animate={{ opacity: 1, x: 0, height: "auto", marginBottom: 20 }}
                exit={{ opacity: 0, x: 15, height: 0, marginBottom: 0 }}
                transition={{ duration: 0.3, ease: cardEase }}
                style={{ overflow: "hidden" }}
              >
                <label>Full Name</label>
                <input
                  type="text"
                  placeholder="Enter your full name"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                />
              </motion.div>
            )}
          </AnimatePresence>

          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>

          <div className="form-group" style={{ marginBottom: "24px" }}>
            <label>Password</label>
            <input
              type="password"
              placeholder="••••••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") mode === "login" ? handleLogin() : handleRegister();
              }}
            />
          </div>
        </div>

        <motion.button
          className="submit-btn"
          onClick={mode === "login" ? handleLogin : handleRegister}
          disabled={loading}
          whileHover={loading ? {} : { y: -2, boxShadow: "0 4px 12px rgba(var(--primary-rgb), 0.2)" }}
          whileTap={loading ? {} : { scale: 0.98, y: 0 }}
          transition={{ duration: 0.2, ease: "easeOut" }}
        >
          {loading ? "Authenticating..." : mode === "login" ? "Sign In" : "Create Account"}
        </motion.button>

        <div className="toggle-link-container">
          <span>{mode === "login" ? "New to our store?" : "Already have an account?"}</span>
          <motion.button
            className="toggle-link"
            onClick={() => {
              setMode(mode === "login" ? "register" : "login");
              setErrorMsg(null);
              setSuccessMsg(null);
            }}
            whileHover={{ scale: 1.03 }}
            whileTap={{ scale: 0.97 }}
          >
            {mode === "login" ? "Sign Up" : "Back to Sign In"}
          </motion.button>
        </div>
      </motion.div>
    </div>
  );
}

export const motionConfig = {
  durationFast: 0.15,
  durationBase: 0.25,
  durationSlow: 0.4,

  easeOut: [0.16, 1, 0.3, 1],
  easeInOut: [0.65, 0, 0.35, 1],

  page: {
    initial: { opacity: 0, y: 8 },
    animate: { opacity: 1, y: 0 },
    exit: { opacity: 0, y: -6 },
  },

  card: {
    hover: { y: -4, scale: 1.01 },
    tap: { scale: 0.98 },
  },

  fade: {
    initial: { opacity: 0 },
    animate: { opacity: 1 },
  },
};

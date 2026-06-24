// src/features/AccountSettings.tsx
import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { api, apiOrigin } from "../../lib/apiClient";

interface UserIdentity {
  name: string;
  username: string;
  password?: string;
  role: "CUSTOMER";
}

interface CustomerProfile {
  avatarUrl: string | null;
  address: {
    street: string;
    houseNo: string;
    zipCode: string;
    city: string;
    country: string;
  };
}

export default function AccountSettings() {
  const navigate = useNavigate();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [editingFields, setEditingFields] = useState<Record<string, boolean>>({});
  const [loading, setLoading] = useState<boolean>(true);

  // Core Identity State
  const [identity, setIdentity] = useState<UserIdentity>({
    name: "",
    username: "",
    password: "",
    role: "CUSTOMER",
  });

  // Role-Specific Customer Profile State
  const [profile, setProfile] = useState<CustomerProfile>({
    avatarUrl: null,
    address: {
      street: "",
      houseNo: "",
      zipCode: "",
      city: "",
      country: "",
    },
  });

  // Pristine tracking states for rolling back modifications
  const [pristineIdentity, setPristineIdentity] = useState<UserIdentity>({ ...identity });
  const [pristineProfile, setPristineProfile] = useState<CustomerProfile>({
    ...profile,
    address: { ...profile.address },
  });

  // 1. Fetch current customer data and parse the combined address string
  useEffect(() => {
    api.get("/users/me")
      .then((res) => {
        const data = res.data;

        const freshIdentity: UserIdentity = {
          name: data.name || "",
          username: data.username || "",
          password: "",
          role: "CUSTOMER"
        };

        // PARSING LOGIC: Break down the single string back into form fields
        let street = "";
        let houseNo = "";
        let zipCode = "";
        let city = "";
        let country = "";

        if (data.address) {
          const parts = data.address.split(", ");
          // Fallback protection in case data format is irregular or legacy
          street = parts[0] || "";
          houseNo = parts[1] || "";
          zipCode = parts[2] || "";
          city = parts[3] || "";
          country = parts[4] || "";
        }

        const freshProfile: CustomerProfile = {
          avatarUrl: data.avatarUrl || null,
          address: { street, houseNo, zipCode, city, country }
        };

        setIdentity(freshIdentity);
        setProfile(freshProfile);
        setPristineIdentity({ ...freshIdentity });
        setPristineProfile({ ...freshProfile, address: { ...freshProfile.address } });
        setLoading(false);
      })
      .catch((err) => {
        console.error("Failed to load user profile context data", err);
        setLoading(false);
      });
  }, []);

  const validateField = (fieldKey: string, value: string): string => {
    if (!value.trim() && fieldKey !== "password") return "This field cannot be left blank.";
    if (fieldKey === "zipCode" && !/^\d+$/.test(value)) {
      return "Zip code must contain numerical digits only.";
    }
    return "";
  };

  const handleIdentityChange = (key: keyof UserIdentity, value: string) => {
    setIdentity((prev) => ({ ...prev, [key]: value }));
    setErrors((prev) => ({ ...prev, [key]: validateField(key, value) }));
  };

  const handleAddressChange = (key: keyof CustomerProfile["address"], value: string) => {
    setProfile((prev) => ({
      ...prev,
      address: { ...prev.address, [key]: value },
    }));
    setErrors((prev) => ({ ...prev, [key]: validateField(key, value) }));
  };

  const handleAvatarClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (file.size > 1024 * 1024) {
      alert("File is too large. Image file size must be less than 1MB.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      setToastMessage("Uploading new profile image...");
      const res = await api.post("/users/me/avatar", formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });

      const freshAvatarUrl = `${apiOrigin}${res.data.avatarUrl}`;
      setProfile(prev => ({ ...prev, avatarUrl: freshAvatarUrl }));

      // Notify components like Header to sync updated avatar changes
      window.dispatchEvent(new CustomEvent("user-profile-updated"));

      setToastMessage("Avatar updated successfully!");
      setTimeout(() => setToastMessage(null), 2000);
    } catch (err) {
      console.error("Avatar upload error", err);
      setToastMessage("Failed to update avatar image selection.");
      setTimeout(() => setToastMessage(null), 3000);
    }
  };

  const toggleEdit = (fieldId: string) => {
    setEditingFields((prev) => ({ ...prev, [fieldId]: !prev[fieldId] }));
  };

  const handleDiscard = () => {
    setIdentity({ ...pristineIdentity });
    setProfile({ ...pristineProfile, address: { ...pristineProfile.address } });
    setEditingFields({});
    setErrors({});
    navigate("/");
  };

  // 2. Serialize separate fields into a flat string payload right before dispatching
  const handleSave = async () => {
    const newErrors: Record<string, string> = {};

    Object.keys(identity).forEach((k) => {
      if (k !== "role") {
        const err = validateField(k, identity[k as keyof UserIdentity] || "");
        if (err) newErrors[k] = err;
      }
    });

    Object.keys(profile.address).forEach((k) => {
      const err = validateField(k, profile.address[k as keyof CustomerProfile["address"]]);
      if (err) newErrors[k] = err;
    });

    if (Object.values(newErrors).some((x) => x !== "")) {
      setErrors(newErrors);
      return;
    }

    // SERIALIZATION LOGIC: Merge properties safely into a comma-delimited single string
    const addr = profile.address;
    const combinedAddressString = `${addr.street}, ${addr.houseNo}, ${addr.zipCode}, ${addr.city}, ${addr.country}`;

    // Prepare payload matching UserRequestDto format perfectly
    const savePayload = {
      name: identity.name,
      username: identity.username,
      role: "CUSTOMER", // Matches @JsonProperty("role") variable serialization mapping
      ...(identity.password ? { password: identity.password } : {}),
      avatarUrl: profile.avatarUrl,
      address: combinedAddressString // Matches 'private String address;' inside DTO
    };

    try {
      await api.put("/users/me/profile", savePayload);

      // Notify components like Header to sync updated text profile data points
      window.dispatchEvent(new CustomEvent("user-profile-updated"));

      setPristineIdentity({ ...identity });
      setPristineProfile({ ...profile, address: { ...profile.address } });
      setEditingFields({});

      setToastMessage("Your profile and identity settings have been successfully updated.");
      setTimeout(() => {
        setToastMessage(null);
        navigate("/");
      }, 2200);
    } catch (err) {
      console.error("Profile updates failed to settle", err);
      setToastMessage("An error occurred while saving profile settings.");
    }
  };

  if (loading) {
    return <div style={{ padding: "40px", color: "var(--text-muted)" }}>Loading account information...</div>;
  }

  return (
    <div className="settings-viewport" style={{ textAlign: "left", position: "relative" }}>
      {toastMessage && (
        <div className="system-toast-alert">
          <div className="toast-icon-check">✓</div>
          <div className="toast-text-body">{toastMessage}</div>
        </div>
      )}

      <h2 style={{ fontSize: "22px", marginBottom: "24px", color: "var(--text-strong)", fontWeight: 600 }}>
        Account Profile Settings
      </h2>

      <div className="settings-panel core-unified-layout">
        <div className="integrated-avatar-center-wrapper">
          <div className="avatar-frame interactive-camera-frame" onClick={handleAvatarClick} style={{ cursor: "pointer", overflow: "hidden", position: "relative" }}>
            <img
              src={profile.avatarUrl || "/default-avatar.png"}
              alt="Avatar"
              style={{ width: "100%", height: "100%", objectFit: "cover" }}
            />
            <div className="camera-overlay">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="camera-icon-svg">
                <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z" />
                <circle cx="12" cy="13" r="3" />
              </svg>
            </div>
          </div>
          <input type="file" ref={fileInputRef} style={{ display: "none" }} accept="image/png, image/jpeg, image/jpg, image/webp" onChange={handleFileChange} />
          <div className="avatar-meta-centered">
            <h4>Profile Picture</h4>
            <p>Click image to upload a PNG or JPG file (max 1MB).</p>
          </div>
        </div>

        <hr style={{ border: "none", borderTop: "1px solid var(--border)", margin: "24px 0" }} />
        <h3 style={{ fontSize: "13px", textTransform: "uppercase", letterSpacing: "0.5px", margin: "0 0 16px 0", opacity: 0.6 }}>Personal Information</h3>

        <div className="settings-row-item">
          <div className="field-info-split">
            <label>Full Name</label>
            {editingFields.name ? (
              <input type="text" value={identity.name} onChange={(e) => handleIdentityChange("name", e.target.value)} />
            ) : (
              <div className="read-only-text">{identity.name}</div>
            )}
            {errors.name && <span className="field-error-log">{errors.name}</span>}
          </div>
          <button className={`inline-edit-trigger ${editingFields.name ? "active" : ""}`} onClick={() => toggleEdit("name")}>
            {editingFields.name ? "Lock" : "Edit"}
          </button>
        </div>

        <div className="settings-row-item">
          <div className="field-info-split">
            <label>Username</label>
            {editingFields.username ? (
              <input type="text" value={identity.username} onChange={(e) => handleIdentityChange("username", e.target.value)} />
            ) : (
              <div className="read-only-text">@{identity.username}</div>
            )}
            {errors.username && <span className="field-error-log">{errors.username}</span>}
          </div>
          <button className={`inline-edit-trigger ${editingFields.username ? "active" : ""}`} onClick={() => toggleEdit("username")}>
            {editingFields.username ? "Lock" : "Edit"}
          </button>
        </div>

        <div className="settings-row-item">
          <div className="field-info-split">
            <label>Password</label>
            {editingFields.password ? (
              <input type="password" placeholder="Enter new password" value={identity.password} onChange={(e) => handleIdentityChange("password", e.target.value)} />
            ) : (
              <div className="read-only-text">••••••••</div>
            )}
            {errors.password && <span className="field-error-log">{errors.password}</span>}
          </div>
          <button className={`inline-edit-trigger ${editingFields.password ? "active" : ""}`} onClick={() => toggleEdit("password")}>
            {editingFields.password ? "Lock" : "Edit"}
          </button>
        </div>

        <hr style={{ border: "none", borderTop: "1px solid var(--border)", margin: "24px 0" }} />
        <h3 style={{ fontSize: "13px", textTransform: "uppercase", letterSpacing: "0.5px", margin: "0 0 16px 0", opacity: 0.6 }}>Customer Delivery Address</h3>

        <div className="settings-row-item address-block">
          <div className="field-info-split grid-address-inputs">
            <div className="sub-input-node">
              <label className="sub-tag">Street Address</label>
              {editingFields.address ? (
                <input type="text" value={profile.address.street} onChange={(e) => handleAddressChange("street", e.target.value)} />
              ) : (
                <div className="read-only-text">{profile.address.street || "Not provided"}</div>
              )}
              {errors.street && <span className="field-error-log">{errors.street}</span>}
            </div>

            <div className="sub-input-node">
              <label className="sub-tag">House / Apt No.</label>
              {editingFields.address ? (
                <input type="text" value={profile.address.houseNo} onChange={(e) => handleAddressChange("houseNo", e.target.value)} />
              ) : (
                <div className="read-only-text">{profile.address.houseNo || "Not provided"}</div>
              )}
              {errors.houseNo && <span className="field-error-log">{errors.houseNo}</span>}
            </div>

            <div className="sub-input-node">
              <label className="sub-tag">Zip Code</label>
              {editingFields.address ? (
                <input type="text" value={profile.address.zipCode} onChange={(e) => handleAddressChange("zipCode", e.target.value)} />
              ) : (
                <div className="read-only-text">{profile.address.zipCode || "Not provided"}</div>
              )}
              {errors.zipCode && <span className="field-error-log">{errors.zipCode}</span>}
            </div>

            <div className="sub-input-node">
              <label className="sub-tag">City</label>
              {editingFields.address ? (
                <input type="text" value={profile.address.city} onChange={(e) => handleAddressChange("city", e.target.value)} />
              ) : (
                <div className="read-only-text">{profile.address.city || "Not provided"}</div>
              )}
              {errors.city && <span className="field-error-log">{errors.city}</span>}
            </div>

            <div className="sub-input-node full-width-node">
              <label className="sub-tag">Country</label>
              {editingFields.address ? (
                <input type="text" value={profile.address.country} onChange={(e) => handleAddressChange("country", e.target.value)} />
              ) : (
                <div className="read-only-text">{profile.address.country || "Not provided"}</div>
              )}
              {errors.country && <span className="field-error-log">{errors.country}</span>}
            </div>
          </div>
          <button className={`inline-edit-trigger ${editingFields.address ? "active" : ""}`} style={{ alignSelf: "flex-start", marginTop: "24px" }} onClick={() => toggleEdit("address")}>
            {editingFields.address ? "Lock" : "Edit"}
          </button>
        </div>

        <div className="form-commit-bar">
          <button className="cancel-btn" onClick={handleDiscard}>
            Discard Changes
          </button>
          <button className="save-btn" onClick={handleSave}>
            Save Changes
          </button>
        </div>
      </div>
    </div>
  );
}

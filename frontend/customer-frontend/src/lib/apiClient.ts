// src / lib / apiClient.ts
import axios from "axios";
import { auth } from "./auth";

type ApiEnvelope<T = unknown> = {
  success: boolean;
  data: T;
  message?: string;
  timestamp?: string;
};

const isApiEnvelope = (value: unknown): value is ApiEnvelope =>
  !!value &&
  typeof value === "object" &&
  "success" in value &&
  "data" in value;

export const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1").replace(/\/$/, "");
export const apiOrigin = new URL(apiBaseUrl).origin;

export const api = axios.create({
  baseURL: apiBaseUrl,
  withCredentials: true,
});

// Auto-inject current Access Token into headers
api.interceptors.request.use((config) => {
  const token = auth.getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

// Response interceptor to handle token expiration gracefully
api.interceptors.response.use(
  (response) => {
    if (isApiEnvelope(response.data)) {
      response.data = response.data.data;
    }
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const res = await axios.post(`${apiBaseUrl}/auth/refresh`, {}, { withCredentials: true });

        const tokenPayload = isApiEnvelope(res.data) ? res.data.data : res.data;
        const { accessToken } = tokenPayload;
        auth.setToken(accessToken);

        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        auth.logout();
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

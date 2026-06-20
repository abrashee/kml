// src / features / auth.api.ts
import { api } from "../../lib/apiClient";

export interface LoginRequestPayload {
  username: string;
  password: string;
}

export interface RegisterRequestPayload {
  username: string;
  password: string;
  name?: string;
}

export interface TokenResponse {
  accessToken: string;
  tokenType: string;
}

/**
 * Sends customer credentials for secure storefront session generation.
 */
export async function loginRequest(payload: LoginRequestPayload): Promise<TokenResponse> {
  const body = {
    username: payload.username,
    password: payload.password
  };

  const res = await api.post<TokenResponse>("/auth/login", body);
  return res.data;
}

/**
 * Submits new user data to register an authenticated customer account.
 */
export async function registerRequest(payload: RegisterRequestPayload): Promise<any> {
  const body = {
    username: payload.username,
    password: payload.password,
    name: payload.name,
    role: "CUSTOMER",
    userRole: "CUSTOMER"
  };

  const res = await api.post("/users/register/customer", body);
  return res.data;
}

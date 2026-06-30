import { api } from "../../lib/apiClient";

export type CurrentUserProfile = {
  id?: number;
  name?: string;
  username?: string;
  role?: string;
  avatarUrl?: string | null;
  address?: string | null;
};

export async function getCurrentUserProfile(): Promise<CurrentUserProfile> {
  const res = await api.get("/users/me");
  return res.data;
}

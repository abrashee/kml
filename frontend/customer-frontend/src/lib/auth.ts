// // // // src / lib / auth.ts
export interface AuthUser {
  id: string;
  name: string;
  role: string;
}

const TOKEN_KEY = "kml_access_token";
const USER_KEY = "kml_user";

export const auth = {
  /**
   * Retrieves the current stored access token string
   */
  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  },

  /**
   * Sets the current access token string in safe storage
   */
  setToken(token: string): void {
    sessionStorage.setItem(TOKEN_KEY, token);
  },

  /**
   * Retrieves the parsed metadata payload of the authenticated principal
   */
  getUser(): AuthUser | null {
    const data = localStorage.getItem(USER_KEY);
    if (!data) return null;
    try {
      return JSON.parse(data) as AuthUser;
    } catch {
      return null;
    }
  },

  /**
   * Caches user info details to handle state representation locally
   */
  setUser(user: AuthUser): void {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },

  /**
   * Purges all locally held security secrets to reset authorization boundaries
   */
  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }
};

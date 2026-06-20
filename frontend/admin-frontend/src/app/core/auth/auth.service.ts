import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserRole, User } from '../../features/users/models/user.model';

interface LoginResponse {
  accessToken: string;
  tokenType: string;
}

interface JwtPayload {
  sub: string;
  role: UserRole;
  exp: number;
  id?: number;
  name?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  // Tracks active authenticated session context reactively
  currentUser = signal<User | null>(null);

  constructor() {
    // 1. Immediately read the token payload synchronously so route guards function instantly
    this.restoreUserFromToken();

    // 2. BREAK CIRCULAR DEPENDENCY: Defer background API call until after compilation hooks settle
    queueMicrotask(() => {
      this.refreshProfileMetricsFromServer();
    });
  }

  private restoreUserFromToken(): void {
    const token = this.getToken();
    if (token) {
      const payload = this.decodeJwt(token);
      if (payload && payload.exp * 1000 > Date.now()) {
        this.setUserFromPayload(payload);
      } else {
        this.clearTokens();
      }
    }
  }

  /**
   * Fetches the latest database column states from Spring Boot
   * to hydrate values that are missing inside raw token payloads (like avatarUrl)
   */
  private refreshProfileMetricsFromServer(): void {
    if (!this.getToken()) return;

    this.http.get<any>(`${environment.apiUrl}/api/v1/users/me`).subscribe({
      next: (fullUser) => {
        this.updateCurrentUserState({
          avatarUrl: fullUser.avatarUrl,
          name: fullUser.name,
          warehouseId: fullUser.warehouseId,
          // 🛑 storageId has been successfully removed from the mapping
          address: fullUser.address
        });
      },
      error: (err) => {
        console.error('Failed to update background profile metrics:', err);
      }
    });
  }

  private decodeJwt(token: string): JwtPayload | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;
      const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');
      return JSON.parse(atob(padded)) as JwtPayload;
    } catch {
      return null;
    }
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/api/v1/auth/login`, { username, password }, { withCredentials: true })
      .pipe(
        tap(resp => {
          sessionStorage.setItem('access_token', resp.accessToken);
          const payload = this.decodeJwt(resp.accessToken);
          if (payload) {
            this.setUserFromPayload(payload);
            // Re-fetch profile immediately upon manual logins to get avatar path
            this.refreshProfileMetricsFromServer();
          }
        })
      );
  }

  logout(): void {
    this.clearTokens();
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  updateCurrentUserState(updatedFields: Partial<User>): void {
    this.currentUser.update(current => {
      if (!current) return null;
      return { ...current, ...updatedFields };
    });
  }

  private setUserFromPayload(payload: JwtPayload): void {
    this.currentUser.set({
      id: payload.id || 0,
      username: payload.sub,
      name: payload.name || payload.sub,
      role: payload.role,
      avatarUrl: this.currentUser()?.avatarUrl || undefined, // Maintain pre-existing image path if loaded
      createdAt: new Date().toISOString()
    });
  }

  private clearTokens(): void {
    sessionStorage.removeItem('access_token');
  }

  getToken(): string | null {
    return sessionStorage.getItem('access_token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken() && this.currentUser() !== null;
  }

  refreshToken(): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/api/v1/auth/refresh`, {}, { withCredentials: true })
      .pipe(
        tap(resp => {
          sessionStorage.setItem('access_token', resp.accessToken);
        })
      );
  }

  fetchUserProfile(): Observable<User> {
    return this.http.get<any>(`${environment.apiUrl}/api/v1/users/me`).pipe(
      tap(fullUser => {
        this.updateCurrentUserState({
          ...fullUser,
          role: fullUser.role || this.currentUser()?.role
        });
      })
    );
  }
}

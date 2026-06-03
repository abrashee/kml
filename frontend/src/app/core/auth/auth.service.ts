import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserRole } from '../models/user.model';

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

interface JwtPayload {
  sub: string;
  role: UserRole;
  exp: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  currentUser = signal<{ username: string; role: UserRole } | null>(null);

  constructor() {
    this.restoreUserFromToken();
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

  private restoreUserFromToken(): void {
    const token = this.getToken();
    if (token) {
      const payload = this.decodeJwt(token);
      if (payload && payload.exp * 1000 > Date.now()) {
        this.currentUser.set({ username: payload.sub, role: payload.role });
      } else {
        this.clearTokens();
      }
    }
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/api/v1/auth/login`, { username, password })
      .pipe(
        tap(resp => {
          localStorage.setItem('access_token', resp.accessToken);
          localStorage.setItem('refresh_token', resp.refreshToken);
          const payload = this.decodeJwt(resp.accessToken);
          if (payload) {
            this.currentUser.set({ username: payload.sub, role: payload.role });
          }
        })
      );
  }

  logout(): void {
    this.clearTokens();
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  private clearTokens(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
  }

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken() && this.currentUser() !== null;
  }

  refreshToken(): Observable<LoginResponse> {
    const refreshToken = localStorage.getItem('refresh_token');
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/api/v1/auth/refresh`, { refreshToken })
      .pipe(
        tap(resp => {
          localStorage.setItem('access_token', resp.accessToken);
          if (resp.refreshToken) {
            localStorage.setItem('refresh_token', resp.refreshToken);
          }
        })
      );
  }
}

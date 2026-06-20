// src/app/layout/navbar.ts
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  template: `
    <nav class="navbar">
      <a class="navbar-brand" routerLink="/dashboard">KML Fulfillment</a>
      <div class="navbar-right">
        @if (authService.currentUser(); as user) {
          <a routerLink="/profile" class="navbar-user-profile" style="text-decoration: none;">
            <img
              [src]="getNavbarAvatar(user)"
              alt="Avatar"
              class="navbar-avatar-thumbnail"
              (error)="handleAvatarError($event)"
            />
            <span class="badge badge-info">{{ user.username }}</span>
          </a>
          <span class="badge badge-secondary">{{ user.role }}</span>
        }
        <button class="btn btn-outline btn-sm" (click)="authService.logout()">Logout</button>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      position: fixed; top: 0; left: 0; right: 0; height: 60px;
      background: var(--surface); border-bottom: 1px solid var(--border);
      display: flex; align-items: center; justify-content: space-between;
      padding: 0 24px; z-index: 100;
    }
    .navbar-brand { font-size: 18px; font-weight: 700; color: var(--primary); text-decoration: none; }
    .navbar-right { display: flex; align-items: center; gap: 12px; }
    .navbar-user-profile { display: flex; align-items: center; gap: 8px; font-size: 14px; color: var(--text-muted); }
    .navbar-avatar-thumbnail { width: 28px; height: 28px; border-radius: 50%; object-fit: cover; border: 1px solid var(--border); background: #cbd5e1; }
  `]
})
export class NavbarComponent {
  protected authService = inject(AuthService);

  getNavbarAvatar(user: any): string {
    if (user?.avatarUrl) {
      return user.avatarUrl.startsWith('http') ? user.avatarUrl : `${environment.apiUrl}${user.avatarUrl}`;
    }
    // Return empty string or local fallback pathway
    return 'default-avatar.png';
  }

  // BREAKS THE INFINITE LOOP:
  handleAvatarError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;

    // 1. Prevent the error listener from firing again
    imgElement.onerror = null;

    // 2. Fall back to an inline SVG or transparent layout if your local asset directory isn't configured yet
    imgElement.src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23cbd5e1"><circle cx="12" cy="12" r="12"/></svg>';
  }
}

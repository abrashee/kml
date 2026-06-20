// src/app/layout/main-layout.component.ts
import { Component, inject, computed } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  template: `
    <div class="app-layout">
      <aside class="sidebar">
        <div class="brand">
          <h2>Operations App</h2>
          <span class="role-badge">{{ userRole() }}</span>
        </div>

        <nav class="nav-menu">
          <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
            Dashboard
          </a>

          @if (canSeeInventory()) {
            <a routerLink="/inventory" routerLinkActive="active" class="nav-item">
              Inventory
            </a>
          }

          @if (canSeeOrders()) {
            <a routerLink="/orders" routerLinkActive="active" class="nav-item">
              Orders
            </a>
          }

          @if (canSeeShipments()) {
            <a routerLink="/shipments" routerLinkActive="active" class="nav-item">
              Shipments
            </a>
          }

          @if (canManageUsers()) {
            <a routerLink="/users" routerLinkActive="active" class="nav-item">
              Users & Roles
            </a>
          }

          @if (canManageWarehouses()) {
            <a routerLink="/warehouses" routerLinkActive="active" class="nav-item">
              Warehouses
            </a>
          }
        </nav>
      </aside>

      <main class="main-content">
        <header class="topbar">
          <div class="user-info">
            <!-- CLICKABLE IDENTITY CONTAINER -->
            <a routerLink="/profile" class="profile-trigger" title="View Profile">
              <div class="profile-names">
                <span class="full-name">{{ fullName() }}</span>
                <span class="username">&#64;{{ username() }}</span>
              </div>
              <img
                [src]="avatarUrl() || 'default-avatar.png'"
                alt="Profile photo"
                class="header-avatar"
                (error)="onAvatarError($event)"
              />
            </a>

            <button class="btn btn-outline btn-sm" (click)="logout()">Logout</button>
          </div>
        </header>

        <div class="page-container">
          <router-outlet></router-outlet>
        </div>
      </main>
    </div>
  `,
  styles: [`
    .app-layout { display: flex; height: 100vh; background: var(--bg-light); }

    .sidebar { width: 250px; background: #1e293b; color: white; display: flex; flex-direction: column; }
    .brand { padding: 20px; border-bottom: 1px solid #334155; }
    .brand h2 { margin: 0; font-size: 18px; color: #f8fafc; }
    .role-badge { display: inline-block; margin-top: 8px; font-size: 11px; padding: 3px 8px; background: #3b82f6; border-radius: 12px; font-weight: bold; text-transform: uppercase; }

    .nav-menu { padding: 20px 0; display: flex; flex-direction: column; gap: 4px; }
    .nav-item { padding: 12px 20px; color: #cbd5e1; text-decoration: none; font-size: 15px; transition: 0.2s; }
    .nav-item:hover, .nav-item.active { background: #334155; color: white; border-left: 4px solid #3b82f6; }

    .main-content { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
    .topbar { height: 60px; background: white; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: flex-end; align-items: center; padding: 0 24px; }
    .user-info { display: flex; align-items: center; gap: 16px; font-weight: 500; }

    /* NEW PROFILE HEADER STYLES */
    .profile-trigger { display: flex; align-items: center; gap: 12px; text-decoration: none; color: inherit; padding: 4px 8px; border-radius: 6px; transition: background 0.2s; }
    .profile-trigger:hover { background: #f1f5f9; }
    .profile-names { display: flex; flex-direction: column; text-align: right; }
    .full-name { font-size: 14px; font-weight: 600; color: #0f172a; line-height: 1.2; }
    .username { font-size: 11px; color: #64748b; font-weight: 400; }
    .header-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; border: 2px solid #e2e8f0; }

    .page-container { padding: 24px; overflow-y: auto; flex: 1; }
  `]
})
export class MainLayoutComponent {
  private authService = inject(AuthService);

  // Derived signals for UI
  userRole = computed(() => this.authService.currentUser()?.role || 'GUEST');
  username = computed(() => this.authService.currentUser()?.username || 'Unknown');
  fullName = computed(() => this.authService.currentUser()?.name || 'User Profile');
  // avatarUrl = computed(() => this.authService.currentUser()?.avatar || null);
  avatarUrl = computed(() => null);

  // Permission Checks (Based on Rules)
  canSeeInventory = computed(() => ['ADMIN', 'MANAGER'].includes(this.userRole()));
  canSeeOrders = computed(() => ['ADMIN', 'MANAGER', 'WORKER'].includes(this.userRole()));
  canSeeShipments = computed(() => ['ADMIN', 'MANAGER', 'WORKER'].includes(this.userRole()));
  canManageUsers = computed(() => this.userRole() === 'ADMIN');
  canManageWarehouses = computed(() => this.userRole() === 'ADMIN');

  onAvatarError(event: Event): void {
    (event.target as HTMLImageElement).src = 'default-avatar.png';
  }

  logout(): void {
    this.authService.logout();
  }
}

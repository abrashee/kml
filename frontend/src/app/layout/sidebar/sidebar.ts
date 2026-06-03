import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <aside class="sidebar">
      <nav class="sidebar-nav">
        <a class="sidebar-link" routerLink="/dashboard" routerLinkActive="active"
           [routerLinkActiveOptions]="{ exact: true }">
          📊 Dashboard
        </a>
        <a class="sidebar-link" routerLink="/inventory" routerLinkActive="active">
          📦 Inventory
        </a>
        <a class="sidebar-link" routerLink="/orders" routerLinkActive="active">
          🛒 Orders
        </a>
        <a class="sidebar-link" routerLink="/shipments" routerLinkActive="active">
          🚚 Shipments
        </a>
        <a class="sidebar-link" routerLink="/warehouses" routerLinkActive="active">
          🏭 Warehouses
        </a>
        @if (authService.currentUser()?.role === 'ADMIN') {
          <a class="sidebar-link" routerLink="/users" routerLinkActive="active">
            👥 Users
          </a>
        }
      </nav>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: 240px; background: var(--surface);
      border-right: 1px solid var(--border); height: 100%; overflow-y: auto; flex-shrink: 0;
    }
    .sidebar-nav { padding: 16px 0; }
    .sidebar-link {
      display: flex; align-items: center; gap: 10px;
      padding: 10px 24px; font-size: 14px; font-weight: 500;
      color: var(--text); text-decoration: none; transition: background 0.15s;
    }
    .sidebar-link:hover { background: var(--surface-2); }
    .sidebar-link.active {
      background: #eff6ff; color: var(--primary);
      border-right: 3px solid var(--primary);
    }
  `]
})
export class SidebarComponent {
  protected authService = inject(AuthService);
}

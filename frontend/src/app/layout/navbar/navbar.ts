import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  template: `
    <nav class="navbar">
      <a class="navbar-brand" routerLink="/dashboard">KML Fulfillment</a>
      <div class="navbar-right">
        @if (authService.currentUser(); as user) {
          <span class="navbar-user">{{ user.username }}</span>
          <span class="badge badge-info">{{ user.role }}</span>
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
    .navbar-user  { font-size: 14px; color: var(--text-muted); }
  `]
})
export class NavbarComponent {
  protected authService = inject(AuthService);
}

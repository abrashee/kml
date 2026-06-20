import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="auth-container" style="max-width: 500px; margin: 100px auto; text-align: center;">
      <h1 style="font-size: 48px; margin-bottom: 16px;">🛑</h1>
      <h2>Access Denied</h2>
      <p class="text-muted" style="margin-bottom: 24px;">
        Your current role configuration does not have clearance to view this module.
      </p>
      <a routerLink="/dashboard" class="btn btn-primary">Return to Dashboard</a>
    </div>
  `
})
export class UnauthorizedComponent {}
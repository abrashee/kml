// src/ app/ layout / shell.ts
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar';
import { SidebarComponent } from '../sidebar/sidebar';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, SidebarComponent],
  template: `
    <div class="shell">
      <app-navbar />
      <div class="shell-body">
        <app-sidebar />
        <main class="main-content">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
  styles: [`
    .shell       { display: flex; flex-direction: column; height: 100vh; }
    .shell-body  { display: flex; flex: 1; overflow: hidden; margin-top: 60px; }
    .main-content { flex: 1; overflow-y: auto; padding: 24px; background: var(--surface-2); }
  `]
})
export class ShellComponent {}

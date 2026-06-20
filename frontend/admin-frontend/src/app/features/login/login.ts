import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { UserService } from '../users/services/user.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div class="login-page">
      <div class="login-card card">

        <h1 class="login-title">KML Fulfillment</h1>
        <p class="login-subtitle">Sign in to your account</p>

        <form [formGroup]="form" (ngSubmit)="onSubmit()">

          <div class="form-group">
            <label class="form-label">Username</label>
            <input class="form-control" formControlName="username" placeholder="Enter your username" />
          </div>

          <div class="form-group">
            <label class="form-label">Password</label>
            <input type="password" class="form-control" formControlName="password" placeholder="Enter your password" />
          </div>

          @if (error()) {
            <div class="error-banner">{{ error() }}</div>
          }

          <button type="submit" class="btn btn-primary" style="width:100%">
            {{ loading() ? 'Authenticating...' : 'Login' }}
          </button>

        </form>

      </div>
    </div>
  `,
  styles: [`
    .login-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background-color: #f8fafc; }
    .login-card { width: 420px; padding: 32px; background: white; border-radius: 8px; box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1); }
    .login-title { text-align: center; margin-bottom: 8px; color: #0f172a; }
    .login-subtitle { text-align: center; margin-bottom: 24px; color: #64748b; }
    .form-group { margin-bottom: 16px; }
    .form-label { display: block; margin-bottom: 6px; font-weight: 500; color: #334155; }
    .form-control { width: 100%; padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box; }
    .btn { padding: 10px 16px; border: none; border-radius: 4px; cursor: pointer; font-weight: 500; }
    .btn-primary { background-color: #2563eb; color: white; }
    .btn-primary:hover { background-color: #1d4ed8; }
    .error-banner { background: #fee2e2; color: #b91c1c; padding: 12px; margin: 16px 0; border-radius: 4px; font-size: 14px; text-align: center; }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private users = inject(UserService);
  private router = inject(Router);

  loading = signal(false);
  error = signal<string | null>(null);

  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.error.set(null);
    const val = this.form.value;

    this.auth.login(val.username!, val.password!).subscribe({
      next: () => {
        // 1. Token already decoded in AuthService.login()
        const user = this.auth.currentUser();

        // 2. Navigate immediately based on RBAC logic
        if (user?.role === 'WORKER') {
          this.router.navigate(['/worker-dashboard']);
        } else {
          this.router.navigate(['/dashboard']);
        }

        // 3. Try to enrich profile in background (non-blocking)
        this.users.getMe().subscribe({
          next: (fullUser: any) => {
            // Safely merge the incoming data without destroying the existing role.
            this.auth.updateCurrentUserState({
              ...fullUser,
              role: fullUser.role || fullUser.userRole || this.auth.currentUser()?.role
            });
          },
          error: () => {
            // silent fail → DO NOT break login
            console.warn('Profile enrichment failed');
          }
        });

        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message ?? 'Invalid username or password');
        this.loading.set(false);
      }
    });
  }
}
import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { UserService } from '../../core/services/user.service';
import { UserRole } from '../../core/models/user.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div class="login-page">
      <div class="login-card card">

        <h1 class="login-title">KML Fulfillment</h1>
        <p class="login-subtitle">
          {{ isLoginMode() ? 'Sign in' : 'Create account' }}
        </p>

        <form [formGroup]="form" (ngSubmit)="onSubmit()">

          @if (!isLoginMode()) {
            <div class="form-group">
              <label class="form-label">Name</label>
              <input class="form-control" formControlName="name" />
            </div>

            <div class="form-group">
              <label class="form-label">Role</label>
              <select class="form-control" formControlName="userRole">
                @for (r of roles; track r) {
                  <option [value]="r">{{ r }}</option>
                }
              </select>
            </div>
          }

          <div class="form-group">
            <label class="form-label">Username</label>
            <input class="form-control" formControlName="username" />
          </div>

          <div class="form-group">
            <label class="form-label">Password</label>
            <input type="password" class="form-control" formControlName="password" />
          </div>

          @if (error()) {
            <div class="error-banner">{{ error() }}</div>
          }

          <button type="submit" class="btn btn-primary" style="width:100%">
            {{ loading() ? 'Please wait...' : (isLoginMode() ? 'Login' : 'Register') }}
          </button>

        </form>

        <div style="margin-top:12px;text-align:center">
          <button class="btn btn-link" type="button" (click)="toggleMode()">
            {{ isLoginMode() ? 'Create account' : 'Already have account?' }}
          </button>
        </div>

      </div>
    </div>
  `,
  styles: [`
    .login-page { min-height:100vh; display:flex; align-items:center; justify-content:center; }
    .login-card { width:420px; padding:32px; }
    .login-title { text-align:center; }
    .login-subtitle { text-align:center; margin-bottom:20px; }
    .error-banner { background:#fee2e2; padding:10px; margin:10px 0; }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private users = inject(UserService);
  private router = inject(Router);

  loading = signal(false);
  error = signal<string | null>(null);
  isLoginMode = signal(true);

  roles: UserRole[] = ['ADMIN', 'MANAGER', 'USER', 'CUSTOMER'];

  form = this.fb.group({
    name: [''],
    username: ['', Validators.required],
    password: ['', Validators.required],
    userRole: ['USER' as UserRole]
  });

  toggleMode(): void {
    this.isLoginMode.set(!this.isLoginMode());
    this.error.set(null);
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.error.set(null);

    const val = this.form.value;

    if (this.isLoginMode()) {
      this.auth.login(val.username!, val.password!).subscribe({
        next: () => this.router.navigate(['/dashboard']),
        error: (err) => {
          this.error.set(err.error?.message ?? 'Login failed');
          this.loading.set(false);
        }
      });
    } else {
      this.users.create({
        username: val.username!,
        password: val.password!,
        name: val.name!,
        userRole: val.userRole as UserRole
      }).subscribe({
        next: () => {
          this.isLoginMode.set(true);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Registration failed');
          this.loading.set(false);
        }
      });
    }
  }
}
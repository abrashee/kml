// // src / app / features/ user / components / user-form.ts
import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../services/user.service';
import { UserRole } from '../models/user.model';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h1>New User</h1>
    </div>

    <div class="card" style="max-width:600px">
      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div class="form-group">
          <label class="form-label">Username *</label>
          <input class="form-control" [class.invalid]="isInvalid('username')" formControlName="username" />
          @if (isInvalid('username')) { <div class="form-error">Username is required</div> }
        </div>

        <div class="form-group">
          <label class="form-label">Email *</label>
          <input type="email" class="form-control" [class.invalid]="isInvalid('email')" formControlName="email" />
          @if (isInvalid('email')) { <div class="form-error">Valid email is required</div> }
        </div>

        <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px">
          <div class="form-group">
            <label class="form-label">First Name *</label>
            <input class="form-control" [class.invalid]="isInvalid('firstName')" formControlName="firstName" />
            @if (isInvalid('firstName')) { <div class="form-error">Required</div> }
          </div>
          <div class="form-group">
            <label class="form-label">Last Name *</label>
            <input class="form-control" [class.invalid]="isInvalid('lastName')" formControlName="lastName" />
            @if (isInvalid('lastName')) { <div class="form-error">Required</div> }
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Password *</label>
          <input type="password" class="form-control" [class.invalid]="isInvalid('password')" formControlName="password" />
          @if (isInvalid('password')) { <div class="form-error">Password required (min 6 characters)</div> }
        </div>

        <div class="form-group">
          <label class="form-label">Role</label>
          <select class="form-control" formControlName="role">
            @for (r of roles; track r) { <option [value]="r">{{ r }}</option> }
          </select>
        </div>

        <div style="display:flex;gap:8px;margin-top:24px">
          <button type="submit" class="btn btn-primary" [disabled]="loading() || form.invalid">
            {{ loading() ? 'Creating…' : 'Create User' }}
          </button>
          <a routerLink="/users" class="btn btn-outline">Cancel</a>
        </div>
      </form>
    </div>
  `
})
export class UserFormComponent {
  private fb = inject(FormBuilder);
  private service = inject(UserService);
  private router = inject(Router);

  loading = signal(false);
  roles: UserRole[] = ['ADMIN', 'MANAGER', 'WORKER', 'CUSTOMER'];

  form = this.fb.group({
    name: [''],
    username: ['', Validators.required],
    password: ['', Validators.required],
    userRole: ['USER' as UserRole]
  });

  isInvalid(field: string): boolean {
    const c = this.form.get(field);
    return !!(c?.invalid && c?.touched);
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    const val = this.form.value;
    this.service.create({
      username:  val.username!,
      password:  val.password!,
      name:      val.name!,
      userRole:  val.userRole as UserRole
    }).subscribe({
      next: () => this.router.navigate(['/users']),
      error: () => this.loading.set(false)
    });
  }
}

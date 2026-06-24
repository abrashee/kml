// src/app/features/warehouse/warehouse-form.component.ts
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { WarehouseService } from '../services/warehouse.service';

@Component({
  selector: 'app-warehouse-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h1>Register New Warehouse</h1>
    </div>

    <div class="card" style="max-width: 600px;">
      <form [formGroup]="form" (ngSubmit)="onSubmit()">

        <div class="form-group">
          <label class="form-label">Warehouse Name *</label>
          <input class="form-control" formControlName="name" placeholder="e.g., EU-Central-1" />
          @if (isInvalid('name')) { <div class="form-error">Name is required</div> }
        </div>

        <!-- <div class="form-group">
          <label class="form-label">Location (Region/City) *</label>
          <input class="form-control" formControlName="location" placeholder="e.g., Munich, Germany" />
          @if (isInvalid('location')) { <div class="form-error">Location is required</div> }
        </div> -->

        <div class="form-group">
          <label class="form-label">Street Address *</label>
          <input class="form-control" formControlName="address" placeholder="e.g., 123 Logistics Way" />
          @if (isInvalid('address')) { <div class="form-error">Address is required</div> }
        </div>

        <div style="display:flex; gap:12px; margin-top:24px;">
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid || loading()">
            {{ loading() ? 'Saving...' : 'Create Warehouse' }}
          </button>
          <a routerLink="/warehouses" class="btn btn-outline">Cancel</a>
        </div>

      </form>
    </div>
  `,
  styles: [`
    .form-group { margin-bottom: 16px; }
    .form-label { display: block; margin-bottom: 6px; font-weight: 500; }
    .form-control { width: 100%; padding: 10px; border: 1px solid var(--border); border-radius: 6px; }
    .form-error { color: #dc2626; font-size: 12px; margin-top: 4px; }
  `]
})
export class WarehouseFormComponent {
  private fb = inject(FormBuilder);
  private warehouseService = inject(WarehouseService);
  private authService = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);

  form = this.fb.group({
    name: ['', Validators.required],
    // location: ['', Validators.required],
    address: ['', Validators.required]
  });

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!(control?.invalid && control?.touched);
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    const ownerUserId = this.authService.currentUser()?.id;
    if (!ownerUserId) {
      alert('Cannot create warehouse: current user id is missing from the login session.');
      this.loading.set(false);
      return;
    }

    this.loading.set(true);
    this.warehouseService.create({
      ownerUserId,
      name: this.form.value.name!,
      address: this.form.value.address!
    }).subscribe({
      next: () => this.router.navigate(['/warehouses']),
      error: () => this.loading.set(false)
    });
  }
}

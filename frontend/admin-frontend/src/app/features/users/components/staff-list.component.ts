// src/app/features/users/components/staff-list.component.ts
import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { StaffService } from '../services/staff.service';
import { WarehouseService } from '../../warehouses/services/warehouse.service';
import { AuthService } from '../../../core/auth/auth.service';
import { StaffMember } from '../models/staff.model';
import { Warehouse } from '../../warehouses/models/warehouse.model';
import { Page, UserRole } from '../models/user.model';

@Component({
  selector: 'app-staff-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="page-header" style="display: flex; justify-content: space-between; margin-bottom: 24px;">
      <div>
        <h1>Staff Management</h1>
        <p class="text-muted">Manage internal team roles and warehouse assignments</p>
      </div>
      @if (canCreateStaff()) {
        <button class="btn btn-primary" (click)="openCreateMode()">+ Add Staff Member</button>
      }
    </div>

    @if (showPanel()) {
      <div class="card" style="margin-bottom: 24px; background: #f8fafc; border-left: 4px solid #3b82f6; padding: 20px; border-radius: 8px;">
        <h3 style="margin-top: 0;">{{ editingStaff() ? 'Update Operational Access' : 'Create New Staff Member' }}</h3>

        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div style="display: flex; gap: 16px; margin-bottom: 16px;">
            <div style="flex: 1;">
              <label class="form-label">Username</label>
              <input class="form-control" formControlName="username" [readonly]="editingStaff()" />
            </div>

            <div style="flex: 1;">
              <label class="form-label">Full Name</label>
              <input class="form-control" formControlName="name" [readonly]="editingStaff()" />
            </div>

            @if (!editingStaff()) {
              <div style="flex: 1;">
                <label class="form-label">Initial Password</label>
                <input type="password" class="form-control" formControlName="password" />
              </div>
            }
          </div>

          <div style="display: flex; gap: 16px; margin-bottom: 24px;">
            <div style="flex: 1;">
              <label class="form-label">System Role</label>
              <select class="form-control" formControlName="role">
                <option value="WORKER">Worker</option>
                @if (isAdmin()) {
                  <option value="MANAGER">Manager</option>
                  <option value="ADMIN">Admin</option>
                }
              </select>
            </div>

            <div style="flex: 1;">
              <label class="form-label">Assigned Warehouse Scope</label>
              <select class="form-control" formControlName="warehouseId">
                <option [ngValue]="null">-- Global Corporate Scope --</option>
                @for (wh of warehouses(); track wh.id) {
                  <option [ngValue]="wh.id" [disabled]="isManager() && wh.id !== currentUserWarehouseId()">
                    {{ wh.name }} ({{ wh.location }})
                  </option>
                }
              </select>
            </div>
          </div>

          <div style="display: flex; gap: 8px;">
            <button type="submit" class="btn btn-primary" [disabled]="form.invalid || loading()">
              {{ loading() ? 'Saving Configuration...' : 'Save Configuration' }}
            </button>
            <button type="button" class="btn btn-outline" (click)="closePanel()">Cancel</button>
          </div>
        </form>
      </div>
    }

    <div class="card" style="background: var(--panel); border: 1px solid var(--border); border-radius: 8px; padding: 20px;">
      <table class="data-table">
        <thead>
          <tr>
            <th>Username</th>
            <th>Name</th>
            <th>Role</th>
            <th>Location Scope</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          @for (user of page()?.content; track user.id) {
            <tr>
              <td class="fw-bold">{{ user.username }}</td>
              <td>{{ user.name }}</td>

              <td><span class="role-badge">{{ user.role || $any(user).userRole }}</span></td>

              <td>{{ user.warehouseName || 'Global' }}</td>
              <td>
                <span class="badge" [class.bg-success]="user.status === 'ACTIVE'" [class.bg-danger]="user.status === 'SUSPENDED'">
                  {{ user.status }}
                </span>
              </td>
              <td>
                @if (canManageUser(user)) {
                  <button class="btn btn-sm btn-outline" (click)="openEditMode(user)">Manage Access</button>
                } @else {
                  <span style="font-size: 12px; opacity: 0.4;">Read-Only</span>
                }
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .data-table { width: 100%; border-collapse: collapse; text-align: left; }
    .data-table th, .data-table td { padding: 12px; border-bottom: 1px solid var(--border); }
    .fw-bold { font-weight: 600; }
    .role-badge { background: #e0f2fe; color: #0284c7; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; }
    .badge { padding: 4px 8px; border-radius: 12px; font-size: 11px; font-weight: bold; }
    .bg-success { background: #dcfce7; color: #166534; }
    .bg-danger { background: #fee2e2; color: #991b1b; }
    .form-control { width: 100%; padding: 10px; border: 1px solid var(--border); border-radius: 6px; box-sizing: border-box; }
    .form-control[readonly] { background: #e2e8f0; cursor: not-allowed; color: #64748b; }
    .form-label { display: block; margin-bottom: 6px; font-weight: 500; font-size: 14px; }
  `]
})
export class StaffListComponent implements OnInit {
  private fb = inject(FormBuilder);
  private staffService = inject(StaffService);
  private warehouseService = inject(WarehouseService);
  private authService = inject(AuthService);

  currentUser = computed(() => this.authService.currentUser());
  isAdmin = computed(() => this.currentUser()?.role === 'ADMIN');
  isManager = computed(() => this.currentUser()?.role === 'MANAGER');
  canCreateStaff = computed(() => this.isAdmin() || this.isManager());
  currentUserWarehouseId = computed(() => (this.currentUser() as any)?.warehouseId || null);

  page = signal<Page<StaffMember> | null>(null);
  warehouses = signal<Warehouse[]>([]);
  showPanel = signal(false);
  editingStaff = signal<StaffMember | null>(null);
  loading = signal(false);

  form = this.fb.group({
    username: ['', Validators.required],
    name: ['', Validators.required],
    password: [''],
    role: ['WORKER' as UserRole, Validators.required],
    warehouseId: [null as number | null],
    status: ['ACTIVE' as 'ACTIVE' | 'SUSPENDED']
  });

  ngOnInit(): void {
    this.loadStaff();
    this.loadWarehouses();
  }

  loadStaff(): void {
    this.staffService.getStaff(0, 50).subscribe(p => this.page.set(p));
  }

  loadWarehouses(): void {
    this.warehouseService.getAll(0, 100).subscribe(p => this.warehouses.set(p.content));
  }

  openCreateMode(): void {
    this.editingStaff.set(null);

    this.form.get('username')?.enable();
    this.form.get('name')?.enable();

    this.form.reset({
      role: 'WORKER',
      status: 'ACTIVE',
      warehouseId: this.isManager() ? this.currentUserWarehouseId() : null
    });

    this.form.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
    this.form.get('password')?.updateValueAndValidity();
    this.showPanel.set(true);
  }

  openEditMode(staff: StaffMember): void {
    this.editingStaff.set(staff);

    this.form.get('username')?.disable();
    this.form.get('name')?.disable();

    this.form.get('password')?.clearValidators();
    this.form.get('password')?.updateValueAndValidity();

    this.form.patchValue({
      username: staff.username,
      name: staff.name,
      // FIX: Ensure the form patches correctly whether it's named 'role' or 'userRole'
      role: staff.role || (staff as any).userRole,
      warehouseId: staff.warehouseId || null,
      status: staff.status
    });
    this.showPanel.set(true);
  }

  canManageUser(targetUser: StaffMember): boolean {
    if (this.isAdmin()) return true;
    if (this.isManager()) {
      // FIX: Use fallback for userRole check here as well
      const targetRole = targetUser.role || (targetUser as any).userRole;
      return targetRole === 'WORKER' && targetUser.warehouseId === this.currentUserWarehouseId();
    }
    return false;
  }

  closePanel(): void { this.showPanel.set(false); }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);

    const val = this.form.getRawValue();
    const isEdit = !!this.editingStaff();

    if (isEdit) {
      this.staffService.updateOperationalAccess(this.editingStaff()!.id, {
        // Fallback for API structure: Send back userRole if that's what the backend expects
        userRole: val.role as UserRole,
        role: val.role as UserRole,
        warehouseId: val.warehouseId ?? undefined,
        status: val.status as 'ACTIVE' | 'SUSPENDED'
      } as any).subscribe({
        next: () => { this.loadStaff(); this.closePanel(); this.loading.set(false); },
        error: () => this.loading.set(false)
      });
    } else {
      this.staffService.createStaff({
        username: val.username!,
        name: val.name!,
        password: val.password!,
        userRole: val.role as UserRole,
        role: val.role as UserRole,
        warehouseId: val.warehouseId ?? undefined
      } as any).subscribe({
        next: () => { this.loadStaff(); this.closePanel(); this.loading.set(false); },
        error: () => this.loading.set(false)
      });
    }
  }
}

// src/app/features/profile/components/profile.component.ts
import { Component, inject, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="page-header">
      <h1>My Profile</h1>
      <p class="text-muted">Manage your personal identity, delivery details, and credentials.</p>
    </div>

    <div class="card profile-container">
      <div class="avatar-section">
        <div class="avatar-wrapper" style="position: relative;">
          <img
            [src]="avatarUrl()"
            alt="Profile Avatar"
            class="profile-avatar-large"
            (error)="onAvatarError()"
          />
          <label class="avatar-edit-overlay">
            📸
            <input type="file" style="display: none;" (change)="onFileSelected($event)" accept="image/*" />
          </label>
        </div>

        @if (uploadingFile()) {
          <small class="text-muted mt-2" style="display: block;">Uploading file...</small>
        } @else {
          <small class="text-muted mt-2" style="display: block; font-size: 11px;">Click image to upload</small>
        }

        <div class="role-badge mt-2">{{ currentUser()?.role || '...' }}</div>
      </div>

      <div class="form-section">
        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label class="form-label">Full Name</label>
            <input class="form-control" formControlName="name" />
          </div>

          <div class="form-group">
            <label class="form-label">Username</label>
            <input class="form-control" formControlName="username" readonly style="background: #f8fafc; color: #64748b;" />
          </div>

          <div class="form-group">
            <label class="form-label">Physical Delivery Address</label>
            <textarea class="form-control" formControlName="address" rows="3"></textarea>
          </div>

          @if (isLogisticsRole()) {
            <div class="logistics-panel" style="background: #f8fafc; border: 1px solid #e2e8f0; padding: 16px; border-radius: 8px; margin-top: 24px;">
              <h3 style="font-size: 15px; margin: 0 0 16px 0;">Operational Assignment</h3>
              <div class="form-group" style="margin-bottom: 12px;">
                <label class="form-label text-muted">Assigned Warehouse ID</label>
                <input class="form-control disabled-input" [value]="currentUser()?.warehouseId || 'Pending'" disabled />
              </div>
            </div>
          }

          <div class="form-group">
            <label class="form-label">Update Password (Optional)</label>
            <input type="password" class="form-control" formControlName="password" placeholder="Leave blank to keep current" />
          </div>

          <div class="actions">
            <button type="submit" class="btn btn-primary" [disabled]="form.invalid || loading()">
              {{ loading() ? 'Saving...' : 'Save Profile' }}
            </button>
            @if (successMessage()) { <span class="success-text">Profile updated successfully!</span> }
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .profile-container { display: flex; gap: 40px; align-items: flex-start; max-width: 800px; }
    .avatar-section { display: flex; flex-direction: column; align-items: center; min-width: 150px; }
    .profile-avatar-large { width: 120px; height: 120px; border-radius: 50%; object-fit: cover; border: 4px solid #f1f5f9; }
    .avatar-wrapper { width: 120px; height: 120px; border-radius: 50%; overflow: hidden; position: relative; }
    .avatar-edit-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; opacity: 0; cursor: pointer; color: white; }
    .avatar-wrapper:hover .avatar-edit-overlay { opacity: 1; }
    .disabled-input { background: #e2e8f0; cursor: not-allowed; }
  `]
})
export class ProfileComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private http = inject(HttpClient);

  // REACTIVE DATA
  currentUser = computed(() => this.authService.currentUser());
  isLogisticsRole = computed(() => ['MANAGER', 'WORKER'].includes(this.currentUser()?.role || ''));

  // Computed avatar URL reacts automatically when Auth state updates
  avatarUrl = computed(() => {
    const url = this.currentUser()?.avatarUrl;
    if (!url) return 'default-avatar.png';
    return url.startsWith('http') ? url : `${environment.apiUrl}${url}`;
  });

  loading = signal(false);
  uploadingFile = signal(false);
  successMessage = signal(false);

  form = this.fb.group({
    name: ['', Validators.required],
    username: [{ value: '', disabled: true }],
    address: [''],
    password: ['']
  });

  constructor() {
    // Sync form when currentUser updates
    effect(() => {
      const user = this.currentUser();
      if (user) {
        this.form.patchValue({
          name: user.name,
          username: user.username,
          address: (user as any).address || ''
        }, { emitEvent: false });
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (!file) return;

    this.uploadingFile.set(true);
    const formData = new FormData();
    formData.append('file', file);

    this.http.post<any>(`${environment.apiUrl}/api/v1/users/me/avatar`, formData).subscribe({
      next: (res) => {
        this.authService.updateCurrentUserState({ avatarUrl: res.avatarUrl });
        this.uploadingFile.set(false);
      },
      error: () => this.uploadingFile.set(false)
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.successMessage.set(false);

    // FIX: form.getRawValue() includes disabled values like username!
    // We also append the current reactive avatarUrl state.
    const payload = {
      ...this.form.getRawValue(),
      avatarUrl: this.currentUser()?.avatarUrl || ''
    };

    // This matches your backend URL setup perfectly now
    this.http.put<any>(`${environment.apiUrl}/api/v1/users/me/profile`, payload).subscribe({
      next: (updatedUser) => {
        // Broadcast changes to update navbar and local component signals immediately
        this.authService.updateCurrentUserState(updatedUser);
        this.loading.set(false);
        this.successMessage.set(true);
        this.form.get('password')?.reset();

        setTimeout(() => this.successMessage.set(false), 3000);
      },
      error: (err) => {
        console.error('Failed to save profile changes:', err);
        this.loading.set(false);
      }
    });
  }
  onAvatarError() { /* Handle default */ }
}

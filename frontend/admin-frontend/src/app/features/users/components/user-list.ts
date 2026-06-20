//// // src / app / features/ user / componetns / user-list.ts
import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserService } from '../services/user.service';
import { User, UserRole, Page } from '../models/user.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state';
import { DateFormatPipe } from '../../../shared/pipes/date-format.pipe';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [RouterLink, FormsModule, LoadingSpinnerComponent, EmptyStateComponent, DateFormatPipe],
  template: `
    <div class="page-header">
      <h1>Users</h1>
      <a routerLink="/users/new" class="btn btn-primary">+ New User</a>
    </div>

    <div class="card">
      @if (loading()) {
        <app-loading-spinner />
      } @else if (!page()?.content?.length) {
        <app-empty-state message="No users found" [showRetry]="true" (retry)="load()" />
      } @else {
        <table>
          <thead>
            <tr>
              <th>Username</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Created</th>
            </tr>
          </thead>
          <tbody>
            @for (user of page()!.content; track user.id) {
              <tr>
                <td>{{ user.username }}</td>
                <td>{{ user.name }}</td>role
                <td>{{ user.username }}</td>
                <td>
                  <div style="display:flex;gap:8px;align-items:center">
                    <select class="form-control" style="width:auto" [(ngModel)]="roleEdits[user.id]">
                      @for (r of roles; track r) { <option [value]="r">{{ r }}</option> }
                    </select>
                    <button class="btn btn-outline btn-sm" (click)="saveRole(user)">Save</button>
                  </div>
                </td>
                <td>{{ user.createdAt | dateFormat }}</td>
              </tr>
            }
          </tbody>
        </table>

        <div class="pagination">
          <button class="btn btn-outline btn-sm" [disabled]="currentPage() === 0" (click)="goPage(currentPage() - 1)">Prev</button>
          <span>Page {{ currentPage() + 1 }} of {{ page()?.totalPages ?? 1 }}</span>
          <button class="btn btn-outline btn-sm" [disabled]="currentPage() >= (page()?.totalPages ?? 1) - 1" (click)="goPage(currentPage() + 1)">Next</button>
        </div>
      }
    </div>
  `
})
export class UserListComponent implements OnInit {
  private userService = inject(UserService);

  loading = signal(true);
  page = signal<Page<User> | null>(null);
  currentPage = signal(0);
  roles: UserRole[] = ['ADMIN', 'MANAGER', 'WORKER', 'CUSTOMER'];
  roleEdits: Record<number, UserRole> = {};

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.userService.getAll(this.currentPage(), 20).subscribe({
      next: p => {
        this.page.set(p);
        p.content.forEach(u => { this.roleEdits[u.id] = u.role; });
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  goPage(p: number): void { this.currentPage.set(p); this.load(); }

  saveRole(user: User): void {
    this.userService.updateRole(user.id, this.roleEdits[user.id]).subscribe();
  }
}

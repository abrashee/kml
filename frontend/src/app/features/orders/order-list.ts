import { Component, inject, signal, OnInit, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../core/services/order.service';
import { AuthService } from '../../core/auth/auth.service';
import { Order } from '../../core/models/order.model';
import { Page } from '../../core/models/user.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog';
import { DateFormatPipe } from '../../shared/pipes/date-format.pipe';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [RouterLink, FormsModule, LoadingSpinnerComponent, EmptyStateComponent, ConfirmDialogComponent, DateFormatPipe],
  template: `
    <div class="page-header">
      <h1>Orders</h1>
      @if (canEdit()) {
        <a routerLink="/orders/new" class="btn btn-primary">+ New Order</a>
      }
    </div>

    <div class="card">
      <div class="search-bar">
        <select class="form-control" [(ngModel)]="statusFilter" (ngModelChange)="load()" style="max-width:200px">
          <option value="">All Statuses</option>
          @for (s of statusOptions; track s) { <option [value]="s">{{ s }}</option> }
        </select>
      </div>

      @if (loading()) {
        <app-loading-spinner />
      } @else if (!page()?.content?.length) {
        <app-empty-state message="No orders found" [showRetry]="true" (retry)="load()" />
      } @else {
        <table>
          <thead>
            <tr>
              <th>Code</th>
              <th>Status</th>
              <th>Items</th>
              <th>Created</th>
              @if (canEdit()) { <th>Actions</th> }
            </tr>
          </thead>
          <tbody>
            @for (order of page()!.content; track order.id) {
              <tr>
                <td>{{ order.code }}</td>
                <td><span class="badge badge-info">{{ order.status }}</span></td>
                <td>{{ order.items.length }}</td>
                <td>{{ order.createdAt | dateFormat }}</td>
                @if (canEdit()) {
                  <td>
                    <div style="display:flex;gap:8px">
                      <a [routerLink]="['/orders', order.id, 'edit']" class="btn btn-outline btn-sm">Edit</a>
                      <button class="btn btn-danger btn-sm" (click)="onDelete(order)">Delete</button>
                    </div>
                  </td>
                }
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

    <app-confirm-dialog
      #confirmDialog
      title="Delete Order"
      message="Are you sure you want to delete this order?"
      (confirmed)="deleteConfirmed()"
    />
  `
})
export class OrderListComponent implements OnInit {
  @ViewChild('confirmDialog') confirmDialog!: ConfirmDialogComponent;

  private orderService = inject(OrderService);
  private authService = inject(AuthService);

  loading = signal(true);
  page = signal<Page<Order> | null>(null);
  currentPage = signal(0);
  statusFilter = '';
  statusOptions = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];
  private selectedId: number | null = null;

  canEdit(): boolean {
    return this.authService.currentUser()?.role !== 'CUSTOMER';
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.orderService.getAll(this.currentPage(), 20, this.statusFilter).subscribe({
      next: p => { this.page.set(p); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  goPage(p: number): void { this.currentPage.set(p); this.load(); }

  onDelete(order: Order): void {
    this.selectedId = order.id;
    this.confirmDialog.show();
  }

  deleteConfirmed(): void {
    if (this.selectedId === null) return;
    this.orderService.delete(this.selectedId).subscribe({ next: () => this.load() });
    this.selectedId = null;
  }
}

import { Component, inject, signal, OnInit, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { InventoryService } from '../../core/services/inventory.service';
import { AuthService } from '../../core/auth/auth.service';
import { InventoryItem } from '../../core/models/inventory.model';
import { Page } from '../../core/models/user.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog';
import { DateFormatPipe } from '../../shared/pipes/date-format.pipe';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [RouterLink, FormsModule, LoadingSpinnerComponent, EmptyStateComponent, ConfirmDialogComponent, DateFormatPipe],
  template: `
    <div class="page-header">
      <h1>Inventory</h1>
      @if (canEdit()) {
        <a routerLink="/inventory/new" class="btn btn-primary">+ New Item</a>
      }
    </div>

    <div class="card">
      <div class="search-bar">
        <input class="form-control" type="text" placeholder="Search by name or SKU…"
               [(ngModel)]="search" (keyup.enter)="load()" />
        <button class="btn btn-outline" (click)="load()">Search</button>
      </div>

      @if (loading()) {
        <app-loading-spinner />
      } @else if (!page()?.content?.length) {
        <app-empty-state message="No inventory items found" [showRetry]="true" (retry)="load()" />
      } @else {
        <table>
          <thead>
            <tr>
              <th>SKU</th>
              <th>Name</th>
              <th>Quantity</th>
              <th>Created</th>
              @if (canEdit()) { <th>Actions</th> }
            </tr>
          </thead>
          <tbody>
            @for (item of page()!.content; track item.id) {
              <tr>
                <td>{{ item.sku }}</td>
                <td>{{ item.name }}</td>
                <td>{{ item.quantity }}</td>
                <td>{{ item.createdAt | dateFormat }}</td>
                @if (canEdit()) {
                  <td>
                    <div style="display:flex;gap:8px">
                      <a [routerLink]="['/inventory', item.id, 'edit']" class="btn btn-outline btn-sm">Edit</a>
                      <button class="btn btn-danger btn-sm" (click)="onDelete(item)">Delete</button>
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
      title="Delete Item"
      message="Are you sure you want to delete this inventory item?"
      (confirmed)="deleteConfirmed()"
    />
  `
})
export class InventoryListComponent implements OnInit {
  @ViewChild('confirmDialog') confirmDialog!: ConfirmDialogComponent;

  private inventoryService = inject(InventoryService);
  private authService = inject(AuthService);

  loading = signal(true);
  page = signal<Page<InventoryItem> | null>(null);
  currentPage = signal(0);
  search = '';
  private selectedId: number | null = null;

  canEdit(): boolean {
    const role = this.authService.currentUser()?.role;
    return role === 'ADMIN' || role === 'MANAGER';
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.inventoryService.getAll(this.currentPage(), 20, this.search).subscribe({
      next: p => { this.page.set(p); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  goPage(p: number): void { this.currentPage.set(p); this.load(); }

  onDelete(item: InventoryItem): void {
    this.selectedId = item.id;
    this.confirmDialog.show();
  }

  deleteConfirmed(): void {
    if (this.selectedId === null) return;
    this.inventoryService.delete(this.selectedId).subscribe({ next: () => this.load() });
    this.selectedId = null;
  }
}

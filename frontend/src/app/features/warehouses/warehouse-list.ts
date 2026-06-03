import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { WarehouseService } from '../../core/services/warehouse.service';
import { Warehouse } from '../../core/models/warehouse.model';
import { Page } from '../../core/models/user.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state';
import { DateFormatPipe } from '../../shared/pipes/date-format.pipe';

@Component({
  selector: 'app-warehouse-list',
  standalone: true,
  imports: [RouterLink, LoadingSpinnerComponent, EmptyStateComponent, DateFormatPipe],
  template: `
    <div class="page-header">
      <h1>Warehouses</h1>
    </div>

    <div class="card">
      @if (loading()) {
        <app-loading-spinner />
      } @else if (!page()?.content?.length) {
        <app-empty-state message="No warehouses found" [showRetry]="true" (retry)="load()" />
      } @else {
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Address</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (w of page()!.content; track w.id) {
              <tr>
                <td>{{ w.name }}</td>
                <td>{{ w.address }}</td>
                <td>{{ w.createdAt | dateFormat }}</td>
                <td>
                  <a [routerLink]="['/warehouses', w.id]" class="btn btn-outline btn-sm">View</a>
                </td>
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
export class WarehouseListComponent implements OnInit {
  private warehouseService = inject(WarehouseService);

  loading = signal(true);
  page = signal<Page<Warehouse> | null>(null);
  currentPage = signal(0);

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.warehouseService.getAll(this.currentPage(), 20).subscribe({
      next: p => { this.page.set(p); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  goPage(p: number): void { this.currentPage.set(p); this.load(); }
}

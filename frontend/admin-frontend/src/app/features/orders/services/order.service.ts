import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface Order {
  id: number;
  code: string; // Aligned perfectly with backend OrderResponse DTO naming
  statusId: number;
  statusName: string; // e.g., PENDING, PROCESSING, SHIPPED
  warehouseId?: number;
  assignedWorkerId?: number;
  createdAt: string;
  updatedAt: string;
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/orders`;

  getOrders(
    page = 0,
    size = 20,
    warehouseId?: number | null,
    workerId?: number | null,
    searchQuery?: string,
    status?: string | null
  ): Observable<Page<Order>> {
    let params = new HttpParams().set('page', String(page)).set('size', String(size));

    if (warehouseId) params = params.set('warehouseId', String(warehouseId));
    if (workerId) params = params.set('workerId', String(workerId)); // Maps exactly to @RequestParam Long workerId
    if (searchQuery) params = params.set('search', searchQuery);
    if (status) params = params.set('status', status);

    return this.http.get<Order[] | Page<Order>>(this.apiUrl, { params }).pipe(
      map(response => this.toPage(response, page, size))
    );
  }

  assignWorker(orderId: number, workerId: number): Observable<Order> {
    return this.http.patch<Order>(`${this.apiUrl}/${orderId}/worker/${workerId}`, {});
  }

  private toPage(response: Order[] | Page<Order>, page: number, size: number): Page<Order> {
    if (Array.isArray(response)) {
      return {
        content: response,
        totalElements: response.length,
        totalPages: response.length ? 1 : 0,
        size,
        number: page
      };
    }

    return response;
  }
}

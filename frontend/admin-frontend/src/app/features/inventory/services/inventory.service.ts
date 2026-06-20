// src/app/features/inventory/services/inventory.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import {
  InventoryItem,
  CreateInventoryRequest,
  InventoryQuantityUpdateRequest,
  StorageUnit
} from '../models/inventory.model';
import { Page } from '../../users/models/user.model';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/inventories`;
  private warehouseApiUrl = `${environment.apiUrl}/api/v1/warehouses`;

  getInventory(page = 0, size = 50, searchQuery?: string, warehouseId?: number | null): Observable<Page<InventoryItem>> {
  let params = new HttpParams().set('page', String(page)).set('size', String(size));
  if (searchQuery) params = params.set('sku', searchQuery);
  if (warehouseId) params = params.set('warehouseId', String(warehouseId)); // ⚡ Pass warehouseId constraint
  return this.http.get<InventoryItem[] | Page<InventoryItem>>(this.apiUrl, { params }).pipe(
    map(response => this.toPage(response, page, size))
  );
}

  getStorageUnitsByWarehouse(warehouseId: number): Observable<StorageUnit[]> {
    return this.http.get<StorageUnit[]>(`${this.warehouseApiUrl}/${warehouseId}/storage-units`);
  }

  createItem(req: CreateInventoryRequest): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(this.apiUrl, {
      ownerUserId: req.ownerUserId,
      sku: req.sku,
      name: req.name || req.sku,
      quantity: req.quantity,
      warehouseId: req.warehouseId,
      storageUnitId: req.storageUnitId,
      reorderThreshold: req.reorderThreshold ?? 10,
      safetyStockLevel: req.safetyStockLevel ?? 20
    });
  }

  updateQuantity(id: number, delta: number): Observable<InventoryItem> {
    return this.http.patch<InventoryItem>(
      `${this.apiUrl}/${id}/quantity`,
      { delta } as InventoryQuantityUpdateRequest
    );
  }

  deleteItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  private toPage(response: InventoryItem[] | Page<InventoryItem>, page: number, size: number): Page<InventoryItem> {
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

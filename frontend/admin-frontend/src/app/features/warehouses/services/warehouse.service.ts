// src/app/features/warehouse/services/warehouse.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { Warehouse, StorageUnit, CreateWarehouseRequest, CreateStorageUnitRequest } from '../models/warehouse.model';
import { Page } from '../../users/models/user.model';

@Injectable({ providedIn: 'root' })
export class WarehouseService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/warehouses`;

  // --- WAREHOUSES ---

  getAll(page = 0, size = 20): Observable<Page<Warehouse>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<Warehouse[] | Page<Warehouse>>(this.apiUrl, { params }).pipe(
      map(response => this.toPage(response, page, size))
    );
  }

  getById(id: number): Observable<Warehouse> {
    return this.http.get<Warehouse>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateWarehouseRequest): Observable<Warehouse> {
    return this.http.post<Warehouse>(this.apiUrl, req);
  }

  // --- STORAGE UNITS ---

getStorageUnits(warehouseId: number): Observable<StorageUnit[]> {
    return this.http.get<StorageUnit[]>(`${this.apiUrl}/${warehouseId}/storage-units`);
  }

  addStorageUnit(req: CreateStorageUnitRequest): Observable<StorageUnit> {
    return this.http.post<StorageUnit>(`${this.apiUrl}/${req.warehouseId}/storage-units`, {
      code: req.code,
      capacity: req.capacity
    });
  }

  private toPage(response: Warehouse[] | Page<Warehouse>, page: number, size: number): Page<Warehouse> {
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

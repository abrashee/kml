import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Warehouse, WarehouseLayout } from '../models/warehouse.model';
import { Page } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class WarehouseService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/warehouses`;

  getAll(page = 0, size = 20): Observable<Page<Warehouse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Warehouse>>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Warehouse> {
    return this.http.get<Warehouse>(`${this.apiUrl}/${id}`);
  }

  create(req: { name: string; address: string }): Observable<Warehouse> {
    return this.http.post<Warehouse>(this.apiUrl, req);
  }

  getLayout(id: number): Observable<WarehouseLayout> {
    return this.http.get<WarehouseLayout>(`${this.apiUrl}/${id}/layout`);
  }
}

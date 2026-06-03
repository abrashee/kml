import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { InventoryItem, CreateInventoryRequest } from '../models/inventory.model';
import { Page } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/inventory`;

  getAll(page = 0, size = 20, search = ''): Observable<Page<InventoryItem>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<Page<InventoryItem>>(this.apiUrl, { params });
  }

  getById(id: number): Observable<InventoryItem> {
    return this.http.get<InventoryItem>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateInventoryRequest): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(this.apiUrl, req);
  }

  update(id: number, req: CreateInventoryRequest): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateQuantity(id: number, quantity: number): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${this.apiUrl}/${id}/quantity`, { quantity });
  }
}

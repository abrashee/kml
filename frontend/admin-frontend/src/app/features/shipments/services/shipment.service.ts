// src/app/features/shipments/services/shipment.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { Shipment, ShipmentStatus } from '../models/shipment.model';
import { Page } from '../../users/models/user.model';

@Injectable({ providedIn: 'root' })
export class ShipmentService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/shipments`;

  getShipments(page = 0, size = 50, status?: ShipmentStatus, search?: string): Observable<Page<Shipment>> {
    let params = new HttpParams().set('page', String(page)).set('size', String(size));
    if (status) params = params.set('status', status);
    if (search && search.trim() !== '') params = params.set('search', search.trim());

    return this.http.get<Shipment[] | Page<Shipment>>(this.apiUrl, { params }).pipe(
      map(response => this.toPage(response, page, size))
    );
  }

  getShipmentById(id: number): Observable<Shipment> {
    return this.http.get<Shipment>(`${this.apiUrl}/${id}`);
  }

  updateStatus(id: number, status: ShipmentStatus): Observable<Shipment> {
    return this.http.patch<Shipment>(`${this.apiUrl}/${id}/status/${status}`, {});
  }

  private toPage(response: Shipment[] | Page<Shipment>, page: number, size: number): Page<Shipment> {
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

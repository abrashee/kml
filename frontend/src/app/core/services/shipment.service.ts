import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Shipment, ShipmentHistory, CreateShipmentRequest } from '../models/shipment.model';
import { Page } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class ShipmentService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/shipments`;

  getAll(page = 0, size = 20, status = ''): Observable<Page<Shipment>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<Page<Shipment>>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Shipment> {
    return this.http.get<Shipment>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateShipmentRequest): Observable<Shipment> {
    return this.http.post<Shipment>(this.apiUrl, req);
  }

  updateStatus(id: number, status: string): Observable<Shipment> {
    return this.http.patch<Shipment>(`${this.apiUrl}/${id}/status`, { status });
  }

  getHistory(id: number): Observable<ShipmentHistory[]> {
    return this.http.get<ShipmentHistory[]>(`${this.apiUrl}/${id}/history`);
  }
}

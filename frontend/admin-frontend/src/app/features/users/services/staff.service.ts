// src/app/features/users/services/staff.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { StaffMember, CreateStaffRequest, UpdateStaffRoleRequest } from '../models/staff.model';
import { Page } from '../../users/models/user.model';

@Injectable({ providedIn: 'root' })
export class StaffService {
  private http = inject(HttpClient);
//  private apiUrl = `${environment.apiUrl}/api/v1/internal/staff`;
  private apiUrl = `${environment.apiUrl}/api/v1/users`;

  // Backend will automatically scope this based on the requester's token (Admin sees all, Manager sees own warehouse)
  getStaff(page = 0, size = 20): Observable<Page<StaffMember>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<Page<StaffMember>>(this.apiUrl, { params });
  }

  createStaff(req: CreateStaffRequest): Observable<StaffMember> {
    return this.http.post<StaffMember>(this.apiUrl, req);
  }

  // Updates operational data ONLY (role, warehouse, status)
  updateOperationalAccess(id: number, req: UpdateStaffRoleRequest): Observable<StaffMember> {
    return this.http.patch<StaffMember>(`${this.apiUrl}/${id}/access`, req);
  }

  getWorkersByWarehouse(warehouseId: number): Observable<StaffMember[]> {
  // Assuming your backend supports filtering staff by warehouse and role
  return this.http.get<StaffMember[]>(`${this.apiUrl}/search`, {
    params: { role: 'WORKER', warehouseId: warehouseId.toString() }
  });
}
}
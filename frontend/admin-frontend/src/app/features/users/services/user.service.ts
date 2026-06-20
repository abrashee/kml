// src / app / features/ user / services / user.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { User, UserRole, Page } from '../models/user.model';

export interface CreateUserRequest {
  username: string;
  password: string;
  name: string;
  userRole: UserRole;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/users`;
 // private apiUrl = `${environment.apiUrl}/api/v1/orders`;


  getAll(page = 0, size = 20): Observable<Page<User>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<User>>(this.apiUrl, { params });
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, {
      username: req.username,
      password: req.password,
      name: req.name,
      userRole: req.userRole
    });
  }

  updateRole(id: number, role: UserRole): Observable<User> {
  return this.http.patch<User>(`${this.apiUrl}/${id}/role`, { userRole: role });
  }

  getActivity(id: number): Observable<unknown[]> {
    return this.http.get<unknown[]>(`${this.apiUrl}/${id}/activity`);
  }

  // src/app/features/users/services/user.service.ts
getMe(): Observable<User> {
  return this.http.get<User>(`${environment.apiUrl}/api/v1/users/me`);
}

}

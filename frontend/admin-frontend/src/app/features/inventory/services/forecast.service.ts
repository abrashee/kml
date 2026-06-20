import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ForecastResult } from '../models/inventory.model';

@Injectable({ providedIn: 'root' })
export class ForecastService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/ims/forecast`;

  getWeeklyDemandForecast(productId: number): Observable<ForecastResult> {
    return this.http.get<ForecastResult>(`${this.apiUrl}/${productId}`);
  }
}

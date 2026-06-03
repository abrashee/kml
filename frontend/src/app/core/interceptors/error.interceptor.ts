import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AlertService } from '../services/alert.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const alertService = inject(AlertService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        return throwError(() => error);
      }

      let message: string;
      switch (error.status) {
        case 403:
          message = 'Access denied';
          break;
        case 404:
          message = 'Not found';
          break;
        case 500:
          message = 'Server error';
          break;
        default:
          message = (error.error as { message?: string })?.message ?? error.message ?? 'An error occurred';
      }

      alertService.show(message, 'error');
      return throwError(() => error);
    })
  );
};

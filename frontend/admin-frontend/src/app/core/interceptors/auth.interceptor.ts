import { HttpInterceptorFn, HttpErrorResponse, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { Injector, inject } from '@angular/core';
import { catchError, switchMap, throwError, BehaviorSubject, filter, take } from 'rxjs';
import { AuthService } from '../auth/auth.service';

// Module-level variables to hold our refresh state
let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const injector = inject(Injector);

  // 1. Bypass interception for auth endpoints to prevent infinite loops
  if (req.url.includes('/api/v1/auth/')) {
    return next(req);
  }

  // 2. Attach current token directly from storage to smash the constructor dependency loop
  const token = sessionStorage.getItem('access_token');
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` }, withCredentials: true })
    : req;

  // 3. Handle the request and catch errors
  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Only handle 401s here. error.interceptor.ts handles the rest.
      if (error.status === 401) {
        // Lazily look up AuthService inside the error pathway rather than injecting top-level
        const authService = injector.get(AuthService);
        return handle401Error(req, next, authService);
      }
      return throwError(() => error);
    })
  );
};

/**
 * Handles the 401 workflow, ensuring only ONE refresh request is sent
 * even if multiple HTTP calls fail simultaneously.
 */
const handle401Error = (req: HttpRequest<unknown>, next: HttpHandlerFn, authService: AuthService) => {
  if (!isRefreshing) {
    // Lock the refresh process
    isRefreshing = true;
    refreshTokenSubject.next(null); // Reset the subject

    return authService.refreshToken().pipe(
      switchMap((resp) => {
        isRefreshing = false;
        // Broadcast the new token to any requests waiting in the queue
        refreshTokenSubject.next(resp.accessToken);

        // Retry the original request that failed
        const retryReq = req.clone({
          setHeaders: { Authorization: `Bearer ${resp.accessToken}` },
          withCredentials: true
        });
        return next(retryReq);
      }),
      catchError((refreshError) => {
        // If the refresh token itself is expired/invalid, nuke the session
        isRefreshing = false;
        authService.logout();
        return throwError(() => refreshError);
      })
    );
  } else {
    // If a refresh is already in progress, queue this request
    return refreshTokenSubject.pipe(
      // Wait until the subject emits a valid token
      filter(token => token !== null),
      take(1), // Only take the first emission, then complete
      switchMap(token => {
        // Retry the request with the newly minted token
        const retryReq = req.clone({
          setHeaders: { Authorization: `Bearer ${token}` },
          withCredentials: true
        });
        return next(retryReq);
      })
    );
  }
};

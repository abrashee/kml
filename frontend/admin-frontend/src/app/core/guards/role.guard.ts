//src/ app / core / guards/ role.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.currentUser();

  // 1. Kick out unauthenticated users
  if (!user) {
    return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  }

  const allowedRoles = route.data?.['roles'] as Array<string>;

  // 2. Validate role against route requirements
  if (allowedRoles && !allowedRoles.includes(user.role)) {
    console.warn(`Access Denied: Role ${user.role} attempted to access ${state.url}`);

    // Route to a dedicated unauthorized page or fallback to their native dashboard
    return router.createUrlTree(['/unauthorized']);
  }

  // 3. Authorized
  return true;
};

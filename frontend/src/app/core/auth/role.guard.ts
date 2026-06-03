import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const roles: string[] = route.data['roles'] ?? [];
  const user = authService.currentUser();

  if (user && roles.includes(user.role)) {
    return true;
  }
  return router.createUrlTree(['/dashboard']);
};

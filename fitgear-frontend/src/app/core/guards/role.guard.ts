import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const auth     = inject(AuthService);
  const router   = inject(Router);
  const expected: string[] = route.data['roles'] ?? [];

  if (!auth.isLoggedIn()) {
    router.navigate(['/auth/login']);
    return false;
  }

  const userType = auth.user()?.userType ?? '';
  if (expected.length === 0 || expected.includes(userType)) return true;

  // Redirect to role-appropriate home
  const dest = auth.isCoach() ? '/coach/dashboard' : '/client/dashboard';
  router.navigate([dest]);
  return false;
};

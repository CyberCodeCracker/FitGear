import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Redirects CLIENT users who have no assigned coach to the coaches
 * discovery page, so they can pick one before accessing the dashboard
 * or any other protected client feature.
 */
export const coachRequiredGuard: CanActivateFn = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);

  // Only applies to clients
  if (!auth.isClient()) return true;

  const hasCoach = !!auth.user()?.coach;
  if (hasCoach) return true;

  router.navigate(['/client/coaches']);
  return false;
};

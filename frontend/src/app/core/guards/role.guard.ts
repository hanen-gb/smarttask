import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/user.model';

/**
 * Guard parametrable : roleGuard(['ADMIN']) protege une route pour un ou
 * plusieurs roles precis. S'utilise ainsi dans app.routes.ts :
 *   canActivate: [authGuard, roleGuard(['ADMIN'])]
 */
export function roleGuard(allowedRoles: Role[]): CanActivateFn {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const user = authService.currentUser();

    if (user && allowedRoles.includes(user.role)) {
      return true;
    }

    router.navigateByUrl('/dashboard');
    return false;
  };
}

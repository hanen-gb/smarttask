import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../services/token.service';

/**
 * Guard fonctionnel : s'execute AVANT d'autoriser la navigation vers une route.
 * Ici on verifie juste la presence d'un token -- une verification plus fine
 * (token expire, par exemple) sera ajoutee quand on gerera le refresh token.
 */
export const authGuard: CanActivateFn = () => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  if (tokenService.getToken()) {
    return true;
  }

  router.navigateByUrl('/login');
  return false;
};

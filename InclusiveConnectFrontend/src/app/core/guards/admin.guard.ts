import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const role = localStorage.getItem('ic_role');

  if (authService.isAuthenticated() && role === 'ROLE_ADMIN') {
    return true;
  }

  router.navigate(['/']);
  return false;
};
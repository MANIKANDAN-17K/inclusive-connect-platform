import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        localStorage.removeItem('ic_access_token');
        localStorage.removeItem('ic_refresh_token');
        localStorage.removeItem('ic_role');
        router.navigate(['/auth/login']);
      }
      return throwError(() => error);
    })
  );
};
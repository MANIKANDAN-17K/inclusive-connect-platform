import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // TODO: on 401, clear tokens and redirect to /auth/login (Sprint 2+)
      return throwError(() => error);
    })
  );
};
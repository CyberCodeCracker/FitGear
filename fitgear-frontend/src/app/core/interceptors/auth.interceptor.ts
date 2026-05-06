import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError, BehaviorSubject, filter, take } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../services/auth.service';

const API = 'http://localhost:8080/api/v1';

// Shared state across interceptor invocations
let isRefreshing  = false;
const refreshDone$ = new BehaviorSubject<string | null>(null);

function addToken(req: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
}

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const auth = inject(AuthService);
  const http = inject(HttpClient);

  const token = auth.getToken();
  const authedReq = token ? addToken(req, token) : req;

  return next(authedReq).pipe(
    catchError((err: HttpErrorResponse) => {
      // Only intercept 401s; skip the refresh endpoint itself to avoid loops
      if (err.status !== 401 || req.url.includes('/auth/refresh-token')) {
        return throwError(() => err);
      }

      const refreshToken = localStorage.getItem('fg_refresh');
      if (!refreshToken) {
        auth.logout();
        return throwError(() => err);
      }

      // If a refresh is already in-flight, queue this request until it finishes
      if (isRefreshing) {
        return refreshDone$.pipe(
          filter(t => t !== null),
          take(1),
          switchMap(newToken => next(addToken(req, newToken!)))
        );
      }

      isRefreshing = true;
      refreshDone$.next(null);

      return http.post<{ token: string; refreshToken: string }>(
        `${API}/auth/refresh-token`,
        { refreshToken }
      ).pipe(
        switchMap(res => {
          isRefreshing = false;
          localStorage.setItem('fg_token',   res.token);
          localStorage.setItem('fg_refresh', res.refreshToken);
          refreshDone$.next(res.token);
          return next(addToken(req, res.token));
        }),
        catchError(refreshErr => {
          isRefreshing = false;
          refreshDone$.next(null);
          auth.logout();
          return throwError(() => refreshErr);
        })
      );
    })
  );
};

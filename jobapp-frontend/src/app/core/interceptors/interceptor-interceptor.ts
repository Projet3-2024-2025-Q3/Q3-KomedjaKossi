import { HttpInterceptorFn } from '@angular/common/http';

export const interceptorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req);
};

import { Injectable } from '@angular/core';
import {
  HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';


@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url.includes('/auth/')) {
      return next.handle(req);
    }

    if (!this.auth.isAuthenticated()) {
      this.auth.clearToken();
      this.router.navigate(['/login']);
      return throwError(() => new Error('Missing or expired JWT'));
    }

    const token = this.auth.getToken()!;
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });

   
    return next.handle(authReq).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401 || err.status === 403) {
          this.auth.clearToken();
          this.router.navigate(['/login']);
        }
        return throwError(() => err);
      })
    );
    }
}

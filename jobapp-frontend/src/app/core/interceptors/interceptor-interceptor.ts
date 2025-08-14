import { HttpInterceptorFn } from '@angular/common/http';

export const interceptorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req);
};
// core/interceptors/auth.interceptor.ts
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
    // 1) Ne rien faire pour les endpoints publics d’auth
    if (req.url.includes('/auth/')) {
      return next.handle(req);
    }

    // 2) Token manquant ou expiré → nettoyage + redirection
    if (!this.auth.isAuthenticated()) {
      this.auth.clearToken();
      this.router.navigate(['/login']);
      return throwError(() => new Error('JWT manquant ou expiré'));
    }

    // 3) Attacher l’en-tête Authorization
    const token = this.auth.getToken()!;
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });

    // 4) Réagir aux 401/403
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

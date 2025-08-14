import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  private authService = inject(AuthService);
  private router = inject(Router);

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const expectedRoles: string[] = route.data?.['roles'] ?? [];
    const userRole = this.authService.getRole();

    if (!this.authService.isAuthenticated()) {
      return this.router.parseUrl('/login');
    }
    if (expectedRoles.length && (!userRole || !expectedRoles.includes(userRole))) {
      return this.router.parseUrl('/dashboard'); 
    }
    return true;
  }
}

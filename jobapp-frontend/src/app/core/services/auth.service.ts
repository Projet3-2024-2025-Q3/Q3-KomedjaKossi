import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/enviromnet';

export interface AuthResponse { token: string }
export interface ErrorResponse { message: string }

export type Role = 'ADMIN' | 'COMPANY' | 'STUDENT';

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role: Role;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  companyName?: string;
}

export interface UserResponse {
  id?: string | number;
  username: string;
  email: string;
  role: Role;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  companyName?: string;
}

const TOKEN_KEY = 'auth_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  login(payload: { username: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/login`, payload);
  }

  register(payload: RegisterRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.baseUrl}/auth/register`, payload);
  }

  forgotPassword(email: string): Observable<string> {
    const params = new HttpParams().set('email', email);
    return this.http.post<string>(`${this.baseUrl}/auth/forgot-password`, null, {
      params,
      responseType: 'text' as 'json'
    });
  }

  changePassword(payload: { username: string; oldPassword: string; newPassword: string }): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/auth/change-password`, payload);
  }

  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  clearToken(): void {
    localStorage.removeItem(TOKEN_KEY);
  }

  private decodePayload(): any | null {
    const token = this.getToken();
    if (!token) return null;
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    try {
      const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(json);
    } catch {
      return null;
    }
  }

  isTokenExpired(leewaySeconds = 0): boolean {
    const payload = this.decodePayload();
    if (!payload?.exp) return true;
    const now = Math.floor(Date.now() / 1000);
    return now >= (payload.exp - leewaySeconds);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token && !this.isTokenExpired(5);
  }

  getRole(): Role | null {
    const payload = this.decodePayload();
    if (!payload) return null;
    if (Array.isArray(payload.authorities) && payload.authorities.length > 0) {
      return payload.authorities[0] as Role;
    }
    if (payload.role) {
      return payload.role as Role;
    }
    return null;
  }

  hasRole(role: Role): boolean {
    return this.getRole() === role;
  }
}

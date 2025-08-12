import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/enviromnet';

export interface AuthResponse { token: string }
export interface ErrorResponse { message: string }

const TOKEN_KEY = 'auth_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  // Now uses username directly
  login(payload: { username: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/login`, payload);
  }

  setToken(token: string): void { localStorage.setItem(TOKEN_KEY, token); }
  getToken(): string | null { return localStorage.getItem(TOKEN_KEY); }
  clearToken(): void { localStorage.removeItem(TOKEN_KEY); }
  isAuthenticated(): boolean { return !!this.getToken(); }
}

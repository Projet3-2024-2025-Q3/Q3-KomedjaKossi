import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/enviromnet';

export interface AuthResponse { token: string }
export interface ErrorResponse { message: string }

const TOKEN_KEY = 'auth_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}


  login(payload: { username: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/login`, payload);
  }

  
register(payload: RegisterRequest) {
  return this.http.post<UserResponse>(`${this.baseUrl}/auth/register`, payload);
}

forgotPassword(email: string): Observable<string> {
  const params = new HttpParams().set('email', email);
  return this.http.post<string>(`${this.baseUrl}/auth/forgot-password`, null, {
    params,
    responseType: 'text' as 'json' // <-- force "text" tout en gardant le typage TS
  });
}

  // (Optionnel) Change password: PUT /auth/change-password
  changePassword(payload: { username: string; oldPassword: string; newPassword: string }): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/auth/change-password`, payload);
  }


  setToken(token: string): void { localStorage.setItem(TOKEN_KEY, token); }
  getToken(): string | null { return localStorage.getItem(TOKEN_KEY); }
  clearToken(): void { localStorage.removeItem(TOKEN_KEY); }
  isAuthenticated(): boolean { return !!this.getToken(); }
}

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





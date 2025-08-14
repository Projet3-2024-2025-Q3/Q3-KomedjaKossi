// core/services/admin-user.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/enviromnet';

export type Role = 'ADMIN' | 'COMPANY' | 'STUDENT';

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: Role;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  companyName?: string;
}

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

@Injectable({
  providedIn: 'root'
})
export class adminservice {
  private readonly baseUrl = `${environment.apiBaseUrl}/admin/users`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(this.baseUrl);
  }


  update(id: Number, user: RegisterRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.baseUrl}/${id}`, user);
  }

  delete(id: Number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}

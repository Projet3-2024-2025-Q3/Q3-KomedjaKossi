import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/enviromnet';
import { OfferResponse, OfferRequest } from '../../models/interfaces';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class CompanyOfferService {
  private readonly baseUrl = `${environment.apiBaseUrl}/company/offers`;
  
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getUserId(): number {
    const payload = this.authService.decodePayload?.();
    return payload?.id || 0;
  }
 
  getMyOffers(): Observable<OfferResponse[]> {
    const userId = this.getUserId();
    return this.http.get<OfferResponse[]>(`${this.baseUrl}?userId=${userId}`);
  }
  
  createOffer(request: OfferRequest): Observable<OfferResponse> {
    const userId = this.getUserId();
    return this.http.post<OfferResponse>(`${this.baseUrl}?userId=${userId}`, request);
  }
 
  updateOffer(id: number, request: OfferRequest): Observable<OfferResponse> {
    const userId = this.getUserId();
    return this.http.put<OfferResponse>(`${this.baseUrl}/${id}?userId=${userId}`, request);
  }
  
  deleteOffer(id: number): Observable<void> {
    const userId = this.getUserId();
    return this.http.delete<void>(`${this.baseUrl}/${id}?userId=${userId}`);
  }
}
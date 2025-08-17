import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/enviromnet';
import { OfferResponse } from '../../models/interfaces';

@Injectable({ providedIn: 'root' })
export class StudentOfferService {
 
  private baseUrl = `${environment.apiBaseUrl}/offers`;

  constructor(private http: HttpClient) {}

  getAll(params?: HttpParams): Observable<OfferResponse[]> {
    return this.http.get<OfferResponse[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<OfferResponse> {
    return this.http.get<OfferResponse>(`${this.baseUrl}/${id}`);
  }

  apply(id: number, files: { cv: File; motivation: File }): Observable<void> {
    const form = new FormData();
    form.append('cv', files.cv);
    form.append('motivation', files.motivation);
   
    return this.http.post<void>(`${this.baseUrl}/${id}/apply`, form);
  }

  applyWithProgress(id: number, files: { cv: File; motivation: File }): Observable<HttpEvent<unknown>> {
    const form = new FormData();
    form.append('cv', files.cv);
    form.append('motivation', files.motivation);
    return this.http.post(`${this.baseUrl}/${id}/apply`, form, {
      reportProgress: true,
      observe: 'events'
    });
  }
}

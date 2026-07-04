import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Company } from '../models/company.model';

export interface CreateCompanyPayload {
  companyName: string;
  website?: string;
  industry?: string;
  description?: string;
  companySize?: string;
}

export type UpdateCompanyPayload = Partial<CreateCompanyPayload>;

@Injectable({ providedIn: 'root' })
export class CompanyService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/companies`;

  createCompany(payload: CreateCompanyPayload): Observable<ApiResponse<Company>> {
    return this.http.post<ApiResponse<Company>>(this.baseUrl, payload);
  }

  getMyCompany(): Observable<ApiResponse<Company>> {
    return this.http.get<ApiResponse<Company>>(`${this.baseUrl}/me`);
  }

  getCompanyById(id: number): Observable<ApiResponse<Company>> {
    return this.http.get<ApiResponse<Company>>(`${this.baseUrl}/${id}`);
  }

  updateCompany(payload: UpdateCompanyPayload): Observable<ApiResponse<Company>> {
    return this.http.put<ApiResponse<Company>>(this.baseUrl, payload);
  }
}
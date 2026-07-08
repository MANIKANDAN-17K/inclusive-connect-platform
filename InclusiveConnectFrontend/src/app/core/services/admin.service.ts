import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { AdminCompany, AdminDashboard, AdminUser } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/admin`;

  getDashboard(): Observable<ApiResponse<AdminDashboard>> {
    return this.http.get<ApiResponse<AdminDashboard>>(`${this.baseUrl}/dashboard`);
  }

  getAllUsers(): Observable<ApiResponse<AdminUser[]>> {
    return this.http.get<ApiResponse<AdminUser[]>>(`${this.baseUrl}/users`);
  }

  blockUser(id: number): Observable<ApiResponse<AdminUser>> {
    return this.http.patch<ApiResponse<AdminUser>>(`${this.baseUrl}/users/${id}/block`, {});
  }

  unblockUser(id: number): Observable<ApiResponse<AdminUser>> {
    return this.http.patch<ApiResponse<AdminUser>>(`${this.baseUrl}/users/${id}/unblock`, {});
  }

  getPendingEmployers(): Observable<ApiResponse<AdminCompany[]>> {
    return this.http.get<ApiResponse<AdminCompany[]>>(`${this.baseUrl}/employers/pending`);
  }

  verifyEmployer(id: number): Observable<ApiResponse<AdminCompany>> {
    return this.http.patch<ApiResponse<AdminCompany>>(`${this.baseUrl}/employers/${id}/verify`, {});
  }
}
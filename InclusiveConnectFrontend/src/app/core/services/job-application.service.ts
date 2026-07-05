import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { ApplicationStatus, JobApplication } from '../models/job-application.model';

@Injectable({ providedIn: 'root' })
export class JobApplicationService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  applyToJob(jobId: number, coverLetter?: string): Observable<ApiResponse<JobApplication>> {
    return this.http.post<ApiResponse<JobApplication>>(`${this.baseUrl}/jobs/${jobId}/apply`, { coverLetter });
  }

  getMyApplications(): Observable<ApiResponse<JobApplication[]>> {
    return this.http.get<ApiResponse<JobApplication[]>>(`${this.baseUrl}/applications/me`);
  }

  getApplicationsForJob(jobId: number): Observable<ApiResponse<JobApplication[]>> {
    return this.http.get<ApiResponse<JobApplication[]>>(`${this.baseUrl}/jobs/${jobId}/applications`);
  }

  updateApplicationStatus(applicationId: number, status: ApplicationStatus): Observable<ApiResponse<JobApplication>> {
    return this.http.patch<ApiResponse<JobApplication>>(`${this.baseUrl}/applications/${applicationId}/status`, { status });
  }
}
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { EmploymentType, Job, PageResponse } from '../models/job.model';

export interface JobSearchParams {
  keyword?: string;
  location?: string;
  employmentType?: EmploymentType | '';
  page?: number;
  size?: number;
}

export interface CreateJobPayload {
  title: string;
  description: string;
  location?: string;
  employmentType: EmploymentType;
  salaryRange?: string;
  experienceRequired?: string;
  accessibilityNotes?: string;
  applicationDeadline?: string;
}

export type UpdateJobPayload = Partial<CreateJobPayload> & { status?: string };

@Injectable({ providedIn: 'root' })
export class JobService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/jobs`;

  searchJobs(params: JobSearchParams): Observable<ApiResponse<PageResponse<Job>>> {
    let httpParams = new HttpParams()
      .set('page', params.page ?? 0)
      .set('size', params.size ?? 10);

    if (params.keyword) httpParams = httpParams.set('keyword', params.keyword);
    if (params.location) httpParams = httpParams.set('location', params.location);
    if (params.employmentType) httpParams = httpParams.set('employmentType', params.employmentType);

    return this.http.get<ApiResponse<PageResponse<Job>>>(this.baseUrl, { params: httpParams });
  }

  getJobById(id: number): Observable<ApiResponse<Job>> {
    return this.http.get<ApiResponse<Job>>(`${this.baseUrl}/${id}`);
  }

  getMyJobs(): Observable<ApiResponse<Job[]>> {
    return this.http.get<ApiResponse<Job[]>>(`${this.baseUrl}/my-jobs`);
  }

  createJob(payload: CreateJobPayload): Observable<ApiResponse<Job>> {
    return this.http.post<ApiResponse<Job>>(this.baseUrl, payload);
  }

  updateJob(id: number, payload: UpdateJobPayload): Observable<ApiResponse<Job>> {
    return this.http.put<ApiResponse<Job>>(`${this.baseUrl}/${id}`, payload);
  }

  deleteJob(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
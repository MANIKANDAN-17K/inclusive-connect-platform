import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Education, Experience, Profile, Skill } from '../models/profile.model';

export interface UpdateProfilePayload {
  headline?: string;
  location?: string;
  about?: string;
  disabilityType?: string;
  linkedinUrl?: string;
  githubUrl?: string;
  portfolioUrl?: string;
}

export interface EducationPayload {
  institutionName: string;
  degree: string;
  fieldOfStudy?: string;
  startDate?: string;
  endDate?: string;
  cgpa?: number;
  description?: string;
}

export interface ExperiencePayload {
  companyName: string;
  jobTitle: string;
  employmentType?: string;
  startDate?: string;
  endDate?: string;
  currentlyWorking: boolean;
  description?: string;
}

export interface SkillPayload {
  skillName: string;
  proficiencyLevel: string;
}

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  getMyProfile(): Observable<ApiResponse<Profile>> {
    return this.http.get<ApiResponse<Profile>>(`${this.baseUrl}/profile/me`);
  }

  updateProfile(payload: UpdateProfilePayload): Observable<ApiResponse<Profile>> {
    return this.http.put<ApiResponse<Profile>>(`${this.baseUrl}/profile`, payload);
  }

  addEducation(payload: EducationPayload): Observable<ApiResponse<Education>> {
    return this.http.post<ApiResponse<Education>>(`${this.baseUrl}/educations`, payload);
  }

  updateEducation(id: number, payload: EducationPayload): Observable<ApiResponse<Education>> {
    return this.http.put<ApiResponse<Education>>(`${this.baseUrl}/educations/${id}`, payload);
  }

  deleteEducation(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/educations/${id}`);
  }

  addExperience(payload: ExperiencePayload): Observable<ApiResponse<Experience>> {
    return this.http.post<ApiResponse<Experience>>(`${this.baseUrl}/experiences`, payload);
  }

  updateExperience(id: number, payload: ExperiencePayload): Observable<ApiResponse<Experience>> {
    return this.http.put<ApiResponse<Experience>>(`${this.baseUrl}/experiences/${id}`, payload);
  }

  deleteExperience(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/experiences/${id}`);
  }

  addSkill(payload: SkillPayload): Observable<ApiResponse<Skill>> {
    return this.http.post<ApiResponse<Skill>>(`${this.baseUrl}/skills`, payload);
  }

  deleteSkill(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/skills/${id}`);
  }
}
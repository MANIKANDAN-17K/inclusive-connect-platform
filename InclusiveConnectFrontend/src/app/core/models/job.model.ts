export type EmploymentType = 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'INTERNSHIP' | 'REMOTE';
export type JobStatus = 'OPEN' | 'CLOSED' | 'DRAFT';

export interface Job {
  id: number;
  title: string;
  description: string;
  location?: string;
  employmentType: EmploymentType;
  salaryRange?: string;
  experienceRequired?: string;
  accessibilityNotes?: string;
  applicationDeadline?: string;
  status: JobStatus;
  createdAt: string;
  companyId: number;
  companyName: string;
  companyLogoUrl?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
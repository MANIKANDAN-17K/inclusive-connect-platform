export type ApplicationStatus = 'PENDING' | 'SHORTLISTED' | 'REJECTED' | 'HIRED';

export interface JobApplication {
  id: number;
  status: ApplicationStatus;
  coverLetter?: string;
  resumeUrl?: string;
  appliedAt: string;

  jobId: number;
  jobTitle: string;
  companyName: string;

  candidateId: number;
  candidateName: string;
  candidateEmail: string;
}
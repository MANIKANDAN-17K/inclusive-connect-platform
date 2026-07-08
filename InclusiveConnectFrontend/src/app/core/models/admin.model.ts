export interface AdminUser {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  isVerified: boolean;
  isActive: boolean;
}

export interface AdminCompany {
  id: number;
  companyName: string;
  industry?: string;
  verified: boolean;
  employerUserId: number;
  employerEmail: string;
}

export interface AdminDashboard {
  totalUsers: number;
  totalCandidates: number;
  totalEmployers: number;
  totalJobs: number;
  pendingEmployerVerifications: number;
}
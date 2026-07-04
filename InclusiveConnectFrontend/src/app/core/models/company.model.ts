export interface Company {
  id: number;
  companyName: string;
  website?: string;
  industry?: string;
  description?: string;
  companySize?: string;
  logoUrl?: string;
  verified: boolean;
  employerUserId: number;
}
export interface Education {
  id: number;
  institutionName: string;
  degree: string;
  fieldOfStudy?: string;
  startDate?: string;
  endDate?: string;
  cgpa?: number;
  description?: string;
}

export interface Experience {
  id: number;
  companyName: string;
  jobTitle: string;
  employmentType?: string;
  startDate?: string;
  endDate?: string;
  currentlyWorking: boolean;
  description?: string;
}

export type ProficiencyLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';

export interface Skill {
  id: number;
  skillName: string;
  proficiencyLevel: ProficiencyLevel;
}

export interface Profile {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  headline?: string;
  location?: string;
  profilePicture?: string;
  about?: string;
  disabilityType?: string;
  linkedinUrl?: string;
  githubUrl?: string;
  portfolioUrl?: string;
  resumeUrl?: string;
  educations: Education[];
  experiences: Experience[];
  skills: Skill[];
}
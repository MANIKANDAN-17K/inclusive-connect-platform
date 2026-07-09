import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProfileService } from '../../../core/services/profile.service';
import { Profile } from '../../../core/models/profile.model';
import { FileUploadComponent } from '../../../shared/components/file-upload/file-upload.component';

@Component({
  selector: 'ic-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FileUploadComponent],
  templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit {
  private profileService = inject(ProfileService);
  private fb = new FormBuilder();

  profile: Profile | null = null;
  loading = true;
  loadError: string | null = null;

  // --- upload variables ---
  photoUploading = false;
  photoError: string | null = null;
  resumeUploading = false;
  resumeError: string | null = null;

  uploadProfilePhoto(file: File): void {
    this.photoUploading = true;
    this.photoError = null;
    this.profileService.uploadProfilePhoto(file).subscribe({
      next: (res) => {
        this.photoUploading = false;
        if (this.profile) {
          this.profile.profilePicture = res.data?.profilePictureUrl;
        }
      },
      error: (err) => {
        this.photoUploading = false;
        this.photoError = err?.error?.message ?? 'Failed to upload profile photo.';
      }
    });
  }

  uploadResume(file: File): void {
    this.resumeUploading = true;
    this.resumeError = null;
    this.profileService.uploadResume(file).subscribe({
      next: (res) => {
        this.resumeUploading = false;
        if (this.profile) {
          this.profile.resumeUrl = res.data?.resumeUrl;
          this.profile.resumeFileName = res.data?.resumeFileName;
          this.profile.resumeUploadedAt = res.data?.resumeUploadedAt;
        }
      },
      error: (err) => {
        this.resumeUploading = false;
        this.resumeError = err?.error?.message ?? 'Failed to upload resume.';
      }
    });
  }

  deleteResume(): void {
    if (!confirm('Are you sure you want to delete your resume?')) return;
    this.profileService.deleteResume().subscribe({
      next: () => {
        if (this.profile) {
          this.profile.resumeUrl = undefined;
          this.profile.resumeFileName = undefined;
          this.profile.resumeUploadedAt = undefined;
        }
      },
      error: (err) => {
        this.resumeError = err?.error?.message ?? 'Failed to delete resume.';
      }
    });
  }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const maxSizeBytes = 5 * 1024 * 1024;
      if (file.size > maxSizeBytes) {
        alert('Profile picture must be less than 5 MB.');
        return;
      }
      this.uploadProfilePhoto(file);
    }
  }

  // --- basic info edit ---
  editingBasicInfo = false;
  basicInfoForm = this.fb.group({
    headline: [''],
    location: [''],
    about: [''],
    linkedinUrl: [''],
    githubUrl: [''],
    portfolioUrl: [''],
  });
  savingBasicInfo = false;

  // --- education add form ---
  showEducationForm = false;
  educationForm = this.fb.group({
    institutionName: ['', Validators.required],
    degree: ['', Validators.required],
    fieldOfStudy: [''],
    startDate: [''],
    endDate: [''],
    cgpa: [null as number | null],
    description: [''],
  });
  savingEducation = false;

  // --- experience add form ---
  showExperienceForm = false;
  experienceForm = this.fb.group({
    companyName: ['', Validators.required],
    jobTitle: ['', Validators.required],
    employmentType: [''],
    startDate: [''],
    endDate: [''],
    currentlyWorking: [false],
    description: [''],
  });
  savingExperience = false;

  // --- skill add form ---
  skillForm = this.fb.group({
    skillName: ['', Validators.required],
    proficiencyLevel: ['BEGINNER'],
  });
  savingSkill = false;
  skillError: string | null = null;

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    this.profileService.getMyProfile().subscribe({
      next: (res) => {
        this.profile = res.data ?? null;
        this.loading = false;
      },
      error: (err) => {
        this.loadError = err?.error?.message ?? 'Failed to load profile.';
        this.loading = false;
      },
    });
  }

  // --- basic info ---
  startEditingBasicInfo(): void {
    if (!this.profile) return;
    this.basicInfoForm.patchValue({
      headline: this.profile.headline ?? '',
      location: this.profile.location ?? '',
      about: this.profile.about ?? '',
      linkedinUrl: this.profile.linkedinUrl ?? '',
      githubUrl: this.profile.githubUrl ?? '',
      portfolioUrl: this.profile.portfolioUrl ?? '',
    });
    this.editingBasicInfo = true;
  }

  saveBasicInfo(): void {
    this.savingBasicInfo = true;
    const raw = this.basicInfoForm.getRawValue();

    this.profileService
      .updateProfile({
        headline: raw.headline ?? undefined,
        location: raw.location ?? undefined,
        about: raw.about ?? undefined,
        linkedinUrl: raw.linkedinUrl ?? undefined,
        githubUrl: raw.githubUrl ?? undefined,
        portfolioUrl: raw.portfolioUrl ?? undefined,
      })
      .subscribe({
        next: (res) => {
          this.profile = res.data ?? this.profile;
          this.savingBasicInfo = false;
          this.editingBasicInfo = false;
        },
        error: () => {
          this.savingBasicInfo = false;
        },
      });
  }

  // --- education ---
  submitEducation(): void {
    if (this.educationForm.invalid) {
      this.educationForm.markAllAsTouched();
      return;
    }
    this.savingEducation = true;
    const payload = this.educationForm.getRawValue();
    this.profileService
      .addEducation({
        institutionName: payload.institutionName!,
        degree: payload.degree!,
        fieldOfStudy: payload.fieldOfStudy || undefined,
        startDate: payload.startDate || undefined,
        endDate: payload.endDate || undefined,
        cgpa: payload.cgpa ?? undefined,
        description: payload.description || undefined,
      })
      .subscribe({
        next: () => {
          this.savingEducation = false;
          this.showEducationForm = false;
          this.educationForm.reset();
          this.loadProfile();
        },
        error: () => {
          this.savingEducation = false;
        },
      });
  }

  deleteEducation(id: number): void {
    this.profileService.deleteEducation(id).subscribe(() => this.loadProfile());
  }

  // --- experience ---
  submitExperience(): void {
    if (this.experienceForm.invalid) {
      this.experienceForm.markAllAsTouched();
      return;
    }
    this.savingExperience = true;
    const payload = this.experienceForm.getRawValue();
    this.profileService
      .addExperience({
        companyName: payload.companyName!,
        jobTitle: payload.jobTitle!,
        employmentType: payload.employmentType || undefined,
        startDate: payload.startDate || undefined,
        endDate: payload.currentlyWorking ? undefined : payload.endDate || undefined,
        currentlyWorking: payload.currentlyWorking!,
        description: payload.description || undefined,
      })
      .subscribe({
        next: () => {
          this.savingExperience = false;
          this.showExperienceForm = false;
          this.experienceForm.reset({ currentlyWorking: false });
          this.loadProfile();
        },
        error: () => {
          this.savingExperience = false;
        },
      });
  }

  deleteExperience(id: number): void {
    this.profileService.deleteExperience(id).subscribe(() => this.loadProfile());
  }

  // --- skills ---
  submitSkill(): void {
    if (this.skillForm.invalid) {
      this.skillForm.markAllAsTouched();
      return;
    }
    this.savingSkill = true;
    this.skillError = null;
    const { skillName, proficiencyLevel } = this.skillForm.getRawValue();
    this.profileService.addSkill({ skillName: skillName!, proficiencyLevel: proficiencyLevel! }).subscribe({
      next: () => {
        this.savingSkill = false;
        this.skillForm.reset({ proficiencyLevel: 'BEGINNER' });
        this.loadProfile();
      },
      error: (err) => {
        this.savingSkill = false;
        this.skillError = err?.error?.message ?? 'Could not add skill.';
      },
    });
  }

  deleteSkill(id: number): void {
    this.profileService.deleteSkill(id).subscribe(() => this.loadProfile());
  }
}
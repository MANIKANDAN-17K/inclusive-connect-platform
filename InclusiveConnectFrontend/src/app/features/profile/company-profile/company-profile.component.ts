import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CompanyService } from '../../../core/services/company.service';
import { Company } from '../../../core/models/company.model';
import { FileUploadComponent } from '../../../shared/components/file-upload/file-upload.component';

@Component({
  selector: 'ic-company-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FileUploadComponent],
  templateUrl: './company-profile.component.html',
})
export class CompanyProfileComponent implements OnInit {
  private companyService = inject(CompanyService);
  private fb = new FormBuilder();

  company: Company | null = null;
  loading = true;
  hasCompany = false;
  editing = false;

  // --- upload variables ---
  logoUploading = false;
  logoError: string | null = null;
  coverUploading = false;
  coverError: string | null = null;

  uploadCompanyLogo(file: File): void {
    this.logoUploading = true;
    this.logoError = null;
    this.companyService.uploadCompanyLogo(file).subscribe({
      next: (res) => {
        this.logoUploading = false;
        if (this.company) {
          this.company.logoUrl = res.data?.logoUrl;
        }
      },
      error: (err) => {
        this.logoUploading = false;
        this.logoError = err?.error?.message ?? 'Failed to upload logo.';
      }
    });
  }

  uploadCompanyCover(file: File): void {
    this.coverUploading = true;
    this.coverError = null;
    this.companyService.uploadCompanyCover(file).subscribe({
      next: (res) => {
        this.coverUploading = false;
        if (this.company) {
          this.company.coverImageUrl = res.data?.coverImageUrl;
        }
      },
      error: (err) => {
        this.coverUploading = false;
        this.coverError = err?.error?.message ?? 'Failed to upload cover image.';
      }
    });
  }

  onLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const maxSizeBytes = 5 * 1024 * 1024;
      if (file.size > maxSizeBytes) {
        alert('Logo must be less than 5 MB.');
        return;
      }
      this.uploadCompanyLogo(file);
    }
  }

  onCoverSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const maxSizeBytes = 10 * 1024 * 1024;
      if (file.size > maxSizeBytes) {
        alert('Cover image must be less than 10 MB.');
        return;
      }
      this.uploadCompanyCover(file);
    }
  }

  form = this.fb.group({
    companyName: ['', Validators.required],
    website: [''],
    industry: [''],
    description: [''],
    companySize: [''],
  });

  saving = false;
  errorMessage: string | null = null;

  ngOnInit(): void {
    this.loadCompany();
  }

  loadCompany(): void {
    this.loading = true;
    this.companyService.getMyCompany().subscribe({
      next: (res) => {
        this.company = res.data ?? null;
        this.hasCompany = true;
        this.loading = false;
      },
      error: () => {
        // 400 here means "you haven't created a company yet" — not a real error,
        // just means we should show the create form instead.
        this.hasCompany = false;
        this.loading = false;
      },
    });
  }

  startEditing(): void {
    if (!this.company) return;
    this.form.patchValue({
      companyName: this.company.companyName,
      website: this.company.website ?? '',
      industry: this.company.industry ?? '',
      description: this.company.description ?? '',
      companySize: this.company.companySize ?? '',
    });
    this.editing = true;
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.errorMessage = null;
    const raw = this.form.getRawValue();

    const payload = {
      companyName: raw.companyName!,
      website: raw.website || undefined,
      industry: raw.industry || undefined,
      description: raw.description || undefined,
      companySize: raw.companySize || undefined,
    };

    const request$ = this.hasCompany
      ? this.companyService.updateCompany(payload)
      : this.companyService.createCompany(payload);

    request$.subscribe({
      next: (res) => {
        this.company = res.data ?? this.company;
        this.hasCompany = true;
        this.editing = false;
        this.saving = false;
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Something went wrong. Please try again.';
        this.saving = false;
      },
    });
  }
}
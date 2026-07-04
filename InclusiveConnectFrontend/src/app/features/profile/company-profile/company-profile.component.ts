import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CompanyService } from '../../../core/services/company.service';
import { Company } from '../../../core/models/company.model';

@Component({
  selector: 'ic-company-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './company-profile.component.html',
})
export class CompanyProfileComponent implements OnInit {
  private companyService = inject(CompanyService);
  private fb = new FormBuilder();

  company: Company | null = null;
  loading = true;
  hasCompany = false;
  editing = false;

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
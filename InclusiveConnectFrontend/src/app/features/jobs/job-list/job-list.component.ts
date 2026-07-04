import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { JobService } from '../../../core/services/job.service';
import { Job } from '../../../core/models/job.model';

@Component({
  selector: 'ic-job-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './job-list.component.html',
})
export class JobListComponent implements OnInit {
  private jobService = inject(JobService);
  private fb = new FormBuilder();

  filtersForm = this.fb.group({
    keyword: [''],
    location: [''],
    employmentType: [''],
  });

  jobs: Job[] = [];
  loading = true;
  errorMessage: string | null = null;

  pageNumber = 0;
  totalPages = 0;
  totalElements = 0;
  pageSize = 10;

  ngOnInit(): void {
    this.search();

    // Re-search automatically as the user types, but wait 400ms after they
    // stop typing so we don't fire a request on every keystroke.
    this.filtersForm.valueChanges.pipe(debounceTime(400), distinctUntilChanged()).subscribe(() => {
      this.pageNumber = 0;
      this.search();
    });
  }

  search(): void {
    this.loading = true;
    this.errorMessage = null;
    const { keyword, location, employmentType } = this.filtersForm.getRawValue();

    this.jobService
      .searchJobs({
        keyword: keyword || undefined,
        location: location || undefined,
        employmentType: (employmentType as any) || undefined,
        page: this.pageNumber,
        size: this.pageSize,
      })
      .subscribe({
        next: (res) => {
          const page = res.data!;
          this.jobs = page.content;
          this.totalPages = page.totalPages;
          this.totalElements = page.totalElements;
          this.loading = false;
        },
        error: (err) => {
          this.errorMessage = err?.error?.message ?? 'Failed to load jobs.';
          this.loading = false;
        },
      });
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.pageNumber = page;
    this.search();
  }
}
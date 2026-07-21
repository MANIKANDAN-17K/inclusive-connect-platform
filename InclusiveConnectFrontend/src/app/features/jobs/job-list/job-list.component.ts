import { Component, OnInit, inject, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { JobService } from '../../../core/services/job.service';
import { Job } from '../../../core/models/job.model';
import { IconComponent } from '../../../shared/components/icon/icon.component';

@Component({
  selector: 'ic-job-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, IconComponent],
  templateUrl: './job-list.component.html',
})
export class JobListComponent implements OnInit {
  private jobService = inject(JobService);
  private fb = new FormBuilder();

  filtersForm = this.fb.group({
    keyword:        [''],
    location:       [''],
    employmentType: [''],
  });

  jobs:          Job[]  = [];
  loading        = true;
  errorMessage:  string | null = null;

  pageNumber    = 0;
  totalPages    = 0;
  totalElements = 0;
  pageSize      = 15;

  selectedJob:  Job | null = null;
  isDesktop     = false;

  ngOnInit(): void {
    this.checkDesktop();
    this.search();

    this.filtersForm.valueChanges
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe(() => { this.pageNumber = 0; this.search(); });
  }

  @HostListener('window:resize')
  checkDesktop(): void {
    this.isDesktop = window.innerWidth >= 1024;
    if (!this.isDesktop) this.selectedJob = null;
  }

  search(): void {
    this.loading      = true;
    this.errorMessage = null;
    const { keyword, location, employmentType } = this.filtersForm.getRawValue();

    this.jobService.searchJobs({
      keyword:        keyword || undefined,
      location:       location || undefined,
      employmentType: (employmentType as any) || undefined,
      page:           this.pageNumber,
      size:           this.pageSize,
    }).subscribe({
      next: (res) => {
        const page   = res.data!;
        this.jobs         = page.content;
        this.totalPages   = page.totalPages;
        this.totalElements = page.totalElements;
        this.loading      = false;
        // Auto-select first job on desktop
        if (this.isDesktop && this.jobs.length && !this.selectedJob) {
          this.selectedJob = this.jobs[0];
        }
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Failed to load jobs.';
        this.loading      = false;
      },
    });
  }

  selectJob(job: Job): void {
    this.selectedJob = job;
  }

  clearSelection(): void {
    this.selectedJob = null;
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.pageNumber  = page;
    this.selectedJob = null;
    this.search();
  }

  employmentTypeLabel(type: string): string {
    const map: Record<string, string> = {
      FULL_TIME:  'Full-time',
      PART_TIME:  'Part-time',
      CONTRACT:   'Contract',
      INTERNSHIP: 'Internship',
      REMOTE:     'Remote',
    };
    return map[type] ?? type;
  }
}

import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { JobApplicationService } from '../../../core/services/job-application.service';
import { JobApplication } from '../../../core/models/job-application.model';

@Component({
  selector: 'ic-my-applications',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-applications.component.html',
})
export class MyApplicationsComponent implements OnInit {
  private applicationService = inject(JobApplicationService);

  applications: JobApplication[] = [];
  loading = true;
  errorMessage: string | null = null;

  ngOnInit(): void {
    this.applicationService.getMyApplications().subscribe({
      next: (res) => {
        this.applications = res.data ?? [];
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Failed to load applications.';
        this.loading = false;
      },
    });
  }

  statusColor(status: string): string {
    switch (status) {
      case 'SHORTLISTED': return 'bg-secondary/20 text-secondary';
      case 'HIRED': return 'bg-success/20 text-success';
      case 'REJECTED': return 'bg-error/20 text-error';
      default: return 'bg-gray-100 text-gray-600';
    }
  }
}
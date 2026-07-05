import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { JobService } from '../../../core/services/job.service';
import { JobApplicationService } from '../../../core/services/job-application.service';
import { AuthService } from '../../../core/services/auth.service';
import { Job } from '../../../core/models/job.model';

@Component({
  selector: 'ic-job-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './job-detail.component.html',
})
export class JobDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private jobService = inject(JobService);
  private applicationService = inject(JobApplicationService);
  private authService = inject(AuthService);
  private fb = new FormBuilder();

  job: Job | null = null;
  loading = true;
  errorMessage: string | null = null;

  showApplyForm = false;
  applying = false;
  applySuccess = false;
  applyError: string | null = null;

  coverLetterForm = this.fb.group({
    coverLetter: [''],
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.jobService.getJobById(id).subscribe({
      next: (res) => {
        this.job = res.data ?? null;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Job not found.';
        this.loading = false;
      },
    });
  }

  onApplyClick(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/auth/login']);
      return;
    }
    this.showApplyForm = true;
  }

  submitApplication(): void {
    if (!this.job) return;
    this.applying = true;
    this.applyError = null;
    const { coverLetter } = this.coverLetterForm.getRawValue();

    this.applicationService.applyToJob(this.job.id, coverLetter || undefined).subscribe({
      next: () => {
        this.applying = false;
        this.applySuccess = true;
        this.showApplyForm = false;
      },
      error: (err) => {
        this.applying = false;
        this.applyError = err?.error?.message ?? 'Could not submit application.';
      },
    });
  }
}
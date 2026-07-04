import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { JobService } from '../../../core/services/job.service';
import { Job } from '../../../core/models/job.model';

@Component({
  selector: 'ic-job-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './job-detail.component.html',
})
export class JobDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private jobService = inject(JobService);

  job: Job | null = null;
  loading = true;
  errorMessage: string | null = null;

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
}
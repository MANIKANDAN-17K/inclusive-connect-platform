import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'ic-verify-email',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section class="min-h-screen flex flex-col items-center justify-center text-center px-4">
      <p *ngIf="status === 'loading'">Verifying your email…</p>
      <p *ngIf="status === 'success'" class="text-success">{{ message }}</p>
      <p *ngIf="status === 'error'" class="text-error">{{ message }}</p>
      <a routerLink="/auth/login" class="mt-4 text-primary underline">Go to sign in</a>
    </section>
  `,
})
export class VerifyEmailComponent implements OnInit {
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);

  status: 'loading' | 'success' | 'error' = 'loading';
  message = '';

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.status = 'error';
      this.message = 'Verification link is missing a token.';
      return;
    }

    this.authService.verifyEmail(token).subscribe({
      next: (res) => {
        this.status = 'success';
        this.message = res.message;
      },
      error: (err) => {
        this.status = 'error';
        this.message = err?.error?.message ?? 'Verification failed. The link may have expired.';
      },
    });
  }
}
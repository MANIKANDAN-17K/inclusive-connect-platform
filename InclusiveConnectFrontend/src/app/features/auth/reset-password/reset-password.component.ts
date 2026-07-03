import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'ic-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reset-password.component.html',
})
export class ResetPasswordComponent implements OnInit {
  private fb = new FormBuilder();
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private token = '';
  tokenMissing = false;

  form = this.fb.group({
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
  });

  submitting = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';
    this.tokenMissing = !this.token;
  }

  onSubmit(): void {
    if (this.form.invalid || this.tokenMissing) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.errorMessage = null;
    const { newPassword } = this.form.getRawValue();

    this.authService.resetPassword({ token: this.token, newPassword: newPassword! }).subscribe({
      next: (res) => {
        this.submitting = false;
        this.successMessage = res.message;
        setTimeout(() => this.router.navigate(['/auth/login']), 2000);
      },
      error: (err) => {
        this.submitting = false;
        this.errorMessage = err?.error?.message ?? 'This link may have expired. Please request a new one.';
      },
    });
  }
}
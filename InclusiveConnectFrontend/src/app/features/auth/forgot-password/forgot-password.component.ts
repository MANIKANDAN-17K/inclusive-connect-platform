import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'ic-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
})
export class ForgotPasswordComponent {
  private fb = new FormBuilder();
  private authService = inject(AuthService);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  submitting = false;
  message: string | null = null;

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const { email } = this.form.getRawValue();

    this.authService.forgotPassword({ email: email! }).subscribe({
      next: (res) => {
        this.submitting = false;
        this.message = res.message; // same generic message whether or not the email exists
      },
      error: () => {
        this.submitting = false;
        this.message = 'Something went wrong. Please try again.';
      },
    });
  }
}
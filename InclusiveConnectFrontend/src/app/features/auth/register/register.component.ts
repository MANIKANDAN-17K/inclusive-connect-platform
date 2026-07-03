import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'ic-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  private fb = new FormBuilder();
  private authService = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required],
    role: ['CANDIDATE' as 'CANDIDATE' | 'EMPLOYER', Validators.required],
  });

  submitting = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { password, confirmPassword } = this.form.getRawValue();
    if (password !== confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.submitting = true;
    this.errorMessage = null;

    const { firstName, lastName, email, role } = this.form.getRawValue();

    this.authService
      .register({ firstName: firstName!, lastName: lastName!, email: email!, password: password!, role: role! })
      .subscribe({
        next: (res) => {
          this.submitting = false;
          this.successMessage = res.message;
          setTimeout(() => this.router.navigate(['/auth/login']), 2000);
        },
        error: (err) => {
          this.submitting = false;
          this.errorMessage = err?.error?.message ?? 'Something went wrong. Please try again.';
        },
      });
  }
}
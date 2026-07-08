import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../../core/services/admin.service';
import { AdminCompany } from '../../../core/models/admin.model';

@Component({
  selector: 'ic-admin-employers',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-employers.component.html',
})
export class AdminEmployersComponent implements OnInit {
  private adminService = inject(AdminService);

  companies: AdminCompany[] = [];
  loading = true;
  verifyingId: number | null = null;

  ngOnInit(): void {
    this.loadPending();
  }

  loadPending(): void {
    this.loading = true;
    this.adminService.getPendingEmployers().subscribe({
      next: (res) => {
        this.companies = res.data ?? [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  verify(company: AdminCompany): void {
    this.verifyingId = company.id;
    this.adminService.verifyEmployer(company.id).subscribe({
      next: () => {
        this.companies = this.companies.filter((c) => c.id !== company.id);
        this.verifyingId = null;
      },
      error: () => {
        this.verifyingId = null;
      },
    });
  }
}
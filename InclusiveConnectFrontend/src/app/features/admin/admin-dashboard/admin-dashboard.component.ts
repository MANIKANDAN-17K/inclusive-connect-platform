import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../../core/services/admin.service';
import { AdminDashboard } from '../../../core/models/admin.model';

@Component({
  selector: 'ic-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-dashboard.component.html',
})
export class AdminDashboardComponent implements OnInit {
  private adminService = inject(AdminService);

  stats: AdminDashboard | null = null;
  loading = true;

  ngOnInit(): void {
    this.adminService.getDashboard().subscribe({
      next: (res) => {
        this.stats = res.data ?? null;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }
}
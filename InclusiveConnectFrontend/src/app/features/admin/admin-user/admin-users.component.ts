import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../../core/services/admin.service';
import { AdminUser } from '../../../core/models/admin.model';

@Component({
  selector: 'ic-admin-users',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-users.component.html',
})
export class AdminUsersComponent implements OnInit {
  private adminService = inject(AdminService);

  users: AdminUser[] = [];
  loading = true;
  actioningId: number | null = null;

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.adminService.getAllUsers().subscribe({
      next: (res) => {
        this.users = res.data ?? [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  toggleBlock(user: AdminUser): void {
    this.actioningId = user.id;
    const action$ = user.isActive
      ? this.adminService.blockUser(user.id)
      : this.adminService.unblockUser(user.id);

    action$.subscribe({
      next: (res) => {
        const updated = res.data;
        if (updated) {
          const index = this.users.findIndex((u) => u.id === updated.id);
          if (index !== -1) this.users[index] = updated;
        }
        this.actioningId = null;
      },
      error: () => {
        this.actioningId = null;
      },
    });
  }
}
import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import { AppNotification } from '../../core/models/notification.model';

@Component({
  selector: 'ic-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent implements OnInit, OnDestroy {
  private notificationService = inject(NotificationService);
  private authService = inject(AuthService);
  private router = inject(Router);

  isLoggedIn = false;
  showDropdown = false;
  unreadCount = 0;
  notifications: AppNotification[] = [];

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuthenticated();

    if (this.isLoggedIn) {
      this.notificationService.refreshUnreadCount();
      this.notificationService.connectRealtime();

      this.notificationService.unreadCount$.subscribe((count) => {
        this.unreadCount = count;
      });

      this.notificationService.newNotification$.subscribe((notification) => {
        if (notification) {
          this.notifications = [notification, ...this.notifications];
        }
      });
    }
  }

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown;
    if (this.showDropdown && this.notifications.length === 0) {
      this.notificationService.getMyNotifications().subscribe({
        next: (res) => {
          this.notifications = res.data ?? [];
        },
      });
    }
  }

  onNotificationClick(notification: AppNotification): void {
    if (!notification.isRead) {
      this.notificationService.markAsRead(notification.id).subscribe(() => {
        notification.isRead = true;
        this.notificationService.refreshUnreadCount();
      });
    }
    this.showDropdown = false;
    if (notification.linkUrl) {
      this.router.navigateByUrl(notification.linkUrl);
    }
  }

  logout(): void {
    this.notificationService.disconnect();
    this.authService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.notificationService.disconnect();
  }
}
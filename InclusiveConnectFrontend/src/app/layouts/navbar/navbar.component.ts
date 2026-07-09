import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, NavigationEnd } from '@angular/router';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import { AppNotification } from '../../core/models/notification.model';
import { Subscription, filter } from 'rxjs';

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

  isAdmin = false;
  isLoggedIn = false;
  showDropdown = false;
  unreadCount = 0;
  notifications: AppNotification[] = [];

  private routerSub: Subscription | null = null;
  private unreadSub: Subscription | null = null;
  private notifSub: Subscription | null = null;

  ngOnInit(): void {
    this.updateLoginState();

    this.routerSub = this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.updateLoginState();
    });
  }

  private updateLoginState(): void {
    const wasLoggedIn = this.isLoggedIn;
    this.isLoggedIn = this.authService.isAuthenticated();
    this.isAdmin = localStorage.getItem('ic_role') === 'ROLE_ADMIN';

    if (this.isLoggedIn && !wasLoggedIn) {
      this.unreadSub?.unsubscribe();
      this.notifSub?.unsubscribe();

      this.notificationService.refreshUnreadCount();
      this.notificationService.connectRealtime();

      this.unreadSub = this.notificationService.unreadCount$.subscribe((count) => {
        this.unreadCount = count;
      });

      this.notifSub = this.notificationService.newNotification$.subscribe((notification) => {
        if (notification) {
          this.notifications = [notification, ...this.notifications];
        }
      });
    } else if (!this.isLoggedIn && wasLoggedIn) {
      this.clearSubscriptions();
    }
  }

  private clearSubscriptions(): void {
    this.unreadSub?.unsubscribe();
    this.unreadSub = null;
    this.notifSub?.unsubscribe();
    this.notifSub = null;
    this.notificationService.disconnect();
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
    this.clearSubscriptions();
    this.authService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
    this.clearSubscriptions();
  }
}
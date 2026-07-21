import {
  Component, OnInit, OnDestroy, HostListener, inject, ElementRef, ViewChild
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, NavigationEnd } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import { AppNotification } from '../../core/models/notification.model';
import { IconComponent } from '../../shared/components/icon/icon.component';
import { Subscription, filter } from 'rxjs';

@Component({
  selector: 'ic-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, FormsModule, IconComponent],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent implements OnInit, OnDestroy {
  private notificationService = inject(NotificationService);
  private authService         = inject(AuthService);
  private router              = inject(Router);

  isAdmin    = false;
  isLoggedIn = false;
  showNotifDropdown = false;
  showProfileDropdown = false;
  mobileOpen = false;

  searchQuery = '';
  searchFocused = false;

  unreadCount = 0;
  notifications: AppNotification[] = [];
  userInitial = 'U';
  userName = '';

  private routerSub: Subscription | null = null;
  private unreadSub: Subscription | null = null;
  private notifSub:  Subscription | null = null;

  ngOnInit(): void {
    this.updateLoginState();
    this.routerSub = this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(() => {
        this.updateLoginState();
        this.mobileOpen         = false;
        this.showNotifDropdown  = false;
        this.showProfileDropdown = false;
      });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (this.showNotifDropdown   && !target.closest('[data-notif]'))   this.showNotifDropdown = false;
    if (this.showProfileDropdown && !target.closest('[data-profile]')) this.showProfileDropdown = false;
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.showNotifDropdown   = false;
    this.showProfileDropdown = false;
    this.mobileOpen          = false;
  }

  private updateLoginState(): void {
    const wasLoggedIn = this.isLoggedIn;
    this.isLoggedIn = this.authService.isAuthenticated();
    this.isAdmin    = localStorage.getItem('ic_role') === 'ROLE_ADMIN';

    const storedName = localStorage.getItem('ic_name') ?? '';
    this.userName    = storedName;
    this.userInitial = storedName.charAt(0).toUpperCase() || 'U';

    if (this.isLoggedIn && !wasLoggedIn) {
      this.unreadSub?.unsubscribe();
      this.notifSub?.unsubscribe();
      this.notificationService.refreshUnreadCount();
      this.notificationService.connectRealtime();
      this.unreadSub = this.notificationService.unreadCount$.subscribe(c => this.unreadCount = c);
      this.notifSub  = this.notificationService.newNotification$.subscribe(n => {
        if (n) this.notifications = [n, ...this.notifications];
      });
    } else if (!this.isLoggedIn && wasLoggedIn) {
      this.clearSubscriptions();
    }
  }

  private clearSubscriptions(): void {
    this.unreadSub?.unsubscribe(); this.unreadSub = null;
    this.notifSub?.unsubscribe();  this.notifSub  = null;
    this.notificationService.disconnect();
  }

  toggleNotifDropdown(): void {
    this.showProfileDropdown = false;
    this.showNotifDropdown   = !this.showNotifDropdown;
    if (this.showNotifDropdown && this.notifications.length === 0) {
      this.notificationService.getMyNotifications().subscribe({
        next: (res) => { this.notifications = res.data ?? []; },
      });
    }
  }

  toggleProfileDropdown(): void {
    this.showNotifDropdown   = false;
    this.showProfileDropdown = !this.showProfileDropdown;
  }

  onNotificationClick(n: AppNotification): void {
    if (!n.isRead) {
      this.notificationService.markAsRead(n.id).subscribe(() => {
        n.isRead = true;
        this.notificationService.refreshUnreadCount();
      });
    }
    this.showNotifDropdown = false;
    if (n.linkUrl) this.router.navigateByUrl(n.linkUrl);
  }

  onSearch(event: Event): void {
    event.preventDefault();
    if (this.searchQuery.trim()) {
      this.router.navigate(['/network/search'], { queryParams: { q: this.searchQuery.trim() } });
      this.searchQuery = '';
    }
  }

  logout(): void {
    this.clearSubscriptions();
    this.authService.logout();
    this.isLoggedIn = false;
    this.showProfileDropdown = false;
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
    this.clearSubscriptions();
  }
}

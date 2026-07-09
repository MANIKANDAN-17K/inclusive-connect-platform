import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationService } from '../../core/services/notification.service';
import { AppNotification } from '../../core/models/notification.model';

@Component({
    selector: 'ic-notifications',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './notifications.component.html',
})
export class NotificationsComponent implements OnInit {
    private notificationService = inject(NotificationService);
    private router = inject(Router);

    notifications: AppNotification[] = [];
    loading = true;

    ngOnInit(): void {
        this.notificationService.getMyNotifications().subscribe({
            next: (res) => {
                this.notifications = res.data ?? [];
                this.loading = false;
            },
            error: () => {
                this.loading = false;
            },
        });
    }

    markRead(notification: AppNotification): void {
        if (notification.isRead) return;
        this.notificationService.markAsRead(notification.id).subscribe({
            next: () => {
                notification.isRead = true;
                this.notificationService.refreshUnreadCount();
            },
        });
    }

    navigateTo(notification: AppNotification): void {
        this.markRead(notification);
        if (notification.linkUrl) {
            this.router.navigateByUrl(notification.linkUrl);
        }
    }

    markAllRead(): void {
        this.notifications
            .filter((n) => !n.isRead)
            .forEach((n) => this.markRead(n));
    }
}

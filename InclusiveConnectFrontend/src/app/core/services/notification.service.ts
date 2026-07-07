import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { AppNotification } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/notifications`;

  private stompClient: Client | null = null;
  private unreadCountSubject = new BehaviorSubject<number>(0);
  unreadCount$ = this.unreadCountSubject.asObservable();

  private newNotificationSubject = new BehaviorSubject<AppNotification | null>(null);
  newNotification$ = this.newNotificationSubject.asObservable();

  getMyNotifications(): Observable<ApiResponse<AppNotification[]>> {
    return this.http.get<ApiResponse<AppNotification[]>>(this.baseUrl);
  }

  getUnreadCount(): Observable<ApiResponse<{ count: number }>> {
    return this.http.get<ApiResponse<{ count: number }>>(`${this.baseUrl}/unread-count`);
  }

  markAsRead(id: number): Observable<ApiResponse<AppNotification>> {
    return this.http.patch<ApiResponse<AppNotification>>(`${this.baseUrl}/${id}/read`, {});
  }

  refreshUnreadCount(): void {
    this.getUnreadCount().subscribe({
      next: (res) => this.unreadCountSubject.next(res.data?.count ?? 0),
    });
  }

  connectRealtime(): void {
    const token = localStorage.getItem('ic_access_token');
    if (!token || this.stompClient?.active) return;

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(environment.wsUrl) as any,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      onConnect: () => {
        this.stompClient?.subscribe('/user/queue/notifications', (frame: IMessage) => {
          const notification: AppNotification = JSON.parse(frame.body);
          this.newNotificationSubject.next(notification);
          this.unreadCountSubject.next(this.unreadCountSubject.value + 1);
        });
      },
    });

    this.stompClient.activate();
    }
  /*
  async connectRealtime(): Promise<void> {
    const token = localStorage.getItem('ic_access_token');
    if (!token || this.stompClient?.active) return;
    
  const { default: SockJS } = await import('sockjs-client');

  this.stompClient = new Client({
    webSocketFactory: () => new SockJS(environment.wsUrl) as any,
    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },
    reconnectDelay: 5000,
  });

  this.stompClient.activate();
}
  */
  disconnect(): void {
    this.stompClient?.deactivate();
    this.stompClient = null;
  }
}
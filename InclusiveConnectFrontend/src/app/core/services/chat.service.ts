import { Injectable, inject, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Conversation, Message } from '../models/chat.model';

@Injectable({ providedIn: 'root' })
export class ChatService implements OnDestroy {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/chat`;

  private stompClient: Client | null = null;
  private messageSubject = new Subject<Message>();

  // Observable that components subscribe to for incoming real-time messages.
  messages$ = this.messageSubject.asObservable();

  // ---- REST: history ----

  getConversations(): Observable<ApiResponse<Conversation[]>> {
    return this.http.get<ApiResponse<Conversation[]>>(`${this.baseUrl}/conversations`);
  }

  getMessages(conversationId: number): Observable<ApiResponse<Message[]>> {
    return this.http.get<ApiResponse<Message[]>>(`${this.baseUrl}/conversations/${conversationId}/messages`);
  }

  // ---- WebSocket: real-time ----

  connect(conversationId: number): void {
    const token = localStorage.getItem('ic_access_token');
    if (!token) return;

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(environment.wsUrl) as any,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      onConnect: () => {
        this.stompClient?.subscribe(
          `/topic/conversation.${conversationId}`,
          (frame: IMessage) => {
            const message: Message = JSON.parse(frame.body);
            this.messageSubject.next(message);
          }
        );
      },
    });

    this.stompClient.activate();
  }

  sendMessage(request: { content: string; conversationId?: number; receiverId?: number }): void {
    if (!this.stompClient?.connected) return;

    this.stompClient.publish({
      destination: '/app/chat.send',
      body: JSON.stringify(request),
    });
  }

  disconnect(): void {
    if (this.stompClient?.active) {
      this.stompClient.deactivate();
    }
    this.stompClient = null;
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
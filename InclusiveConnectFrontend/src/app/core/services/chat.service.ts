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

  // A new Subject is created on every connect() so messages from
  // previous conversations never bleed into the new one.
  private messageSubject = new Subject<Message>();
  messages$ = this.messageSubject.asObservable();

  // Pending message to send as soon as the STOMP connection is established
  private pendingMessage: { content: string; conversationId: number } | null = null;

  // ---- REST ----

  getConversations(): Observable<ApiResponse<Conversation[]>> {
    return this.http.get<ApiResponse<Conversation[]>>(`${this.baseUrl}/conversations`);
  }

  getMessages(conversationId: number): Observable<ApiResponse<Message[]>> {
    return this.http.get<ApiResponse<Message[]>>(
      `${this.baseUrl}/conversations/${conversationId}/messages`
    );
  }

  /**
   * Call the REST endpoint to get (or create) a conversation with another user.
   * This must be called BEFORE connect() when starting a brand-new conversation
   * so the real conversation ID is known before subscribing to WebSocket topics.
   */
  startConversation(receiverId: number): Observable<ApiResponse<Conversation>> {
    return this.http.post<ApiResponse<Conversation>>(
      `${this.baseUrl}/conversations/start`,
      { receiverId }
    );
  }

  // ---- WebSocket: real-time ----

  /**
   * Connect to the STOMP broker and subscribe to the given conversation topic.
   * Creates a fresh Subject so old messages don't bleed in.
   */
  connect(conversationId: number): void {
    // Disconnect any previous session first
    this.disconnect();

    const token = localStorage.getItem('ic_access_token');
    if (!token) return;

    // Fresh observable for this conversation
    this.messageSubject = new Subject<Message>();
    this.messages$ = this.messageSubject.asObservable();

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

        // Send any message that arrived before the connection was ready
        if (this.pendingMessage) {
          this._publish(this.pendingMessage.content, this.pendingMessage.conversationId);
          this.pendingMessage = null;
        }
      },
      onStompError: (frame) => {
        console.error('STOMP error', frame);
      },
    });

    this.stompClient.activate();
  }

  /**
   * Send a message for an existing conversation.
   * If the WebSocket isn't connected yet (first message in session),
   * the message is queued and sent immediately when onConnect fires.
   */
  sendMessage(content: string, conversationId: number): void {
    if (this.stompClient?.connected) {
      this._publish(content, conversationId);
    } else {
      // Queue — will be flushed in onConnect
      this.pendingMessage = { content, conversationId };
    }
  }

  private _publish(content: string, conversationId: number): void {
    this.stompClient?.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({ content, conversationId }),
    });
  }

  disconnect(): void {
    if (this.stompClient?.active) {
      this.stompClient.deactivate();
    }
    this.stompClient = null;
    this.pendingMessage = null;
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
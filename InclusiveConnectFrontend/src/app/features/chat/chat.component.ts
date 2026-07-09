import {
  Component, OnInit, OnDestroy, inject, ElementRef, ViewChild
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ChatService } from '../../core/services/chat.service';
import { AuthService } from '../../core/services/auth.service';
import { Conversation, Message } from '../../core/models/chat.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'ic-chat',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './chat.component.html',
})
export class ChatComponent implements OnInit, OnDestroy {
  private chatService = inject(ChatService);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private fb = new FormBuilder();

  @ViewChild('messageContainer') private messageContainer!: ElementRef;

  conversations: Conversation[] = [];
  activeConversation: Conversation | null = null;
  messages: Message[] = [];
  loadingConversations = true;
  loadingMessages = false;
  starting = false; // true while REST /conversations/start is in-flight

  currentUserId: number | null = null;

  messageForm = this.fb.group({
    content: ['', Validators.required],
  });

  sending = false;
  private messageSubscription: Subscription | null = null;

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadConversations();

    // Support ?userId=X&name=Y query params for "Message" buttons elsewhere
    const userId = this.route.snapshot.queryParamMap.get('userId');
    const name = this.route.snapshot.queryParamMap.get('name');
    if (userId && name) {
      this.startConversationWith(Number(userId), name);
    }
  }

  private loadCurrentUser(): void {
    this.authService.me().subscribe({
      next: (res) => {
        this.currentUserId = res.data?.id ?? null;
      },
    });
  }

  loadConversations(): void {
    this.loadingConversations = true;
    this.chatService.getConversations().subscribe({
      next: (res) => {
        this.conversations = res.data ?? [];
        this.loadingConversations = false;
      },
      error: () => {
        this.loadingConversations = false;
      },
    });
  }

  openConversation(conversation: Conversation): void {
    this.teardownActiveConversation();

    this.activeConversation = conversation;
    this.messages = [];
    this.loadingMessages = true;

    this.chatService.getMessages(conversation.id).subscribe({
      next: (res) => {
        this.messages = res.data ?? [];
        this.loadingMessages = false;
        this.scrollToBottom();
      },
      error: () => {
        this.loadingMessages = false;
      },
    });

    // Connect AFTER we know the real conversation ID
    this.chatService.connect(conversation.id);
    this.subscribeToMessages();
  }

  /**
   * Navigate from outside (e.g. Network page) to open / create a conversation
   * with a specific user. Calls the REST endpoint first to get a real conversation
   * ID before connecting to WebSocket — fixes the id=0 topic bug.
   */
  startConversationWith(receiverId: number, receiverName: string): void {
    this.teardownActiveConversation();

    // Show a loading state while the conversation is being created
    this.starting = true;
    this.messages = [];

    this.chatService.startConversation(receiverId).subscribe({
      next: (res) => {
        const conv = res.data!;
        this.activeConversation = conv;
        this.starting = false;

        // Load existing messages if any
        this.loadingMessages = true;
        this.chatService.getMessages(conv.id).subscribe({
          next: (msgRes) => {
            this.messages = msgRes.data ?? [];
            this.loadingMessages = false;
            this.scrollToBottom();
          },
          error: () => {
            this.loadingMessages = false;
          },
        });

        // Connect only after we have the real conversation ID
        this.chatService.connect(conv.id);
        this.subscribeToMessages();

        // Add to conversations list if not already present
        if (!this.conversations.find((c) => c.id === conv.id)) {
          this.conversations = [conv, ...this.conversations];
        }
      },
      error: () => {
        this.starting = false;
      },
    });
  }

  sendMessage(): void {
    if (this.messageForm.invalid || !this.activeConversation || this.starting) return;

    const { content } = this.messageForm.getRawValue();
    if (!content) return;

    this.sending = true;

    // Always sends with a real conversation ID — the id=0 scenario is now resolved
    // by startConversationWith() before the user can type.
    this.chatService.sendMessage(content, this.activeConversation.id);

    this.messageForm.reset();
    this.sending = false;
  }

  private subscribeToMessages(): void {
    // chatService.messages$ is a fresh observable for this conversation
    this.messageSubscription = this.chatService.messages$.subscribe((message) => {
      this.messages.push(message);
      this.scrollToBottom();

      // Update conversation list preview
      const conv = this.conversations.find((c) => c.id === message.conversationId);
      if (conv) {
        conv.lastMessagePreview = message.content;
        conv.lastMessageAt = message.sentAt;
      }
    });
  }

  private teardownActiveConversation(): void {
    this.chatService.disconnect();
    this.messageSubscription?.unsubscribe();
    this.messageSubscription = null;
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      if (this.messageContainer) {
        this.messageContainer.nativeElement.scrollTop =
          this.messageContainer.nativeElement.scrollHeight;
      }
    }, 50);
  }

  isMyMessage(message: Message): boolean {
    return message.senderId === this.currentUserId;
  }

  ngOnDestroy(): void {
    this.teardownActiveConversation();
  }
}
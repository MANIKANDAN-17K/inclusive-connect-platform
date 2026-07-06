import { Component, OnInit, OnDestroy, inject, ElementRef, ViewChild } from '@angular/core';
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
  private fb = new FormBuilder();

  @ViewChild('messageContainer') private messageContainer!: ElementRef;

  conversations: Conversation[] = [];
  activeConversation: Conversation | null = null;
  messages: Message[] = [];
  loadingConversations = true;
  loadingMessages = false;

  currentUserId: number | null = null;

  messageForm = this.fb.group({
    content: ['', Validators.required],
  });

  sending = false;
  private messageSubscription: Subscription | null = null;
  private route = inject(ActivatedRoute);
 ngOnInit(): void {
  this.loadCurrentUser();
  this.loadConversations();

  // Support ?userId=X&name=Y query params for "Message this person" buttons elsewhere
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
    // Disconnect from previous conversation's topic if any
    this.chatService.disconnect();
    this.messageSubscription?.unsubscribe();

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

    // Subscribe to real-time messages for this conversation
    this.chatService.connect(conversation.id);
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

  sendMessage(): void {
    if (this.messageForm.invalid || !this.activeConversation) return;

    const { content } = this.messageForm.getRawValue();
    this.sending = true;

    this.chatService.sendMessage({
      content: content!,
      conversationId: this.activeConversation.id,
    });

    this.messageForm.reset();
    this.sending = false;
  }
  startConversationWith(receiverId: number, receiverName: string): void {
  this.chatService.disconnect();
  this.messageSubscription?.unsubscribe();

  // Create a placeholder conversation in the UI while we wait for the first real message
  this.activeConversation = {
    id: 0, // placeholder — real ID assigned by backend after first message
    createdAt: new Date().toISOString(),
    otherUserId: receiverId,
    otherUserName: receiverName,
  };
  this.messages = [];

  // Connect to a temporary topic — backend will broadcast with the real conversationId
  // after the first message is saved
  this.chatService.connect(0);
  this.messageSubscription = this.chatService.messages$.subscribe((message) => {
    // Update the placeholder conversation with the real ID once we get the first message back
    if (this.activeConversation?.id === 0) {
      this.activeConversation = { ...this.activeConversation, id: message.conversationId };
      // Re-subscribe to the real topic
      this.chatService.disconnect();
      this.chatService.connect(message.conversationId);
    }
    this.messages.push(message);
    this.scrollToBottom();
    this.loadConversations(); // refresh list to show new conversation
  });
}

sendMessageToNew(receiverId: number): void {
  if (this.messageForm.invalid) return;
  const { content } = this.messageForm.getRawValue();

  this.chatService.sendMessage({
    content: content!,
    receiverId: receiverId,
  });

  this.messageForm.reset();
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
    this.chatService.disconnect();
    this.messageSubscription?.unsubscribe();
  }
}
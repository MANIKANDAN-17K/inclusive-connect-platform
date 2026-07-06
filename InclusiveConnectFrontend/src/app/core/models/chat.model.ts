export interface Conversation {
  id: number;
  createdAt: string;
  otherUserId: number;
  otherUserName: string;
  otherUserProfilePicture?: string;
  lastMessagePreview?: string;
  lastMessageAt?: string;
}

export interface Message {
  id: number;
  conversationId: number;
  content: string;
  isRead: boolean;
  sentAt: string;
  senderId: number;
  senderName: string;
}
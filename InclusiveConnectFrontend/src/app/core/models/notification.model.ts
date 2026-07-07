export type NotificationType = 'CONNECTION_REQUEST' | 'CONNECTION_ACCEPTED' | 'JOB_APPLICATION' | 'MESSAGE' | 'SYSTEM';

export interface AppNotification {
  id: number;
  title: string;
  message: string;
  type: NotificationType;
  isRead: boolean;
  linkUrl?: string;
  createdAt: string;
}
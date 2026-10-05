export type NotificationType = 'TASK_ASSIGNED' | 'TASK_STATUS_CHANGED' | 'COMMENT_ADDED';

export interface AppNotification {
  id: string;
  type: NotificationType;
  title: string;
  message: string | null;
  link: string | null;
  isRead: boolean;
  createdAt: string;
}

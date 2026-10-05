import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AppNotification } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly http = inject(HttpClient);

  readonly unreadCount = signal(0);

  refreshUnreadCount(): void {
    this.http
      .get<{ count: number }>(`${environment.apiUrl}/notifications/unread-count`)
      .subscribe((res) => this.unreadCount.set(res.count));
  }

  getAll() {
    return this.http.get<AppNotification[]>(`${environment.apiUrl}/notifications`);
  }

  markAsRead(id: string) {
    return this.http.patch<void>(`${environment.apiUrl}/notifications/${id}/read`, {});
  }
}

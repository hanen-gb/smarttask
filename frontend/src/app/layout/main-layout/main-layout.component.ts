import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { ThemeService } from '../../core/services/theme.service';
import { AppNotification } from '../../core/models/notification.model';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss',
})
export class MainLayoutComponent {
  readonly authService = inject(AuthService);
  readonly notificationService = inject(NotificationService);
  readonly themeService = inject(ThemeService);
  private readonly router = inject(Router);

  readonly showPanel = signal(false);
  readonly notifications = signal<AppNotification[]>([]);

  constructor() {
    this.notificationService.refreshUnreadCount();
  }

  togglePanel(): void {
    this.showPanel.update((v) => !v);
    if (this.showPanel()) {
      this.notificationService.getAll().subscribe((list) => this.notifications.set(list));
    }
  }

  openNotification(notification: AppNotification): void {
    this.notificationService.markAsRead(notification.id).subscribe(() => {
      this.notificationService.refreshUnreadCount();
      this.notifications.update((list) =>
        list.map((n) => (n.id === notification.id ? { ...n, isRead: true } : n)),
      );
    });
    this.showPanel.set(false);
    if (notification.link) {
      this.router.navigateByUrl(notification.link);
    }
  }

  logout(): void {
    this.authService.logout();
  }
}

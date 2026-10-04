import { Component, inject, signal } from '@angular/core';
import { UserService } from '../../../core/services/user.service';
import { UserResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss',
})
export class UserListComponent {
  private readonly userService = inject(UserService);

  readonly users = signal<UserResponse[]>([]);
  readonly loading = signal(true);

  constructor() {
    this.userService.getAll().subscribe((users) => {
      this.users.set(users);
      this.loading.set(false);
    });
  }

  toggleStatus(user: UserResponse): void {
    this.userService.updateStatus(user.id, !user.enabled).subscribe((updated) => {
      this.users.update((list) => list.map((u) => (u.id === updated.id ? updated : u)));
    });
  }
}

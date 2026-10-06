import { Component, inject, signal } from '@angular/core';
import { UserService } from '../../../core/services/user.service';
import { Role, UserResponse } from '../../../core/models/user.model';

const ROLES: Role[] = ['ADMIN', 'MANAGER', 'MEMBER'];

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
  readonly roles = ROLES;

  // userId -> role en attente de confirmation (pas encore envoyee au serveur)
  readonly pendingRole = signal<Record<string, Role>>({});

  constructor() {
    this.userService.getAll().subscribe((users) => {
      this.users.set(users);
      this.loading.set(false);
    });
  }

  initials(user: UserResponse): string {
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  }

  roleFor(user: UserResponse): Role {
    return this.pendingRole()[user.id] ?? user.role;
  }

  hasPendingRole(user: UserResponse): boolean {
    const pending = this.pendingRole()[user.id];
    return pending !== undefined && pending !== user.role;
  }

  stageRole(user: UserResponse, role: Role): void {
    this.pendingRole.update((map) => ({ ...map, [user.id]: role }));
  }

  cancelRole(user: UserResponse): void {
    this.pendingRole.update((map) => {
      const { [user.id]: _removed, ...rest } = map;
      return rest;
    });
  }

  confirmRole(user: UserResponse): void {
    const role = this.pendingRole()[user.id];
    if (!role || role === user.role) {
      this.cancelRole(user);
      return;
    }

    this.userService.updateRole(user.id, role).subscribe((updated) => {
      this.users.update((list) => list.map((u) => (u.id === updated.id ? updated : u)));
      this.cancelRole(user);
    });
  }

  toggleStatus(user: UserResponse): void {
    const action = user.enabled ? 'désactiver' : 'réactiver';
    if (!confirm(`Confirmer : ${action} le compte de ${user.firstName} ${user.lastName} ?`)) return;

    this.userService.updateStatus(user.id, !user.enabled).subscribe((updated) => {
      this.users.update((list) => list.map((u) => (u.id === updated.id ? updated : u)));
    });
  }
}

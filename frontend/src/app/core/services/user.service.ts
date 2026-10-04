import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { UserResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);

  getAll() {
    return this.http.get<UserResponse[]>(`${environment.apiUrl}/users`);
  }

  updateRole(id: string, role: string) {
    return this.http.put<UserResponse>(`${environment.apiUrl}/users/${id}/role`, { role });
  }

  updateStatus(id: string, enabled: boolean) {
    return this.http.put<UserResponse>(
      `${environment.apiUrl}/users/${id}/status?enabled=${enabled}`,
      {},
    );
  }
}

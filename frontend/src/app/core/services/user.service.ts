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

  getById(id: string) {
    return this.http.get<UserResponse>(`${environment.apiUrl}/users/${id}`);
  }

  updateProfile(firstName: string, lastName: string) {
    return this.http.put<UserResponse>(`${environment.apiUrl}/users/me`, { firstName, lastName });
  }

  uploadAvatar(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<UserResponse>(`${environment.apiUrl}/users/me/avatar`, formData);
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

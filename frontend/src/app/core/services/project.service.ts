import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Project, ProjectMember, ProjectRequest, UpdateProjectRequest } from '../models/project.model';

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private readonly http = inject(HttpClient);

  getMyProjects() {
    return this.http.get<Project[]>(`${environment.apiUrl}/projects`);
  }

  create(request: ProjectRequest) {
    return this.http.post<Project>(`${environment.apiUrl}/projects`, request);
  }

  getById(id: string) {
    return this.http.get<Project>(`${environment.apiUrl}/projects/${id}`);
  }

  update(id: string, request: UpdateProjectRequest) {
    return this.http.put<Project>(`${environment.apiUrl}/projects/${id}`, request);
  }

  delete(id: string) {
    return this.http.delete<void>(`${environment.apiUrl}/projects/${id}`);
  }

  getMembers(id: string) {
    return this.http.get<ProjectMember[]>(`${environment.apiUrl}/projects/${id}/members`);
  }

  addMember(id: string, userId: string) {
    return this.http.post<void>(`${environment.apiUrl}/projects/${id}/members`, { userId });
  }

  removeMember(id: string, userId: string) {
    return this.http.delete<void>(`${environment.apiUrl}/projects/${id}/members/${userId}`);
  }
}

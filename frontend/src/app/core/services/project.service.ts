import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Project, ProjectRequest } from '../models/project.model';

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

  delete(id: string) {
    return this.http.delete<void>(`${environment.apiUrl}/projects/${id}`);
  }
}

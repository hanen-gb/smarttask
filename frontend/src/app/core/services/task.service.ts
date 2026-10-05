import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { PageResponse, Task, TaskRequest, TaskStatus, UpdateTaskRequest } from '../models/task.model';

export interface TaskSearchFilters {
  search?: string;
  status?: TaskStatus[];
  projectId?: string;
  page?: number;
  size?: number;
}

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);

  getBoard(projectId: string) {
    return this.http.get<Record<TaskStatus, Task[]>>(
      `${environment.apiUrl}/projects/${projectId}/board`,
    );
  }

  create(request: TaskRequest) {
    return this.http.post<Task>(`${environment.apiUrl}/tasks`, request);
  }

  getById(taskId: string) {
    return this.http.get<Task>(`${environment.apiUrl}/tasks/${taskId}`);
  }

  update(taskId: string, request: UpdateTaskRequest) {
    return this.http.put<Task>(`${environment.apiUrl}/tasks/${taskId}`, request);
  }

  updateStatus(taskId: string, status: TaskStatus) {
    return this.http.patch<Task>(`${environment.apiUrl}/tasks/${taskId}/status`, { status });
  }

  updateAssignee(taskId: string, assigneeId: string | null) {
    return this.http.patch<Task>(`${environment.apiUrl}/tasks/${taskId}/assignee`, { assigneeId });
  }

  delete(taskId: string) {
    return this.http.delete<void>(`${environment.apiUrl}/tasks/${taskId}`);
  }

  search(filters: TaskSearchFilters) {
    let params = new HttpParams();
    if (filters.search) params = params.set('search', filters.search);
    if (filters.projectId) params = params.set('projectId', filters.projectId);
    if (filters.status?.length) {
      filters.status.forEach((s) => (params = params.append('status', s)));
    }
    params = params.set('page', filters.page ?? 0);
    params = params.set('size', filters.size ?? 10);

    return this.http.get<PageResponse<Task>>(`${environment.apiUrl}/tasks`, { params });
  }
}

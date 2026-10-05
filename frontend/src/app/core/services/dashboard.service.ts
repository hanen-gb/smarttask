import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { DashboardSummary, TasksByPriority, TasksByStatus } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);

  getSummary() {
    return this.http.get<DashboardSummary>(`${environment.apiUrl}/dashboard/summary`);
  }

  getTasksByStatus() {
    return this.http.get<TasksByStatus>(`${environment.apiUrl}/dashboard/tasks-by-status`);
  }

  getTasksByPriority() {
    return this.http.get<TasksByPriority>(`${environment.apiUrl}/dashboard/tasks-by-priority`);
  }
}

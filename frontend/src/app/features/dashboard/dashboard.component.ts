import { Component, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardSummary, TasksByPriority, TasksByStatus } from '../../core/models/dashboard.model';
import { TaskPriority, TaskStatus } from '../../core/models/task.model';

const STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: 'À faire',
  IN_PROGRESS: 'En cours',
  IN_REVIEW: 'En relecture',
  DONE: 'Terminé',
  CANCELLED: 'Annulé',
};

const PRIORITY_LABELS: Record<TaskPriority, string> = {
  LOW: 'Basse',
  MEDIUM: 'Moyenne',
  HIGH: 'Haute',
  URGENT: 'Urgente',
};

interface BarItem {
  label: string;
  count: number;
  percent: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  private readonly dashboardService = inject(DashboardService);

  readonly loading = signal(true);
  readonly summary = signal<DashboardSummary | null>(null);
  private readonly byStatus = signal<TasksByStatus | null>(null);
  private readonly byPriority = signal<TasksByPriority | null>(null);

  readonly statusBars = computed(() => this.toBars(this.byStatus(), STATUS_LABELS));
  readonly priorityBars = computed(() => this.toBars(this.byPriority(), PRIORITY_LABELS));

  constructor() {
    forkJoin({
      summary: this.dashboardService.getSummary(),
      byStatus: this.dashboardService.getTasksByStatus(),
      byPriority: this.dashboardService.getTasksByPriority(),
    }).subscribe(({ summary, byStatus, byPriority }) => {
      this.summary.set(summary);
      this.byStatus.set(byStatus);
      this.byPriority.set(byPriority);
      this.loading.set(false);
    });
  }

  private toBars<T extends string>(
    data: Record<T, number> | null,
    labels: Record<T, string>,
  ): BarItem[] {
    if (!data) return [];

    const entries = Object.entries(data) as [T, number][];
    const max = Math.max(1, ...entries.map(([, count]) => count));

    return entries.map(([key, count]) => ({
      label: labels[key],
      count,
      percent: Math.round((count / max) * 100),
    }));
  }
}

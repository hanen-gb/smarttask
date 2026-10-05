import { TaskPriority, TaskStatus } from './task.model';

export interface DashboardSummary {
  totalProjects: number;
  activeProjects: number;
  totalTasks: number;
  myOpenTasks: number;
  myOverdueTasks: number;
  tasksCompletedThisWeek: number;
  completionRate: number;
}

export type TasksByStatus = Record<TaskStatus, number>;
export type TasksByPriority = Record<TaskPriority, number>;

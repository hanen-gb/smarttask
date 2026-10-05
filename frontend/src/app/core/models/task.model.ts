export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'IN_REVIEW' | 'DONE' | 'CANCELLED';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface TaskUserRef {
  id: string;
  firstName: string;
  lastName: string;
}

/** Miroir de com.smarttask.backend.task.dto.TaskResponse (backend) */
export interface Task {
  id: string;
  title: string;
  description: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate: string | null;
  position: number;
  projectId: string;
  projectName: string;
  assignee: TaskUserRef | null;
  reporter: TaskUserRef;
  completedAt: string | null;
  createdAt: string;
}

export interface TaskRequest {
  title: string;
  description?: string;
  projectId: string;
  assigneeId?: string;
  priority?: TaskPriority;
  dueDate?: string;
}

/** Miroir de com.smarttask.backend.task.dto.UpdateTaskRequest (backend) */
export interface UpdateTaskRequest {
  title: string;
  description?: string;
  priority?: TaskPriority;
  dueDate?: string;
}

export const TASK_PRIORITIES: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

/** Miroir de com.smarttask.backend.common.dto.PageResponse (backend) -- generique */
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export const TASK_STATUSES: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE', 'CANCELLED'];

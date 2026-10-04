export type ProjectStatus = 'PLANNING' | 'ACTIVE' | 'ON_HOLD' | 'COMPLETED' | 'ARCHIVED';

export interface ProjectOwnerSummary {
  id: string;
  firstName: string;
  lastName: string;
}

/** Miroir de com.smarttask.backend.project.dto.ProjectResponse (backend) */
export interface Project {
  id: string;
  name: string;
  description: string | null;
  status: ProjectStatus;
  color: string | null;
  startDate: string | null;
  endDate: string | null;
  owner: ProjectOwnerSummary;
  memberCount: number;
  createdAt: string;
}

export interface ProjectRequest {
  name: string;
  description?: string;
  color?: string;
  startDate?: string;
  endDate?: string;
}

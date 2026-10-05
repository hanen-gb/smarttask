export type ProjectStatus = 'PLANNING' | 'ACTIVE' | 'ON_HOLD' | 'COMPLETED' | 'ARCHIVED';
export type ProjectRole = 'OWNER' | 'MANAGER' | 'CONTRIBUTOR' | 'VIEWER';

export const PROJECT_STATUSES: ProjectStatus[] = ['PLANNING', 'ACTIVE', 'ON_HOLD', 'COMPLETED', 'ARCHIVED'];

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

/** Miroir de com.smarttask.backend.project.dto.UpdateProjectRequest (backend) */
export interface UpdateProjectRequest {
  name: string;
  description?: string;
  status?: ProjectStatus;
  color?: string;
  startDate?: string;
  endDate?: string;
}

/** Miroir de com.smarttask.backend.project.dto.ProjectMemberResponse (backend) */
export interface ProjectMember {
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  projectRole: ProjectRole;
  enabled: boolean;
}

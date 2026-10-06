export type Role = 'ADMIN' | 'MANAGER' | 'MEMBER';

export interface UserSummary {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
}

/** Miroir de com.smarttask.backend.user.dto.UserResponse (backend) */
export interface UserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  enabled: boolean;
  avatarUrl: string | null;
}

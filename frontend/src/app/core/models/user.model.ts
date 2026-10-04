export type Role = 'ADMIN' | 'MANAGER' | 'MEMBER';

export interface UserSummary {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
}

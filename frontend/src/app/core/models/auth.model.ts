import { UserSummary } from './user.model';

/** Miroir exact de com.smarttask.backend.auth.dto.RegisterRequest (backend) */
export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

/** Miroir exact de com.smarttask.backend.auth.dto.LoginRequest (backend) */
export interface LoginRequest {
  email: string;
  password: string;
}

/** Miroir exact de com.smarttask.backend.auth.dto.AuthResponse (backend) */
export interface AuthResponse {
  accessToken: string;
  refreshToken: string | null;
  tokenType: string;
  expiresIn: number;
  user: UserSummary;
}

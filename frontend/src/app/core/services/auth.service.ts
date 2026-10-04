import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { UserSummary } from '../models/user.model';
import { TokenService } from './token.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // signal = l'etat reactif central : "qui est connecte, maintenant ?"
  // Tout composant qui lit currentUser() se met a jour automatiquement des qu'il change.
  private readonly currentUserSignal = signal<UserSummary | null>(null);

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.currentUserSignal() !== null);

  constructor(
    private readonly http: HttpClient,
    private readonly tokenService: TokenService,
    private readonly router: Router,
  ) {
    // Au demarrage de l'app (ou apres un F5), si un token existe deja,
    // on redemande au backend "qui suis-je ?" pour repeupler currentUser.
    if (this.tokenService.getToken()) {
      this.fetchCurrentUser();
    }
  }

  private fetchCurrentUser(): void {
    this.http.get<UserSummary>(`${environment.apiUrl}/auth/me`).subscribe({
      next: (user) => this.currentUserSignal.set(user),
      // token invalide/expire : on nettoie proprement plutot que de laisser
      // l'app dans un etat incoherent (token present mais inutilisable)
      error: () => this.logout(),
    });
  }

  register(request: RegisterRequest) {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/register`, request)
      .pipe(tap((response) => this.handleAuthSuccess(response)));
  }

  login(request: LoginRequest) {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, request)
      .pipe(tap((response) => this.handleAuthSuccess(response)));
  }

  logout(): void {
    this.tokenService.clearToken();
    this.currentUserSignal.set(null);
    this.router.navigateByUrl('/login');
  }

  private handleAuthSuccess(response: AuthResponse): void {
    this.tokenService.setToken(response.accessToken);
    this.currentUserSignal.set(response.user);
  }
}

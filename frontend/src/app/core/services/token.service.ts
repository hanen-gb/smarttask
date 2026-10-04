import { Injectable } from '@angular/core';

const TOKEN_KEY = 'smarttask_access_token';

/**
 * Seul endroit de l'application qui touche au localStorage pour le token.
 * Si on change un jour de strategie de stockage, on ne modifie que ce fichier.
 */
@Injectable({ providedIn: 'root' })
export class TokenService {
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  }

  clearToken(): void {
    localStorage.removeItem(TOKEN_KEY);
  }
}

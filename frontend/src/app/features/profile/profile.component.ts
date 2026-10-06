import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { ThemeService } from '../../core/services/theme.service';
import { UserResponse } from '../../core/models/user.model';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent {
  private readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);
  readonly themeService = inject(ThemeService);
  private readonly fb = inject(FormBuilder);

  readonly profile = signal<UserResponse | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly saved = signal(false);
  readonly uploadError = signal<string | null>(null);
  // Invalide le cache navigateur de l'image apres un televersement (meme URL, nouveau contenu)
  readonly avatarCacheBuster = signal(Date.now());

  readonly form = this.fb.group({
    firstName: ['', [Validators.required, Validators.minLength(2)]],
    lastName: ['', [Validators.required, Validators.minLength(2)]],
  });

  constructor() {
    const userId = this.authService.currentUser()?.id;
    if (userId) {
      this.userService.getById(userId).subscribe((user) => {
        this.profile.set(user);
        this.form.patchValue({ firstName: user.firstName, lastName: user.lastName });
      });
    }
  }

  avatarSrc(profile: UserResponse): string | null {
    return profile.avatarUrl ? `${environment.apiUrl}${profile.avatarUrl}?t=${this.avatarCacheBuster()}` : null;
  }

  initials(profile: UserResponse): string {
    return `${profile.firstName.charAt(0)}${profile.lastName.charAt(0)}`.toUpperCase();
  }

  saveProfile(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.userService.updateProfile(value.firstName!, value.lastName!).subscribe({
      next: (updated) => {
        this.profile.set(updated);
        this.authService.refreshCurrentUser();
        this.saved.set(true);
        setTimeout(() => this.saved.set(false), 2000);
      },
      error: () => this.errorMessage.set('Échec de la mise à jour du profil.'),
    });
  }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploadError.set(null);
    this.userService.uploadAvatar(file).subscribe({
      next: (updated) => {
        this.profile.set(updated);
        this.avatarCacheBuster.set(Date.now());
        input.value = '';
      },
      error: (err) => {
        this.uploadError.set(
          err.status === 415 ? 'Le fichier doit être une image (JPG, PNG...).' : "Échec de l'envoi de la photo.",
        );
        input.value = '';
      },
    });
  }
}

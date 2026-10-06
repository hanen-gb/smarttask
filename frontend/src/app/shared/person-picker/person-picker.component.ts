import { Component, computed, input, output, signal } from '@angular/core';

export interface PickablePerson {
  id: string;
  firstName: string;
  lastName: string;
  email?: string;
}

/**
 * Champ de recherche "tape le nom -> suggestions" reutilisable partout ou on
 * doit choisir une personne (membre de projet, assignation de tache, etc.)
 * a la place d'un <select> classique.
 */
@Component({
  selector: 'app-person-picker',
  standalone: true,
  imports: [],
  templateUrl: './person-picker.component.html',
  styleUrl: './person-picker.component.scss',
})
export class PersonPickerComponent {
  readonly people = input.required<PickablePerson[]>();
  readonly placeholder = input('Tape un nom...');

  readonly picked = output<PickablePerson>();

  readonly query = signal('');
  readonly open = signal(false);

  readonly results = computed(() => {
    const q = this.query().trim().toLowerCase();
    const pool = this.people();
    if (!q) return pool.slice(0, 6);
    return pool
      .filter((p) => `${p.firstName} ${p.lastName}`.toLowerCase().includes(q) || p.email?.toLowerCase().includes(q))
      .slice(0, 6);
  });

  onInput(value: string): void {
    this.query.set(value);
    this.open.set(true);
  }

  select(person: PickablePerson): void {
    this.picked.emit(person);
    this.query.set('');
    this.open.set(false);
  }

  onBlur(): void {
    // Delai pour laisser le (click) sur une suggestion se declencher avant fermeture
    setTimeout(() => this.open.set(false), 150);
  }

  initials(p: PickablePerson): string {
    return `${p.firstName.charAt(0)}${p.lastName.charAt(0)}`.toUpperCase();
  }
}

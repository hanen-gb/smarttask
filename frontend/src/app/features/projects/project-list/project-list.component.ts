import { Component, computed, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { ProjectService } from '../../../core/services/project.service';
import { UserService } from '../../../core/services/user.service';
import { Project } from '../../../core/models/project.model';
import { PersonPickerComponent, PickablePerson } from '../../../shared/person-picker/person-picker.component';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, PersonPickerComponent],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.scss',
})
export class ProjectListComponent {
  private readonly fb = inject(FormBuilder);
  private readonly projectService = inject(ProjectService);
  private readonly userService = inject(UserService);

  readonly projects = signal<Project[]>([]);
  readonly loading = signal(true);
  readonly showForm = signal(false);
  readonly creating = signal(false);

  readonly allUsers = signal<PickablePerson[]>([]);
  readonly selectedMembers = signal<PickablePerson[]>([]);

  readonly availableToPick = computed(() => {
    const chosenIds = new Set(this.selectedMembers().map((p) => p.id));
    return this.allUsers().filter((u) => !chosenIds.has(u.id));
  });

  readonly form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
  });

  constructor() {
    this.loadProjects();
    this.userService.getAll().subscribe((users) => this.allUsers.set(users));
  }

  private loadProjects(): void {
    this.loading.set(true);
    this.projectService.getMyProjects().subscribe((projects) => {
      this.projects.set(projects);
      this.loading.set(false);
    });
  }

  addMember(person: PickablePerson): void {
    this.selectedMembers.update((list) => [...list, person]);
  }

  removeMember(person: PickablePerson): void {
    this.selectedMembers.update((list) => list.filter((p) => p.id !== person.id));
  }

  initials(p: PickablePerson): string {
    return `${p.firstName.charAt(0)}${p.lastName.charAt(0)}`.toUpperCase();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.creating.set(true);
    this.projectService
      .create(this.form.getRawValue() as { name: string; description: string })
      .subscribe((project) => {
        const members = this.selectedMembers();
        const addCalls = members.map((m) => this.projectService.addMember(project.id, m.id));
        const after$ = addCalls.length > 0 ? forkJoin(addCalls) : of([]);
        after$.subscribe(() => {
          this.form.reset();
          this.selectedMembers.set([]);
          this.showForm.set(false);
          this.creating.set(false);
          this.loadProjects();
        });
      });
  }
}

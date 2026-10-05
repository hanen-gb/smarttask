import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ProjectService } from '../../../core/services/project.service';
import { UserService } from '../../../core/services/user.service';
import { Project, ProjectMember, PROJECT_STATUSES, ProjectStatus } from '../../../core/models/project.model';
import { UserResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './project-detail.component.html',
  styleUrl: './project-detail.component.scss',
})
export class ProjectDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly projectService = inject(ProjectService);
  private readonly userService = inject(UserService);
  private readonly fb = inject(FormBuilder);

  readonly projectId = this.route.snapshot.paramMap.get('id')!;

  readonly project = signal<Project | null>(null);
  readonly members = signal<ProjectMember[]>([]);
  readonly allUsers = signal<UserResponse[]>([]);
  readonly errorMessage = signal<string | null>(null);
  readonly memberError = signal<string | null>(null);
  readonly saved = signal(false);
  readonly selectedUserId = signal('');
  readonly showAddMember = signal(false);

  readonly statuses = PROJECT_STATUSES;

  // Les utilisateurs pas encore membres -- seuls candidats pour "+ Ajouter"
  readonly availableUsers = computed(() => {
    const memberIds = new Set(this.members().map((m) => m.userId));
    return this.allUsers().filter((u) => !memberIds.has(u.id));
  });

  readonly editForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    status: this.fb.control<ProjectStatus>('PLANNING'),
    startDate: [''],
    endDate: [''],
  });

  constructor() {
    this.loadProject();
    this.loadMembers();
    this.userService.getAll().subscribe((users) => this.allUsers.set(users));
  }

  private loadProject(): void {
    this.projectService.getById(this.projectId).subscribe({
      next: (project) => {
        this.project.set(project);
        this.editForm.patchValue({
          name: project.name,
          description: project.description ?? '',
          status: project.status,
          startDate: project.startDate ?? '',
          endDate: project.endDate ?? '',
        });
      },
      error: () => this.errorMessage.set('Projet introuvable ou accès refusé.'),
    });
  }

  private loadMembers(): void {
    this.projectService.getMembers(this.projectId).subscribe((members) => this.members.set(members));
  }

  saveProject(): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    const value = this.editForm.getRawValue();
    this.projectService
      .update(this.projectId, {
        name: value.name!,
        description: value.description || undefined,
        status: value.status!,
        startDate: value.startDate || undefined,
        endDate: value.endDate || undefined,
      })
      .subscribe({
        next: (project) => {
          this.project.set(project);
          this.saved.set(true);
          setTimeout(() => this.saved.set(false), 2000);
        },
        error: () =>
          this.errorMessage.set(
            "Seul le propriétaire/manager de ce projet peut le modifier.",
          ),
      });
  }

  addMember(): void {
    const userId = this.selectedUserId();
    if (!userId) return;

    this.memberError.set(null);
    this.projectService.addMember(this.projectId, userId).subscribe({
      next: () => {
        this.selectedUserId.set('');
        this.showAddMember.set(false);
        this.loadMembers();
        this.loadProject(); // memberCount a change
      },
      error: (err) =>
        this.memberError.set(
          err.status === 409
            ? 'Cette personne est déjà membre.'
            : "Vous n'avez pas le droit d'ajouter des membres à ce projet.",
        ),
    });
  }

  removeMember(member: ProjectMember): void {
    if (!confirm(`Retirer ${member.firstName} ${member.lastName} du projet ?`)) return;

    this.projectService.removeMember(this.projectId, member.userId).subscribe({
      next: () => {
        this.loadMembers();
        this.loadProject();
      },
      error: (err) =>
        this.memberError.set(
          err.status === 409 ? 'Impossible de retirer le propriétaire.' : 'Suppression refusée.',
        ),
    });
  }

  initials(firstName: string, lastName: string): string {
    return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
  }

  deleteProject(): void {
    const project = this.project();
    if (!project) return;

    if (!confirm(`Supprimer "${project.name}" ? Cette action est irréversible.`)) {
      return;
    }

    this.projectService.delete(project.id).subscribe({
      next: () => this.router.navigateByUrl('/projects'),
      error: () => this.errorMessage.set('Suppression refusée (seul le propriétaire ou un admin peut supprimer).'),
    });
  }
}

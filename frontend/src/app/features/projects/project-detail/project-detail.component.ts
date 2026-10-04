import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProjectService } from '../../../core/services/project.service';
import { Project } from '../../../core/models/project.model';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './project-detail.component.html',
  styleUrl: './project-detail.component.scss',
})
export class ProjectDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly projectService = inject(ProjectService);

  readonly project = signal<Project | null>(null);
  readonly errorMessage = signal<string | null>(null);

  constructor() {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.projectService.getById(id).subscribe({
      next: (project) => this.project.set(project),
      error: () => this.errorMessage.set("Projet introuvable ou accès refusé."),
    });
  }

  deleteProject(): void {
    const project = this.project();
    if (!project) return;

    if (!confirm(`Supprimer "${project.name}" ? Cette action est irréversible.`)) {
      return;
    }

    this.projectService.delete(project.id).subscribe({
      next: () => this.router.navigateByUrl('/projects'),
      error: () => this.errorMessage.set("Suppression refusée (seul le propriétaire ou un admin peut supprimer)."),
    });
  }
}

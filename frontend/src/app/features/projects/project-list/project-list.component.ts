import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ProjectService } from '../../../core/services/project.service';
import { Project } from '../../../core/models/project.model';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.scss',
})
export class ProjectListComponent {
  private readonly fb = inject(FormBuilder);
  private readonly projectService = inject(ProjectService);

  readonly projects = signal<Project[]>([]);
  readonly loading = signal(true);
  readonly showForm = signal(false);

  readonly form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
  });

  constructor() {
    this.loadProjects();
  }

  private loadProjects(): void {
    this.loading.set(true);
    this.projectService.getMyProjects().subscribe((projects) => {
      this.projects.set(projects);
      this.loading.set(false);
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.projectService
      .create(this.form.getRawValue() as { name: string; description: string })
      .subscribe(() => {
        this.form.reset();
        this.showForm.set(false);
        this.loadProjects();
      });
  }
}

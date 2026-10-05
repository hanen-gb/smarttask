import { Component, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { TaskService } from '../../../core/services/task.service';
import { PageResponse, Task, TaskStatus, TASK_STATUSES } from '../../../core/models/task.model';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss',
})
export class TaskListComponent {
  private readonly taskService = inject(TaskService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly allStatuses = TASK_STATUSES;
  readonly result = signal<PageResponse<Task> | null>(null);
  readonly loading = signal(true);

  readonly searchControl = new FormControl(this.route.snapshot.queryParamMap.get('search') ?? '');
  readonly activeStatuses = signal<TaskStatus[]>(
    (this.route.snapshot.queryParamMap.getAll('status') as TaskStatus[]) ?? [],
  );
  private currentPage = Number(this.route.snapshot.queryParamMap.get('page') ?? 0);

  constructor() {
    // debounceTime(300) : on attend 300ms apres la derniere frappe avant
    // d'envoyer la requete -- evite un appel HTTP a CHAQUE caractere tape.
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => {
        this.currentPage = 0;
        this.search();
      });

    this.search();
  }

  toggleStatus(status: TaskStatus): void {
    const current = this.activeStatuses();
    this.activeStatuses.set(
      current.includes(status) ? current.filter((s) => s !== status) : [...current, status],
    );
    this.currentPage = 0;
    this.search();
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.search();
  }

  private search(): void {
    this.loading.set(true);

    const filters = {
      search: this.searchControl.value ?? undefined,
      status: this.activeStatuses().length ? this.activeStatuses() : undefined,
      page: this.currentPage,
      size: 5,
    };

    // Synchronise l'URL : partageable, et les filtres survivent au rechargement (F5)
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        search: filters.search || null,
        status: filters.status ?? null,
        page: this.currentPage || null,
      },
      queryParamsHandling: 'merge',
    });

    this.taskService.search(filters).subscribe((result) => {
      this.result.set(result);
      this.loading.set(false);
    });
  }
}

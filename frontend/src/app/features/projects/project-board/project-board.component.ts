import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { TaskService } from '../../../core/services/task.service';
import { Task, TaskStatus, TASK_STATUSES } from '../../../core/models/task.model';

const STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: 'À faire',
  IN_PROGRESS: 'En cours',
  IN_REVIEW: 'En relecture',
  DONE: 'Terminé',
  CANCELLED: 'Annulé',
};

@Component({
  selector: 'app-project-board',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './project-board.component.html',
  styleUrl: './project-board.component.scss',
})
export class ProjectBoardComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly taskService = inject(TaskService);
  private readonly fb = inject(FormBuilder);

  readonly projectId = this.route.snapshot.paramMap.get('id')!;
  readonly columns = TASK_STATUSES;
  readonly statusLabel = STATUS_LABELS;

  readonly board = signal<Record<TaskStatus, Task[]>>({
    TODO: [],
    IN_PROGRESS: [],
    IN_REVIEW: [],
    DONE: [],
    CANCELLED: [],
  });
  readonly showForm = signal(false);

  readonly form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(3)]],
  });

  constructor() {
    this.loadBoard();
  }

  private loadBoard(): void {
    this.taskService.getBoard(this.projectId).subscribe((board) => {
      // S'assure que les 5 colonnes existent toujours, meme vides
      this.board.set({
        TODO: board.TODO ?? [],
        IN_PROGRESS: board.IN_PROGRESS ?? [],
        IN_REVIEW: board.IN_REVIEW ?? [],
        DONE: board.DONE ?? [],
        CANCELLED: board.CANCELLED ?? [],
      });
    });
  }

  createTask(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.taskService
      .create({ title: this.form.value.title!, projectId: this.projectId })
      .subscribe(() => {
        this.form.reset();
        this.showForm.set(false);
        this.loadBoard();
      });
  }

  moveToNextStatus(task: Task): void {
    const order: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];
    const currentIndex = order.indexOf(task.status);
    if (currentIndex === -1 || currentIndex === order.length - 1) return;

    const nextStatus = order[currentIndex + 1];
    this.taskService.updateStatus(task.id, nextStatus).subscribe(() => this.loadBoard());
  }
}

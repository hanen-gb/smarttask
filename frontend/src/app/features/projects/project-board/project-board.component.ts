import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { TaskService } from '../../../core/services/task.service';
import { ProjectService } from '../../../core/services/project.service';
import { Task, TaskPriority, TaskStatus, TASK_PRIORITIES, TASK_STATUSES } from '../../../core/models/task.model';
import { PersonPickerComponent, PickablePerson } from '../../../shared/person-picker/person-picker.component';

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
  imports: [ReactiveFormsModule, RouterLink, PersonPickerComponent],
  templateUrl: './project-board.component.html',
  styleUrl: './project-board.component.scss',
})
export class ProjectBoardComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly taskService = inject(TaskService);
  private readonly projectService = inject(ProjectService);
  private readonly fb = inject(FormBuilder);

  readonly projectId = this.route.snapshot.paramMap.get('id')!;
  readonly columns = TASK_STATUSES;
  readonly statusLabel = STATUS_LABELS;
  readonly priorities = TASK_PRIORITIES;

  readonly board = signal<Record<TaskStatus, Task[]>>({
    TODO: [],
    IN_PROGRESS: [],
    IN_REVIEW: [],
    DONE: [],
    CANCELLED: [],
  });
  readonly showForm = signal(false);
  readonly members = signal<PickablePerson[]>([]);
  readonly selectedAssignee = signal<PickablePerson | null>(null);

  readonly form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(3)]],
    priority: this.fb.control<TaskPriority>('MEDIUM'),
  });

  constructor() {
    this.loadBoard();
    this.projectService.getMembers(this.projectId).subscribe((members) =>
      this.members.set(members.map((m) => ({ id: m.userId, firstName: m.firstName, lastName: m.lastName, email: m.email }))),
    );
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
    const value = this.form.getRawValue();
    this.taskService
      .create({
        title: value.title!,
        projectId: this.projectId,
        priority: value.priority!,
        assigneeId: this.selectedAssignee()?.id,
      })
      .subscribe(() => {
        this.form.reset({ priority: 'MEDIUM' });
        this.selectedAssignee.set(null);
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

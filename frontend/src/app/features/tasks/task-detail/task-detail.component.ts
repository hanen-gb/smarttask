import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { TaskService } from '../../../core/services/task.service';
import { CommentService } from '../../../core/services/comment.service';
import { AttachmentService } from '../../../core/services/attachment.service';
import { ProjectService } from '../../../core/services/project.service';
import { Task, TASK_PRIORITIES, TASK_STATUSES, TaskPriority, TaskStatus } from '../../../core/models/task.model';
import { Comment } from '../../../core/models/comment.model';
import { Attachment } from '../../../core/models/attachment.model';
import { ProjectMember } from '../../../core/models/project.model';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.scss',
})
export class TaskDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly taskService = inject(TaskService);
  private readonly commentService = inject(CommentService);
  private readonly attachmentService = inject(AttachmentService);
  private readonly projectService = inject(ProjectService);
  private readonly fb = inject(FormBuilder);

  readonly taskId = this.route.snapshot.paramMap.get('id')!;

  readonly task = signal<Task | null>(null);
  readonly members = signal<ProjectMember[]>([]);
  readonly comments = signal<Comment[]>([]);
  readonly attachments = signal<Attachment[]>([]);

  readonly statuses = TASK_STATUSES;
  readonly priorities = TASK_PRIORITIES;

  readonly errorMessage = signal<string | null>(null);
  readonly uploadError = signal<string | null>(null);
  readonly saved = signal(false);

  readonly editForm = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    priority: this.fb.control<TaskPriority>('MEDIUM'),
    dueDate: [''],
  });

  readonly commentForm = this.fb.group({
    content: ['', [Validators.required, Validators.minLength(1)]],
  });

  constructor() {
    this.loadTask();
    this.loadComments();
    this.loadAttachments();
  }

  private loadTask(): void {
    this.taskService.getById(this.taskId).subscribe({
      next: (task) => {
        this.task.set(task);
        this.editForm.patchValue({
          title: task.title,
          description: task.description ?? '',
          priority: task.priority,
          // input[type=date] attend "YYYY-MM-DD" -- on tronque l'ISO complet
          dueDate: task.dueDate ? task.dueDate.substring(0, 10) : '',
        });
        this.projectService.getMembers(task.projectId).subscribe((members) => this.members.set(members));
      },
      error: () => this.errorMessage.set('Tâche introuvable ou accès refusé.'),
    });
  }

  private loadComments(): void {
    this.commentService.getForTask(this.taskId).subscribe((comments) => this.comments.set(comments));
  }

  private loadAttachments(): void {
    this.attachmentService.getForTask(this.taskId).subscribe((attachments) => this.attachments.set(attachments));
  }

  saveTaskInfo(): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    const value = this.editForm.getRawValue();
    this.taskService
      .update(this.taskId, {
        title: value.title!,
        description: value.description || undefined,
        priority: value.priority!,
        dueDate: value.dueDate ? `${value.dueDate}T00:00:00Z` : undefined,
      })
      .subscribe({
        next: (task) => {
          this.task.set(task);
          this.saved.set(true);
          setTimeout(() => this.saved.set(false), 2000);
        },
        error: () => this.errorMessage.set("Vous n'avez pas le droit de modifier cette tâche."),
      });
  }

  changeStatus(status: TaskStatus): void {
    this.taskService.updateStatus(this.taskId, status).subscribe({
      next: (task) => this.task.set(task),
      error: () => this.errorMessage.set('Changement de statut refusé.'),
    });
  }

  changeAssignee(assigneeId: string): void {
    this.taskService.updateAssignee(this.taskId, assigneeId || null).subscribe({
      next: (task) => this.task.set(task),
      error: () =>
        this.errorMessage.set(
          "Seul le propriétaire/manager du projet peut réassigner une tâche.",
        ),
    });
  }

  deleteTask(): void {
    if (!confirm('Supprimer cette tâche ? Cette action est irréversible.')) return;

    this.taskService.delete(this.taskId).subscribe({
      next: () => this.router.navigateByUrl('/tasks'),
      error: () => this.errorMessage.set('Suppression refusée.'),
    });
  }

  submitComment(): void {
    if (this.commentForm.invalid) return;
    this.commentService.add(this.taskId, this.commentForm.value.content!).subscribe(() => {
      this.commentForm.reset();
      this.loadComments();
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploadError.set(null);
    this.attachmentService.upload(this.taskId, file).subscribe({
      next: () => {
        this.loadAttachments();
        input.value = '';
      },
      error: (err) => {
        this.uploadError.set(
          err.status === 415
            ? 'Type de fichier non autorisé.'
            : err.status === 413
              ? 'Fichier trop volumineux (max 10 Mo).'
              : "Échec de l'envoi.",
        );
      },
    });
  }

  downloadAttachment(attachment: Attachment): void {
    this.attachmentService.download(attachment);
  }

  formatSize(bytes: number): string {
    return bytes < 1024 ? `${bytes} o` : `${(bytes / 1024).toFixed(1)} Ko`;
  }
}

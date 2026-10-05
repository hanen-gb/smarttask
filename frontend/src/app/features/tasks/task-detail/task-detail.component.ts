import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommentService } from '../../../core/services/comment.service';
import { AttachmentService } from '../../../core/services/attachment.service';
import { Comment } from '../../../core/models/comment.model';
import { Attachment } from '../../../core/models/attachment.model';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.scss',
})
export class TaskDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly commentService = inject(CommentService);
  private readonly attachmentService = inject(AttachmentService);
  private readonly fb = inject(FormBuilder);

  readonly taskId = this.route.snapshot.paramMap.get('id')!;
  readonly comments = signal<Comment[]>([]);
  readonly attachments = signal<Attachment[]>([]);
  readonly uploadError = signal<string | null>(null);

  readonly commentForm = this.fb.group({
    content: ['', [Validators.required, Validators.minLength(1)]],
  });

  constructor() {
    this.loadComments();
    this.loadAttachments();
  }

  private loadComments(): void {
    this.commentService.getForTask(this.taskId).subscribe((comments) => this.comments.set(comments));
  }

  private loadAttachments(): void {
    this.attachmentService.getForTask(this.taskId).subscribe((attachments) => this.attachments.set(attachments));
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

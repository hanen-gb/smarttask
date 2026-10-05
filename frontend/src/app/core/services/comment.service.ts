import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Comment } from '../models/comment.model';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private readonly http = inject(HttpClient);

  getForTask(taskId: string) {
    return this.http.get<Comment[]>(`${environment.apiUrl}/tasks/${taskId}/comments`);
  }

  add(taskId: string, content: string) {
    return this.http.post<Comment>(`${environment.apiUrl}/tasks/${taskId}/comments`, { content });
  }

  delete(commentId: string) {
    return this.http.delete<void>(`${environment.apiUrl}/comments/${commentId}`);
  }
}

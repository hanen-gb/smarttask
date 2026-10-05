import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Attachment } from '../models/attachment.model';

@Injectable({ providedIn: 'root' })
export class AttachmentService {
  private readonly http = inject(HttpClient);

  getForTask(taskId: string) {
    return this.http.get<Attachment[]>(`${environment.apiUrl}/tasks/${taskId}/attachments`);
  }

  upload(taskId: string, file: File) {
    const formData = new FormData();
    formData.append('file', file);
    // Pas de Content-Type manuel ici : le navigateur le met lui-meme
    // (multipart/form-data; boundary=...) -- le forcer casserait l'upload.
    return this.http.post<Attachment>(`${environment.apiUrl}/tasks/${taskId}/attachments`, formData);
  }

  delete(attachmentId: string) {
    return this.http.delete<void>(`${environment.apiUrl}/attachments/${attachmentId}`);
  }

  /**
   * Le lien de telechargement exige un token (route protegee) -- impossible
   * via un simple <a href>, qui n'envoie pas notre en-tete Authorization.
   * On recupere le fichier en "blob" via HttpClient (passe par l'intercepteur),
   * puis on simule un clic de telechargement.
   */
  download(attachment: Attachment): void {
    this.http
      .get(`${environment.apiUrl}/attachments/${attachment.id}/download`, { responseType: 'blob' })
      .subscribe((blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = attachment.originalFilename;
        link.click();
        window.URL.revokeObjectURL(url);
      });
  }
}

export interface Attachment {
  id: string;
  originalFilename: string;
  contentType: string | null;
  sizeBytes: number;
  uploadedByName: string;
  createdAt: string;
}

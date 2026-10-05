export interface CommentAuthor {
  id: string;
  firstName: string;
  lastName: string;
}

export interface Comment {
  id: string;
  content: string;
  author: CommentAuthor;
  createdAt: string;
}

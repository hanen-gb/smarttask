package com.smarttask.backend.attachment;

import com.smarttask.backend.attachment.dto.AttachmentResponse;
import com.smarttask.backend.project.ProjectMemberRepository;
import com.smarttask.backend.task.Task;
import com.smarttask.backend.task.TaskRepository;
import com.smarttask.backend.user.Role;
import com.smarttask.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final Set<String> ALLOWED_PREFIXES = Set.of("image/", "application/pdf",
            "application/msword", "application/vnd.openxmlformats-officedocument",
            "application/zip");

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final FileStorageService fileStorageService;

    public AttachmentResponse upload(UUID taskId, User currentUser, MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache introuvable"));

        requireMembership(task, currentUser);
        validateFile(file);

        String storedFilename = fileStorageService.store(file);

        Attachment attachment = new Attachment();
        attachment.setOriginalFilename(file.getOriginalFilename());
        attachment.setStoredFilename(storedFilename);
        attachment.setContentType(file.getContentType());
        attachment.setSizeBytes(file.getSize());
        attachment.setTask(task);
        attachment.setUploadedBy(currentUser);

        return toResponse(attachmentRepository.save(attachment));
    }

    public List<AttachmentResponse> getTaskAttachments(UUID taskId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache introuvable"));
        requireMembership(task, currentUser);

        return attachmentRepository.findByTaskId(taskId).stream().map(this::toResponse).toList();
    }

    public Attachment getForDownload(UUID attachmentId, User currentUser) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable"));
        requireMembership(attachment.getTask(), currentUser);
        return attachment;
    }

    public Resource loadFile(Attachment attachment) {
        return fileStorageService.load(attachment.getStoredFilename());
    }

    public void deleteAttachment(UUID attachmentId, User currentUser) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable"));

        boolean isUploader = attachment.getUploadedBy().getId().equals(currentUser.getId());
        if (!isUploader && currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul celui qui a televerse ce fichier (ou un admin) peut le supprimer");
        }

        fileStorageService.delete(attachment.getStoredFilename());
        attachmentRepository.delete(attachment);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fichier vide");
        }

        String contentType = file.getContentType();
        boolean allowed = contentType != null
                && ALLOWED_PREFIXES.stream().anyMatch(contentType::startsWith);

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Type de fichier non autorise : " + contentType);
        }
        // La limite de taille (10 Mo) est deja appliquee automatiquement par
        // Spring (spring.servlet.multipart.max-file-size) AVANT meme d'arriver ici.
    }

    private void requireMembership(Task task, User currentUser) {
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(task.getProject().getId(), currentUser.getId());
        if (!isMember && currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'etes pas membre de ce projet");
        }
    }

    private AttachmentResponse toResponse(Attachment a) {
        return new AttachmentResponse(
                a.getId().toString(), a.getOriginalFilename(), a.getContentType(),
                a.getSizeBytes(), a.getUploadedBy().getFirstName() + " " + a.getUploadedBy().getLastName(),
                a.getCreatedAt()
        );
    }
}

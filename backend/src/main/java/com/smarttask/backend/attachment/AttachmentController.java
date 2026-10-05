package com.smarttask.backend.attachment;

import com.smarttask.backend.attachment.dto.AttachmentResponse;
import com.smarttask.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping("/api/tasks/{taskId}/attachments")
    public List<AttachmentResponse> getTaskAttachments(@PathVariable UUID taskId, @AuthenticationPrincipal User currentUser) {
        return attachmentService.getTaskAttachments(taskId, currentUser);
    }

    @PostMapping(value = "/api/tasks/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentResponse upload(
            @PathVariable UUID taskId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser
    ) {
        return attachmentService.upload(taskId, currentUser, file);
    }

    @GetMapping("/api/attachments/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        Attachment attachment = attachmentService.getForDownload(id, currentUser);
        Resource resource = attachmentService.loadFile(attachment);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        attachment.getContentType() != null ? attachment.getContentType() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(attachment.getOriginalFilename()).build().toString())
                .body(resource);
    }

    @DeleteMapping("/api/attachments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        attachmentService.deleteAttachment(id, currentUser);
    }
}

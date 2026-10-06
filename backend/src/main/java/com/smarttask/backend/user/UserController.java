package com.smarttask.backend.user;

import com.smarttask.backend.user.dto.UpdateProfileRequest;
import com.smarttask.backend.user.dto.UpdateRoleRequest;
import com.smarttask.backend.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Pas de @PreAuthorize ici (juste "etre connecte", deja impose globalement) :
    // n'importe quel membre doit pouvoir consulter l'annuaire pour choisir un
    // assigne ou un nouveau membre de projet. Seules les actions de GESTION
    // (changer un role, activer/desactiver) restent reservees a l'ADMIN.
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PutMapping("/me")
    public UserResponse updateOwnProfile(
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateOwnProfile(currentUser, request);
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse uploadAvatar(
        @AuthenticationPrincipal User currentUser,
        @RequestParam("file") MultipartFile file
    ) {
        return userService.uploadAvatar(currentUser, file);
    }

    // Route PUBLIQUE (voir SecurityConfig.PUBLIC_ROUTES) : une photo de profil
    // n'est pas une donnee sensible et <img src="..."> ne peut pas envoyer
    // d'en-tete Authorization, contrairement a une requete HttpClient.
    @GetMapping("/{id}/avatar")
    public ResponseEntity<Resource> getAvatar(@PathVariable UUID id) {
        Resource resource = userService.loadAvatar(id);

        String contentType;
        try {
            contentType = Files.probeContentType(Path.of(resource.getURI()));
        } catch (Exception e) {
            contentType = null;
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
            .body(resource);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateRole(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
        return userService.updateRole(id, request.role());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateStatus(@PathVariable UUID id, @RequestParam boolean enabled) {
        return userService.updateStatus(id, enabled);
    }
}

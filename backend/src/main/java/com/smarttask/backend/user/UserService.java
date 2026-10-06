package com.smarttask.backend.user;

import com.smarttask.backend.attachment.FileStorageService;
import com.smarttask.backend.user.dto.UpdateProfileRequest;
import com.smarttask.backend.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    public UserResponse updateOwnProfile(User currentUser, UpdateProfileRequest request) {
        currentUser.setFirstName(request.firstName());
        currentUser.setLastName(request.lastName());
        User saved = userRepository.save(currentUser);
        return toResponse(saved);
    }

    public UserResponse updateRole(UUID id, Role role) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        user.setRole(role);
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateStatus(UUID id, boolean enabled) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        user.setEnabled(enabled);
        return toResponse(userRepository.save(user));
    }

    public UserResponse uploadAvatar(User currentUser, MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fichier vide");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "La photo de profil doit etre une image");
        }

        String storedFilename = fileStorageService.store(file);
        currentUser.setAvatarUrl(storedFilename);
        return toResponse(userRepository.save(currentUser));
    }

    // Le nom de fichier stocke (UUID) n'est jamais expose au client -- seul
    // l'id utilisateur sert a retrouver la photo, comme pour les pieces jointes.
    public Resource loadAvatar(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        if (user.getAvatarUrl() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pas de photo de profil");
        }
        return fileStorageService.load(user.getAvatarUrl());
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId().toString(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole(),
            user.isEnabled(),
            user.getAvatarUrl() != null ? "/users/" + user.getId() + "/avatar" : null
        );
    }
}

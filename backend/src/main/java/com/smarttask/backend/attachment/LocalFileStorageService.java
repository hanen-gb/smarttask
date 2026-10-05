package com.smarttask.backend.attachment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path rootLocation;

    public LocalFileStorageService(@Value("${file.storage-location}") String storageLocation) {
        this.rootLocation = Path.of(storageLocation);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de creer le dossier de stockage : " + rootLocation, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        // On NE REUTILISE JAMAIS le nom fourni par l'utilisateur pour le fichier
        // sur le disque : un nom choisi par un utilisateur pourrait contenir
        // "../../etc/passwd" (path traversal) ou ecraser un fichier existant.
        // Un UUID garantit un nom unique et sans danger.
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String storedFilename = UUID.randomUUID() + extension;

        try {
            Path destination = rootLocation.resolve(storedFilename).normalize();
            file.transferTo(destination);
            return storedFilename;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Echec de l'enregistrement du fichier");
        }
    }

    @Override
    public Resource load(String storedFilename) {
        try {
            Path file = rootLocation.resolve(storedFilename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable");
        }
    }

    @Override
    public void delete(String storedFilename) {
        try {
            Files.deleteIfExists(rootLocation.resolve(storedFilename).normalize());
        } catch (IOException e) {
            // On ne bloque pas la suppression de la ligne en base pour autant
        }
    }
}

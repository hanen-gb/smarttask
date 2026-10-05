package com.smarttask.backend.attachment;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface de stockage de fichiers. L'implementation actuelle (LocalFileStorageService)
 * ecrit sur le disque local -- en production (etape 13+), on pourra la remplacer
 * par une implementation S3 SANS modifier AttachmentService ni AttachmentController,
 * qui ne connaissent que cette interface.
 */
public interface FileStorageService {

    /** Sauvegarde le fichier, renvoie le nom genere (pas le nom original) utilise pour le retrouver. */
    String store(MultipartFile file);

    /** Charge un fichier precedemment stocke, pour le telecharger. */
    Resource load(String storedFilename);

    void delete(String storedFilename);
}

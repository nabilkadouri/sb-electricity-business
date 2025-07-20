package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.exception.InvalidMediaTypeException;
import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadService {

    @Value("${upload.folder}")
    private String uploadFolderStr;
    private Path uploadFolderPath;

    // Cette méthode est exécutée après l'injection des dépendances (comme @Value)
    // Elle permet de créer le dossier uploads
    @PostConstruct
    public void init() {
        this.uploadFolderPath = Paths.get(uploadFolderStr).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadFolderPath);
        } catch (IOException e) {

            throw new RuntimeException("Impossible de créer le répertoire d'upload : " + this.uploadFolderPath, e);
        }
    }

    public String uploadImage(MultipartFile file) {
        // Vérifie si le fichier est bien une image
        if (!checkMediaType(file, "image")) {
            throw new InvalidMediaTypeException("image", "Le fichier uploadé n'est pas une image.");
        }

        // Génère un nom de fichier unique en utilisant UUID et conserve l'extension originale
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + extension;

        try {
            // Crée la miniature de l'image avant de sauvegarder l'original
            createThumbnail(file, filename);
            // Sauvegarde le fichier original dans le dossier d'upload
            file.transferTo(uploadFolderPath.resolve(filename));

        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException("Erreur lors du transfert vers le dossier d'upload " + uploadFolderPath, e);
        }
        return filename; // Retourne le nom unique du fichier
    }

    public void removeExisting(String filename) {
        Path filePath = uploadFolderPath.resolve(filename);
        Path thumbnailPath = uploadFolderPath.resolve("thumbnail-"+filename);
        try {
            Files.deleteIfExists(filePath);
            Files.deleteIfExists(thumbnailPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting uploaded file "+filePath,e);
        }
    }


    public void createThumbnail(MultipartFile file, String filename) throws IOException {
        Thumbnails.of(file.getInputStream())
                .crop(Positions.CENTER) // Centre l'image avant de la redimensionner
                .size(300, 300)      // Taille de la miniature
                .toFile(uploadFolderPath.resolve("thumbnail-" + filename).toFile());
    }


    public boolean checkMediaType(MultipartFile file, String expectedType) {
        Detector detector = new DefaultDetector();
        Metadata metadata = new Metadata();
        try {
            MediaType mediaType = detector.detect(new BufferedInputStream(file.getInputStream()), metadata);
            if (mediaType.getType().equals(expectedType)) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}

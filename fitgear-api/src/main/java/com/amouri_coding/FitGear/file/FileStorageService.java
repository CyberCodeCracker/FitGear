package com.amouri_coding.FitGear.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(
            @Value("${application.file.upload-dir:./uploads}") String uploadDir
    ) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + this.uploadDir, e);
        }
    }

    /**
     * Saves the file to disk under a sub-folder.
     * Returns the relative path from the upload root, e.g. "profile-pictures/abc123.jpg"
     */
    public String store(MultipartFile file, String subFolder) {
        FileUtils.validate(file);

        try {
            Path targetDir = uploadDir.resolve(subFolder);
            Files.createDirectories(targetDir);

            String filename = UUID.randomUUID() + FileUtils.getExtension(file);
            Path targetPath = targetDir.resolve(filename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored: {}", targetPath);
            return subFolder + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    /**
     * Deletes a file by its relative path.
     */
    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path filePath = uploadDir.resolve(relativePath);
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", relativePath, e);
        }
    }

    public Path getUploadDir() {
        return uploadDir;
    }
}

package com.vagabonder.service;

import com.vagabonder.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImageStorageService {
    private final Path uploadLocation;

    public ImageStorageService() {
        this.uploadLocation = Paths.get("uploads");
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(uploadLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    public List<String> storeMemories(List<MultipartFile> files, UUID userId) {
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }

        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> storeImage(file, "memory_" + userId))
                .collect(Collectors.toList());
    }

    public String storeImage(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Failed to store empty file");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            originalFilename = "file_" + UUID.randomUUID();
        }

        try {
            String safeFilename = StringUtils.cleanPath(originalFilename);
            String extension = getFileExtension(safeFilename);
            String newFilename = prefix + "_" + UUID.randomUUID() + extension;
            Path destinationFile = uploadLocation.resolve(newFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/images/" + newFilename;
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : ".bin";
    }

    public void deleteImage(String imagePath) {
        if (imagePath != null && imagePath.startsWith("/images/")) {
            try {
                Path file = uploadLocation.resolve(imagePath.substring("/images/".length()));
                Files.deleteIfExists(file);
            } catch (IOException e) {
                throw new StorageException("Failed to delete file: " + imagePath, e);
            }
        }
    }
}
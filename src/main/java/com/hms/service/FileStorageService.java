package com.hms.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) throws IOException {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadRoot.resolve("doctors"));
    }

    public String saveDoctorPhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "doctor.jpg" : file.getOriginalFilename());
        String extension = "";
        int lastDot = original.lastIndexOf('.');
        if (lastDot >= 0) {
            extension = original.substring(lastDot);
        }
        String fileName = UUID.randomUUID() + extension;
        Path target = uploadRoot.resolve("doctors").resolve(fileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/doctors/" + fileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to store doctor photo", ex);
        }
    }
}

package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Log4j2
public class UploadService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String uploadAnnouncementImage(MultipartFile file) throws ValidationException, InternalErrorException {
        try {
            validateImage(file);

            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "announcements";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String extension = resolveExtension(file.getOriginalFilename());
            String filename = "announcement_" + UUID.randomUUID() + extension;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/announcements/" + filename;

        } catch (ValidationException e) {
            throw e;
        } catch (IOException e) {
            log.error("Error saving announcement image", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error while uploading announcement image", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    private void validateImage(MultipartFile file) throws ValidationException {
        if (file == null || file.isEmpty()) {
            throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/jpeg")
                && !contentType.startsWith("image/png")
                && !contentType.startsWith("image/jpg"))) {
            throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
        }
    }

    private String resolveExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return ".jpg";
    }
}


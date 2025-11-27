package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
@Log4j2
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/announcements")
    public ResponseEntity<Map<String, Object>> uploadAnnouncementImage(@RequestParam("file") MultipartFile file)
            throws ValidationException, InternalErrorException {
        String url = uploadService.uploadAnnouncementImage(file);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Image uploaded successfully");
        response.put("error", false);
        response.put("data", Map.of("url", url));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Serve uploaded files (avatars, announcements, etc.)
     * GET /api/v1/uploads/{type}/{filename}
     * Example: /api/v1/uploads/avatars/user_3_ba177eb5-0927-401a-8e65-7fdf4b999d6c.jpg
     */
    @GetMapping("/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String type,
            @PathVariable String filename) {
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + type;
            Path filePath = Paths.get(uploadDir, filename).normalize();
            
            // Security check: ensure the file is within the uploads directory
            Path uploadsBase = Paths.get(System.getProperty("user.dir"), "uploads").normalize();
            if (!filePath.startsWith(uploadsBase)) {
                log.warn("Attempted access outside uploads directory: {}", filePath);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Resource resource = new FileSystemResource(filePath.toFile());
            
            if (!resource.exists() || !resource.isReadable()) {
                log.warn("File not found or not readable: {}", filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Determine content type
            String contentType = "application/octet-stream";
            try {
                contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
            } catch (Exception e) {
                log.warn("Could not determine content type for file: {}", filePath, e);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Error serving file: {}/{}", type, filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


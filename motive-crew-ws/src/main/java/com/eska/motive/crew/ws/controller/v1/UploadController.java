package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
}


package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.contract.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple health check controller for testing
 * 
 * @author Motive Crew Team
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * Simple health check endpoint
     * GET /api/v1/health
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Motive Crew API is running");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Health check with Response format
     * GET /api/v1/health/check
     */
    @GetMapping("/check")
    public ResponseEntity<Response> healthCheck() {
        Response response = new Response();
        response.setStatusCode(StatusCode.SUCCESS.getCode());
        response.setMessage("API is healthy and running");
        response.setError(false);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}


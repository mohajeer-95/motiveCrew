package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.service.AuthService;
import com.eska.motive.crew.ws.service.DashboardService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard controller
 * 
 * @author Motive Crew Team
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@Log4j2
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthService authService;

    /**
     * Get dashboard data
     * GET /api/v1/dashboard
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        try {
            User user = getCurrentUser(token);
            DashboardService.DashboardDTO dashboard = dashboardService.getDashboardData(user, month, year);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.SUCCESS.getCode());
            response.put("message", "Dashboard data retrieved successfully");
            response.put("error", false);
            response.put("data", dashboard);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (ResourceNotFoundException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.USER_NOT_FOUND.getCode());
            response.put("message", StatusCode.USER_NOT_FOUND.getDescription());
            response.put("error", true);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            log.error("Error getting dashboard data", e);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.INTERNAL_ERROR.getCode());
            response.put("message", "Error retrieving dashboard: " + e.getMessage());
            response.put("error", true);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }
}


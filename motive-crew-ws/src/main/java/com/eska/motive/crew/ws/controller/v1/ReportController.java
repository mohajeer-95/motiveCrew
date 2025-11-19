package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.service.ReportService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Report and summary controller
 * 
 * @author Motive Crew Team
 */
@RestController
@RequestMapping("/api/v1/reports")
@Log4j2
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Get monthly summary
     * GET /api/v1/reports/monthly-summary?month=11&year=2025
     */
    @GetMapping("/monthly-summary")
    public ResponseEntity<Map<String, Object>> getMonthlySummary(
            @RequestParam Integer month,
            @RequestParam Integer year)
            throws ResourceNotFoundException {
        ReportService.MonthlySummaryDTO summary = reportService.getMonthlySummary(month, year);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Monthly summary retrieved successfully");
        response.put("error", false);
        response.put("data", summary);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}


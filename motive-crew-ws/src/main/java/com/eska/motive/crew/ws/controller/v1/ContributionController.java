package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.MarkPaymentRequest;
import com.eska.motive.crew.ws.entity.MemberPayment;
import com.eska.motive.crew.ws.entity.MonthlyCollection;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AuthService;
import com.eska.motive.crew.ws.service.ContributionService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Monthly contribution controller
 * 
 * @author Motive Crew Team
 */
@RestController
@RequestMapping("/api/v1/contributions")
@Log4j2
public class ContributionController {

    @Autowired
    private ContributionService contributionService;

    @Autowired
    private AuthService authService;

    /**
     * Get current month collection
     * GET /api/v1/contributions/current
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentMonthCollection()
            throws ResourceNotFoundException {
        MonthlyCollection collection = contributionService.getCurrentMonthCollection();
        ContributionService.CollectionStats stats = contributionService.calculateStats(collection);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Current month collection retrieved");
        response.put("error", false);
        response.put("data", buildCollectionResponse(collection, stats));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Get collection by year and month
     * GET /api/v1/contributions/monthly?year=2025&month=11
     */
    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyCollection(
            @RequestParam Integer year,
            @RequestParam Integer month)
            throws ResourceNotFoundException {
        try {
            // Get or create monthly collection (creates if doesn't exist)
            MonthlyCollection collection = contributionService.getOrCreateMonthlyCollection(year, month);
            ContributionService.CollectionStats stats = contributionService.calculateStats(collection);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.SUCCESS.getCode());
            response.put("message", "Monthly collection retrieved");
            response.put("error", false);
            response.put("data", buildCollectionResponse(collection, stats));
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error getting monthly collection", e);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.INTERNAL_ERROR.getCode());
            response.put("message", "Error retrieving monthly collection: " + e.getMessage());
            response.put("error", true);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Mark member as paid
     * POST /api/v1/contributions/{collectionId}/members/{memberId}/pay
     */
    @PostMapping("/{collectionId}/members/{memberId}/pay")
    public ResponseEntity<Map<String, Object>> markMemberAsPaid(
            @RequestHeader("Authorization") String token,
            @PathVariable Long collectionId,
            @PathVariable Long memberId,
            @Valid @RequestBody MarkPaymentRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        MemberPayment payment = contributionService.markMemberAsPaid(collectionId, memberId, request, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Member marked as paid");
        response.put("error", false);
        response.put("data", buildPaymentResponse(payment));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Mark member as unpaid
     * DELETE /api/v1/contributions/{collectionId}/members/{memberId}/pay
     */
    @DeleteMapping("/{collectionId}/members/{memberId}/pay")
    public ResponseEntity<Map<String, Object>> markMemberAsUnpaid(
            @RequestHeader("Authorization") String token,
            @PathVariable Long collectionId,
            @PathVariable Long memberId)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        contributionService.markMemberAsUnpaid(collectionId, memberId, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Member marked as unpaid");
        response.put("error", false);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Update target amount
     * PUT /api/v1/contributions/{collectionId}/target
     */
    @PutMapping("/{collectionId}/target")
    public ResponseEntity<Map<String, Object>> updateTargetAmount(
            @RequestHeader("Authorization") String token,
            @PathVariable Long collectionId,
            @RequestParam BigDecimal targetAmount)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        MonthlyCollection collection = contributionService.updateTargetAmount(collectionId, targetAmount, currentUser);
        ContributionService.CollectionStats stats = contributionService.calculateStats(collection);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Target amount updated");
        response.put("error", false);
        response.put("data", buildCollectionResponse(collection, stats));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Lock collection
     * POST /api/v1/contributions/{collectionId}/lock
     */
    @PostMapping("/{collectionId}/lock")
    public ResponseEntity<Map<String, Object>> lockCollection(
            @RequestHeader("Authorization") String token,
            @PathVariable Long collectionId)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        MonthlyCollection collection = contributionService.lockCollection(collectionId, currentUser);
        ContributionService.CollectionStats stats = contributionService.calculateStats(collection);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Collection locked successfully");
        response.put("error", false);
        response.put("data", buildCollectionResponse(collection, stats));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Get collection payments
     * GET /api/v1/contributions/{collectionId}/payments
     */
    @GetMapping("/{collectionId}/payments")
    public ResponseEntity<Map<String, Object>> getCollectionPayments(@PathVariable Long collectionId)
            throws ResourceNotFoundException {
        List<MemberPayment> payments = contributionService.getCollectionPayments(collectionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Payments retrieved");
        response.put("error", false);
        response.put("data", payments.stream()
                .map(this::buildPaymentResponse)
                .toList());
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }

    private Map<String, Object> buildCollectionResponse(MonthlyCollection collection, ContributionService.CollectionStats stats) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", collection.getId());
        data.put("year", collection.getYear());
        data.put("month", collection.getMonth());
        data.put("targetAmount", collection.getTargetAmount());
        data.put("isLocked", collection.getIsLocked());
        data.put("stats", stats);
        return data;
    }

    private Map<String, Object> buildPaymentResponse(MemberPayment payment) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", payment.getId());
        data.put("memberId", payment.getUser().getId());
        data.put("memberName", payment.getUser().getName());
        data.put("collectionId", payment.getCollection().getId());
        data.put("amount", payment.getAmount());
        data.put("paymentDate", payment.getPaymentDate());
        data.put("status", payment.getStatus());
        data.put("notes", payment.getNotes());
        data.put("createdAt", payment.getCreatedAt());
        data.put("updatedAt", payment.getUpdatedAt());
        return data;
    }
}


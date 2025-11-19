package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.MarkPaymentRequest;
import com.eska.motive.crew.ws.entity.MemberPayment;
import com.eska.motive.crew.ws.entity.MonthlyCollection;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.repository.MemberPaymentRepository;
import com.eska.motive.crew.ws.repository.MonthlyCollectionRepository;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing monthly contributions
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class ContributionService {

    @Autowired
    private MonthlyCollectionRepository collectionRepository;

    @Autowired
    private MemberPaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get or create monthly collection for a specific month
     */
    @Transactional
    public MonthlyCollection getOrCreateMonthlyCollection(Integer year, Integer month) {
        Optional<MonthlyCollection> collection = collectionRepository.findByYearAndMonth(year, month);
        
        if (collection.isPresent()) {
            return collection.get();
        }

        // Create new collection
        MonthlyCollection newCollection = MonthlyCollection.builder()
                .year(year)
                .month(month)
                .targetAmount(new BigDecimal("5.00"))
                .isLocked(false)
                .build();

        return collectionRepository.save(newCollection);
    }

    /**
     * Get monthly collection by year and month
     */
    public MonthlyCollection getMonthlyCollection(Integer year, Integer month)
            throws ResourceNotFoundException {
        return collectionRepository.findByYearAndMonth(year, month)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
    }

    /**
     * Get current month collection
     */
    public MonthlyCollection getCurrentMonthCollection() throws ResourceNotFoundException {
        LocalDate now = LocalDate.now();
        return getMonthlyCollection(now.getYear(), now.getMonthValue());
    }

    /**
     * Mark member as paid for a collection
     */
    @Transactional
    public MemberPayment markMemberAsPaid(Long collectionId, Long memberId, MarkPaymentRequest request, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can mark payments
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            MonthlyCollection collection = collectionRepository.findById(collectionId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            // Check if collection is locked
            if (collection.getIsLocked()) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));

            // Find or create payment record
            Optional<MemberPayment> existingPayment = paymentRepository.findByUserAndCollection(member, collection);
            
            MemberPayment payment;
            if (existingPayment.isPresent()) {
                payment = existingPayment.get();
                payment.setAmount(request.getAmount());
                payment.setStatus(MemberPayment.PaymentStatus.PAID);
                payment.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now());
                payment.setNotes(request.getNotes());
            } else {
                payment = MemberPayment.builder()
                        .user(member)
                        .collection(collection)
                        .amount(request.getAmount())
                        .status(MemberPayment.PaymentStatus.PAID)
                        .paymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now())
                        .notes(request.getNotes())
                        .build();
            }

            return paymentRepository.save(payment);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error marking member as paid", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Mark member as unpaid
     */
    @Transactional
    public void markMemberAsUnpaid(Long collectionId, Long memberId, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can mark payments
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            MonthlyCollection collection = collectionRepository.findById(collectionId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            // Check if collection is locked
            if (collection.getIsLocked()) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));

            Optional<MemberPayment> payment = paymentRepository.findByUserAndCollection(member, collection);
            
            if (payment.isPresent()) {
                payment.get().setStatus(MemberPayment.PaymentStatus.PENDING);
                payment.get().setAmount(BigDecimal.ZERO);
                payment.get().setPaymentDate(null);
                paymentRepository.save(payment.get());
            }

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error marking member as unpaid", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Update target amount for a collection
     */
    @Transactional
    public MonthlyCollection updateTargetAmount(Long collectionId, BigDecimal targetAmount, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can update target
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            MonthlyCollection collection = collectionRepository.findById(collectionId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            // Check if collection is locked
            if (collection.getIsLocked()) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            collection.setTargetAmount(targetAmount);
            return collectionRepository.save(collection);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating target amount", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Lock monthly collection
     */
    @Transactional
    public MonthlyCollection lockCollection(Long collectionId, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can lock
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            MonthlyCollection collection = collectionRepository.findById(collectionId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            collection.setIsLocked(true);
            collection.setLockedAt(LocalDateTime.now());
            collection.setLockedBy(currentUser);

            return collectionRepository.save(collection);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error locking collection", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Get all payments for a collection
     */
    public List<MemberPayment> getCollectionPayments(Long collectionId) throws ResourceNotFoundException {
        MonthlyCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
        return paymentRepository.findByCollection(collection);
    }

    /**
     * Calculate collection statistics
     */
    public CollectionStats calculateStats(MonthlyCollection collection) {
        List<MemberPayment> payments = paymentRepository.findByCollection(collection);
        
        long membersPaid = paymentRepository.countByCollectionAndStatus(collection, MemberPayment.PaymentStatus.PAID);
        long totalMembers = userRepository.countByIsActiveTrue();
        
        BigDecimal totalCollected = payments.stream()
                .filter(p -> p.getStatus() == MemberPayment.PaymentStatus.PAID)
                .map(MemberPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal goalAmount = collection.getTargetAmount().multiply(new BigDecimal(totalMembers));
        BigDecimal remainingAmount = goalAmount.subtract(totalCollected);
        
        double progressPercentage = goalAmount.compareTo(BigDecimal.ZERO) > 0
                ? totalCollected.divide(goalAmount, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100)).doubleValue()
                : 0.0;

        return CollectionStats.builder()
                .totalMembers((int) totalMembers)
                .membersPaid((int) membersPaid)
                .membersPending((int) (totalMembers - membersPaid))
                .totalCollected(totalCollected)
                .goalAmount(goalAmount)
                .remainingAmount(remainingAmount)
                .progressPercentage(progressPercentage)
                .build();
    }

    /**
     * Collection statistics DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class CollectionStats {
        private int totalMembers;
        private int membersPaid;
        private int membersPending;
        private BigDecimal totalCollected;
        private BigDecimal goalAmount;
        private BigDecimal remainingAmount;
        private double progressPercentage;
    }
}


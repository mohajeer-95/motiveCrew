package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.MemberPayment;
import com.eska.motive.crew.ws.entity.MonthlyCollection;
import com.eska.motive.crew.ws.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for MemberPayment entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface MemberPaymentRepository extends JpaRepository<MemberPayment, Long> {

    Optional<MemberPayment> findByUserAndCollection(User user, MonthlyCollection collection);

    List<MemberPayment> findByCollection(MonthlyCollection collection);

    List<MemberPayment> findByUser(User user);

    long countByCollectionAndStatus(MonthlyCollection collection, MemberPayment.PaymentStatus status);
}


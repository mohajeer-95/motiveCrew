package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Event;
import com.eska.motive.crew.ws.entity.Expense;
import com.eska.motive.crew.ws.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Expense entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE " +
           "(:month IS NULL OR MONTH(e.expenseDate) = :month) AND " +
           "(:year IS NULL OR YEAR(e.expenseDate) = :year) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:eventId IS NULL OR e.event.id = :eventId) AND " +
           "(:paidById IS NULL OR e.paidBy.id = :paidById) AND " +
           "(:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Expense> findByFilters(
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("category") Expense.ExpenseCategory category,
            @Param("eventId") Long eventId,
            @Param("paidById") Long paidById,
            @Param("search") String search,
            Pageable pageable
    );

    List<Expense> findByEvent(Event event);

    List<Expense> findByPaidBy(User user);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE " +
           "MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
    BigDecimal getTotalSpentByMonth(@Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE " +
           "MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year " +
           "GROUP BY e.category")
    List<Object[]> getExpensesByCategory(@Param("month") Integer month, @Param("year") Integer year);
}


package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.entity.Event;
import com.eska.motive.crew.ws.entity.Expense;
import com.eska.motive.crew.ws.entity.MemberPayment;
import com.eska.motive.crew.ws.entity.MonthlyCollection;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.repository.EventRepository;
import com.eska.motive.crew.ws.repository.ExpenseRepository;
import com.eska.motive.crew.ws.repository.MemberPaymentRepository;
import com.eska.motive.crew.ws.repository.MonthlyCollectionRepository;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for generating reports and summaries
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class ReportService {

    @Autowired
    private MonthlyCollectionRepository collectionRepository;

    @Autowired
    private MemberPaymentRepository paymentRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContributionService contributionService;

    @Autowired
    private ExpenseService expenseService;

    /**
     * Get monthly summary report
     */
    public MonthlySummaryDTO getMonthlySummary(Integer month, Integer year) throws ResourceNotFoundException {
        MonthlyCollection collection = collectionRepository.findByYearAndMonth(year, month)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

        // Get collection stats
        ContributionService.CollectionStats stats = contributionService.calculateStats(collection);

        // Get expenses summary
        ExpenseService.ExpenseSummary expenseSummary = expenseService.getExpenseSummary(month, year);

        // Get events for the month
        List<Event> events = eventRepository.findByFilters(null, null, month, year, null, org.springframework.data.domain.Pageable.unpaged())
                .getContent();

        // Get member contributions
        List<MemberPayment> payments = paymentRepository.findByCollection(collection);

        // Calculate balance
        BigDecimal balance = stats.getTotalCollected().subtract(expenseSummary.getTotalSpent());

        // Generate insights
        List<String> insights = generateInsights(stats, expenseSummary, events);

        return MonthlySummaryDTO.builder()
                .month(month)
                .year(year)
                .monthName(getMonthName(month) + " " + year)
                .financial(FinancialSummary.builder()
                        .totalCollected(stats.getTotalCollected())
                        .totalSpent(expenseSummary.getTotalSpent())
                        .balance(balance)
                        .membersPaid(stats.getMembersPaid())
                        .totalMembers(stats.getTotalMembers())
                        .progressPercentage(stats.getProgressPercentage())
                        .build())
                .expensesByCategory(expenseSummary.getExpensesByCategory())
                .events(events.stream()
                        .map(e -> EventSummary.builder()
                                .id(e.getId())
                                .name(e.getName())
                                .type(e.getType().name())
                                .cost(e.getActualCost() != null ? e.getActualCost() : e.getEstimatedCost())
                                .participants(e.getParticipants() != null 
                                        ? (int) e.getParticipants().stream()
                                                .filter(p -> p.getStatus() == com.eska.motive.crew.ws.entity.EventParticipant.ParticipantStatus.JOINED)
                                                .count()
                                        : 0)
                                .build())
                        .collect(Collectors.toList()))
                .memberContributions(payments.stream()
                        .map(p -> MemberContribution.builder()
                                .memberId(p.getUser().getId())
                                .memberName(p.getUser().getName())
                                .amount(p.getAmount())
                                .status(p.getStatus().name())
                                .build())
                        .collect(Collectors.toList()))
                .insights(insights)
                .build();
    }

    /**
     * Generate insights based on data
     */
    private List<String> generateInsights(ContributionService.CollectionStats stats,
                                         ExpenseService.ExpenseSummary expenseSummary,
                                         List<Event> events) {
        java.util.List<String> insights = new java.util.ArrayList<>();

        // Contribution insight
        if (stats.getProgressPercentage() >= 90) {
            insights.add(String.format("%.0f%% of members contributed — excellent consistency!", stats.getProgressPercentage()));
        } else if (stats.getProgressPercentage() >= 70) {
            insights.add(String.format("%.0f%% of members contributed — good progress!", stats.getProgressPercentage()));
        } else {
            insights.add(String.format("%.0f%% of members contributed — need more contributions", stats.getProgressPercentage()));
        }

        // Expense insight
        if (expenseSummary.getTotalSpent().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal highestExpense = expenseSummary.getTotalSpent();
            insights.add(String.format("Total expenses this month: %.2f JOD", highestExpense));
        }

        // Balance insight
        BigDecimal balance = stats.getTotalCollected().subtract(expenseSummary.getTotalSpent());
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            insights.add(String.format("You have %.2f JOD left for next month's activities", balance));
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            insights.add(String.format("Warning: Overspent by %.2f JOD this month", balance.abs()));
        }

        // Events insight
        if (!events.isEmpty()) {
            Event highestCostEvent = events.stream()
                    .max((e1, e2) -> {
                        BigDecimal cost1 = e1.getActualCost() != null ? e1.getActualCost() : e1.getEstimatedCost() != null ? e1.getEstimatedCost() : BigDecimal.ZERO;
                        BigDecimal cost2 = e2.getActualCost() != null ? e2.getActualCost() : e2.getEstimatedCost() != null ? e2.getEstimatedCost() : BigDecimal.ZERO;
                        return cost1.compareTo(cost2);
                    })
                    .orElse(null);

            if (highestCostEvent != null) {
                BigDecimal cost = highestCostEvent.getActualCost() != null
                        ? highestCostEvent.getActualCost()
                        : highestCostEvent.getEstimatedCost() != null ? highestCostEvent.getEstimatedCost() : BigDecimal.ZERO;
                insights.add(String.format("Highest expense event: %s (%.2f JOD)", highestCostEvent.getName(), cost));
            }
        }

        return insights;
    }

    /**
     * Get month name
     */
    private String getMonthName(int month) {
        String[] monthNames = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }

    /**
     * Monthly Summary DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class MonthlySummaryDTO {
        private Integer month;
        private Integer year;
        private String monthName;
        private FinancialSummary financial;
        private List<Object[]> expensesByCategory;
        private List<EventSummary> events;
        private List<MemberContribution> memberContributions;
        private List<String> insights;
    }

    @lombok.Data
    @lombok.Builder
    public static class FinancialSummary {
        private BigDecimal totalCollected;
        private BigDecimal totalSpent;
        private BigDecimal balance;
        private int membersPaid;
        private int totalMembers;
        private double progressPercentage;
    }

    @lombok.Data
    @lombok.Builder
    public static class EventSummary {
        private Long id;
        private String name;
        private String type;
        private BigDecimal cost;
        private int participants;
    }

    @lombok.Data
    @lombok.Builder
    public static class MemberContribution {
        private Long memberId;
        private String memberName;
        private BigDecimal amount;
        private String status;
    }
}


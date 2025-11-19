package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.ws.entity.Event;
import com.eska.motive.crew.ws.entity.MemberPayment;
import com.eska.motive.crew.ws.entity.MonthlyCollection;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.repository.EventRepository;
import com.eska.motive.crew.ws.repository.MemberPaymentRepository;
import com.eska.motive.crew.ws.repository.MonthlyCollectionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for dashboard/home screen data
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class DashboardService {

    @Autowired
    private MonthlyCollectionRepository collectionRepository;

    @Autowired
    private MemberPaymentRepository paymentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ContributionService contributionService;

    @Autowired
    private ExpenseService expenseService;

    /**
     * Get dashboard data for home screen
     */
    public DashboardDTO getDashboardData(User user, Integer month, Integer year) {
        if (month == null || year == null) {
            LocalDate now = LocalDate.now();
            month = now.getMonthValue();
            year = now.getYear();
        }

        // Get or create monthly collection
        MonthlyCollection collection = contributionService.getOrCreateMonthlyCollection(year, month);

        // Calculate collection stats
        ContributionService.CollectionStats stats = contributionService.calculateStats(collection);

        // Get expenses summary
        ExpenseService.ExpenseSummary expenseSummary = expenseService.getExpenseSummary(month, year);

        // Calculate balance
        BigDecimal balance = stats.getTotalCollected().subtract(expenseSummary.getTotalSpent());

        // Get upcoming events
        List<Event> upcomingEvents = eventRepository.findByEventDateAfterAndStatusOrderByEventDateAsc(
                LocalDate.now(), Event.EventStatus.UPCOMING);

        Event upcomingEvent = upcomingEvents.isEmpty() ? null : upcomingEvents.get(0);

        // Get recent activity (last 5 payments and events)
        List<MemberPayment> recentPayments = paymentRepository.findByCollection(collection)
                .stream()
                .filter(p -> p.getStatus() == MemberPayment.PaymentStatus.PAID)
                .sorted((p1, p2) -> p2.getPaymentDate().compareTo(p1.getPaymentDate()))
                .limit(3)
                .collect(Collectors.toList());

        // Count events for the month
        long eventsCount = eventRepository.findByFilters(null, null, month, year, null,
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        return DashboardDTO.builder()
                .user(UserInfo.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .build())
                .summary(SummaryInfo.builder()
                        .month(getMonthName(month) + " " + year)
                        .totalCollected(stats.getTotalCollected())
                        .totalSpent(expenseSummary.getTotalSpent())
                        .balance(balance)
                        .eventsCount((int) eventsCount)
                        .membersPaid(stats.getMembersPaid())
                        .totalMembers(stats.getTotalMembers())
                        .progressPercentage(stats.getProgressPercentage())
                        .build())
                .upcomingEvent(upcomingEvent != null ? UpcomingEventInfo.builder()
                        .id(upcomingEvent.getId())
                        .name(upcomingEvent.getName())
                        .date(upcomingEvent.getEventDate())
                        .time(upcomingEvent.getEventTime())
                        .location(upcomingEvent.getLocation())
                        .participants(upcomingEvent.getParticipants() != null 
                                ? (int) upcomingEvent.getParticipants().stream()
                                        .filter(p -> p.getStatus() == com.eska.motive.crew.ws.entity.EventParticipant.ParticipantStatus.JOINED)
                                        .count()
                                : 0)
                        .type(upcomingEvent.getType().name())
                        .build() : null)
                .recentActivity(buildRecentActivity(recentPayments, upcomingEvents))
                .quickActions(QuickActions.builder()
                        .canAddEvent(user.getRole() == User.UserRole.ADMIN)
                        .canRecordPayment(user.getRole() == User.UserRole.ADMIN)
                        .canAddExpense(user.getRole() == User.UserRole.ADMIN)
                        .build())
                .build();
    }

    /**
     * Build recent activity list
     */
    private List<ActivityItem> buildRecentActivity(List<MemberPayment> payments, List<Event> events) {
        List<ActivityItem> activities = new java.util.ArrayList<>();

        // Add payment activities
        payments.forEach(payment -> {
            activities.add(ActivityItem.builder()
                    .text(String.format("%s paid %.2f JOD", payment.getUser().getName(), payment.getAmount()))
                    .date(payment.getPaymentDate())
                    .type("payment")
                    .build());
        });

        // Add event activities (limit to 2 most recent)
        events.stream()
                .limit(2)
                .forEach(event -> {
                    activities.add(ActivityItem.builder()
                            .text(String.format("Event created: %s", event.getName()))
                            .date(event.getCreatedAt().toLocalDate())
                            .type("event")
                            .build());
                });

        // Sort by date descending and limit to 5
        return activities.stream()
                .sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate()))
                .limit(5)
                .collect(Collectors.toList());
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
     * Dashboard DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class DashboardDTO {
        private UserInfo user;
        private SummaryInfo summary;
        private UpcomingEventInfo upcomingEvent;
        private List<ActivityItem> recentActivity;
        private QuickActions quickActions;
    }

    @lombok.Data
    @lombok.Builder
    public static class UserInfo {
        private Long id;
        private String name;
        private String role;
    }

    @lombok.Data
    @lombok.Builder
    public static class SummaryInfo {
        private String month;
        private BigDecimal totalCollected;
        private BigDecimal totalSpent;
        private BigDecimal balance;
        private int eventsCount;
        private int membersPaid;
        private int totalMembers;
        private double progressPercentage;
    }

    @lombok.Data
    @lombok.Builder
    public static class UpcomingEventInfo {
        private Long id;
        private String name;
        private LocalDate date;
        private java.time.LocalTime time;
        private String location;
        private int participants;
        private String type;
    }

    @lombok.Data
    @lombok.Builder
    public static class ActivityItem {
        private String text;
        private LocalDate date;
        private String type;
    }

    @lombok.Data
    @lombok.Builder
    public static class QuickActions {
        private boolean canAddEvent;
        private boolean canRecordPayment;
        private boolean canAddExpense;
    }
}


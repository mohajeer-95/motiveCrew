package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreatePollRequest;
import com.eska.motive.crew.ws.entity.Poll;
import com.eska.motive.crew.ws.entity.PollOption;
import com.eska.motive.crew.ws.entity.PollVote;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.enums.PollStatus;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.repository.PollRepository;
import com.eska.motive.crew.ws.repository.PollVoteRepository;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PollService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollVoteRepository pollVoteRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public PollListResult listPolls(User user, String statusParam, String search) {
        PollStatus statusFilter = parseStatus(statusParam);
        String searchValue = StringUtils.hasText(search) ? search.trim() : null;

        List<Poll> polls;
        if (statusFilter != null && searchValue != null) {
            polls = pollRepository.findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(statusFilter, searchValue);
        } else if (statusFilter != null) {
            polls = pollRepository.findByStatusOrderByCreatedAtDesc(statusFilter);
        } else if (searchValue != null) {
            polls = pollRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(searchValue);
        } else {
            polls = pollRepository.findAllByOrderByCreatedAtDesc();
        }

        Map<Long, Long> userVotes = Collections.emptyMap();
        if (user != null && !polls.isEmpty()) {
            List<PollVote> votes = pollVoteRepository.findByUserAndPollIn(user, polls);
            userVotes = votes.stream()
                    .collect(Collectors.toMap(vote -> vote.getPoll().getId(), vote -> vote.getOption().getId()));
        }

        PollStatsResult stats = PollStatsResult.builder()
                .activeCount(pollRepository.countByStatus(PollStatus.ACTIVE))
                .closedCount(pollRepository.countByStatus(PollStatus.CLOSED))
                .totalVotes(pollVoteRepository.count())
                .participationRate(calculateParticipationRate())
                .build();

        return PollListResult.builder()
                .polls(polls)
                .userVotes(userVotes)
                .stats(stats)
                .build();
    }

    @Transactional
    public Poll createPoll(User currentUser, CreatePollRequest request) throws ValidationException {
        if (currentUser == null || currentUser.getRole() != User.UserRole.ADMIN) {
            throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
        }

        List<String> optionLabels = request.getOptions() != null
                ? request.getOptions().stream()
                .filter(opt -> StringUtils.hasText(opt))
                .map(String::trim)
                .collect(Collectors.toList())
                : Collections.emptyList();

        if (optionLabels.size() < 2) {
            throw new ValidationException("At least two options are required");
        }

        Poll poll = Poll.builder()
                .title(request.getTitle().trim())
                .description(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null)
                .status(PollStatus.ACTIVE)
                .expiresAt(parseDeadline(request.getExpiresAt()))
                .createdBy(currentUser)
                .build();

        for (int i = 0; i < optionLabels.size(); i++) {
            poll.addOption(PollOption.builder()
                    .label(optionLabels.get(i))
                    .position(i)
                    .votesCount(0)
                    .build());
        }

        return pollRepository.save(poll);
    }

    @Transactional
    public Poll vote(User user, Long pollId, Long optionId)
            throws ResourceNotFoundException, ValidationException {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

        if (poll.getStatus() != PollStatus.ACTIVE) {
            throw new ValidationException("Poll is not active");
        }

        if (poll.getExpiresAt() != null && poll.getExpiresAt().isBefore(LocalDateTime.now())) {
            poll.setStatus(PollStatus.CLOSED);
            pollRepository.save(poll);
            throw new ValidationException("Poll has already expired");
        }

        if (pollVoteRepository.findByPollAndUser(poll, user).isPresent()) {
            throw new ValidationException("You have already voted for this poll");
        }

        Optional<PollOption> optionOptional = poll.getOptions().stream()
                .filter(option -> option.getId().equals(optionId))
                .findFirst();

        if (optionOptional.isEmpty()) {
            throw new ValidationException("Invalid option selected");
        }

        PollOption option = optionOptional.get();
        option.setVotesCount(option.getVotesCount() + 1);

        PollVote pollVote = PollVote.builder()
                .poll(poll)
                .option(option)
                .user(user)
                .build();
        pollVoteRepository.save(pollVote);

        return pollRepository.save(poll);
    }

    private PollStatus parseStatus(String statusParam) {
        if (!StringUtils.hasText(statusParam)) {
            return null;
        }
        try {
            return PollStatus.valueOf(statusParam.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.warn("Unknown poll status filter: {}", statusParam);
            return null;
        }
    }

    private LocalDateTime parseDeadline(String deadline) throws ValidationException {
        if (!StringUtils.hasText(deadline)) {
            return null;
        }
        String value = deadline.trim();
        try {
            if (value.length() == 10) { // yyyy-MM-dd
                LocalDate date = LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
                return date.atTime(LocalTime.MAX);
            }
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception ex) {
            throw new ValidationException("Invalid deadline format");
        }
    }

    private double calculateParticipationRate() {
        long totalMembers = userRepository.count();
        if (totalMembers == 0) {
            return 0;
        }
        long uniqueVoters = pollVoteRepository.countDistinctUserIds();
        return (uniqueVoters * 100.0) / totalMembers;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class PollListResult {
        private List<Poll> polls;
        @Builder.Default
        private Map<Long, Long> userVotes = Collections.emptyMap();
        private PollStatsResult stats;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class PollStatsResult {
        private long activeCount;
        private long closedCount;
        private long totalVotes;
        private double participationRate;
    }
}


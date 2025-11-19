package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreatePollRequest;
import com.eska.motive.crew.ws.dto.request.VotePollRequest;
import com.eska.motive.crew.ws.entity.Poll;
import com.eska.motive.crew.ws.entity.PollOption;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AuthService;
import com.eska.motive.crew.ws.service.PollService;
import com.eska.motive.crew.ws.service.PollService.PollListResult;
import com.eska.motive.crew.ws.service.PollService.PollStatsResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/v1/polls")
@Log4j2
public class PollController {

    @Autowired
    private PollService pollService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPolls(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) throws ResourceNotFoundException {
        User currentUser = getCurrentUser(token);
        PollListResult result = pollService.listPolls(currentUser, status, search);

        Map<String, Object> data = new HashMap<>();
        data.put("items", buildPollItems(result.getPolls(), result.getUserVotes()));
        data.put("stats", buildStatsResponse(result.getStats()));

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Polls retrieved successfully");
        response.put("error", false);
        response.put("data", data);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPoll(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreatePollRequest request)
            throws ResourceNotFoundException, ValidationException {
        User currentUser = getCurrentUser(token);
        Poll poll = pollService.createPoll(currentUser, request);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Poll created successfully");
        response.put("error", false);
        response.put("data", buildPollResponse(poll, Collections.emptyMap()));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{pollId}/vote")
    public ResponseEntity<Map<String, Object>> vote(
            @RequestHeader("Authorization") String token,
            @PathVariable Long pollId,
            @Valid @RequestBody VotePollRequest request)
            throws ResourceNotFoundException, ValidationException {
        User currentUser = getCurrentUser(token);
        Poll poll = pollService.vote(currentUser, pollId, request.getOptionId());

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Vote submitted successfully");
        response.put("error", false);
        response.put("data", buildPollResponse(poll, Map.of(poll.getId(), request.getOptionId())));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }

    private List<Map<String, Object>> buildPollItems(List<Poll> polls, Map<Long, Long> userVotes) {
        if (CollectionUtils.isEmpty(polls)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (Poll poll : polls) {
            items.add(buildPollResponse(poll, userVotes));
        }
        return items;
    }

    private Map<String, Object> buildPollResponse(Poll poll, Map<Long, Long> userVotes) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", poll.getId());
        data.put("title", poll.getTitle());
        data.put("description", poll.getDescription());
        data.put("status", poll.getStatus());
        data.put("expiresAt", poll.getExpiresAt());
        data.put("createdAt", poll.getCreatedAt());

        int totalVotes = poll.getOptions().stream()
                .mapToInt(PollOption::getVotesCount)
                .sum();
        data.put("totalVotes", totalVotes);

        Long userVoteOptionId = userVotes.get(poll.getId());
        data.put("isVotedByCurrentUser", userVoteOptionId != null);
        data.put("userVoteOptionId", userVoteOptionId);

        List<Map<String, Object>> options = new ArrayList<>();
        for (PollOption option : poll.getOptions()) {
            Map<String, Object> optionMap = new HashMap<>();
            optionMap.put("id", option.getId());
            optionMap.put("label", option.getLabel());
            optionMap.put("votes", option.getVotesCount());
            double percentage = totalVotes == 0 ? 0 : (option.getVotesCount() * 100.0) / totalVotes;
            optionMap.put("percentage", percentage);
            options.add(optionMap);
        }
        data.put("options", options);

        if (poll.getCreatedBy() != null) {
            Map<String, Object> createdBy = new HashMap<>();
            createdBy.put("id", poll.getCreatedBy().getId());
            createdBy.put("name", poll.getCreatedBy().getName());
            createdBy.put("avatarUrl", poll.getCreatedBy().getAvatarUrl());
            data.put("createdBy", createdBy);
        }

        return data;
    }

    private Map<String, Object> buildStatsResponse(PollStatsResult stats) {
        Map<String, Object> statsMap = new HashMap<>();
        if (stats == null) {
            statsMap.put("activeCount", 0);
            statsMap.put("closedCount", 0);
            statsMap.put("totalVotes", 0);
            statsMap.put("participationRate", 0);
            return statsMap;
        }
        statsMap.put("activeCount", stats.getActiveCount());
        statsMap.put("closedCount", stats.getClosedCount());
        statsMap.put("totalVotes", stats.getTotalVotes());
        statsMap.put("participationRate", stats.getParticipationRate());
        return statsMap;
    }
}


package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.entity.Team;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.service.AuthService;
import com.eska.motive.crew.ws.service.TeamService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teams")
@Log4j2
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTeams(
            @RequestHeader("Authorization") String token) throws ResourceNotFoundException {
        getCurrentUser(token); // Verify authentication
        
        List<Team> teams = teamService.getAllTeams();
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Teams retrieved successfully");
        response.put("error", false);
        response.put("data", buildTeamsResponse(teams));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTeamById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) throws ResourceNotFoundException {
        getCurrentUser(token); // Verify authentication
        
        Team team = teamService.getTeamById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Team retrieved successfully");
        response.put("error", false);
        response.put("data", buildTeamResponse(team));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }

    private List<Map<String, Object>> buildTeamsResponse(List<Team> teams) {
        List<Map<String, Object>> teamsList = new ArrayList<>();
        for (Team team : teams) {
            teamsList.add(buildTeamResponse(team));
        }
        return teamsList;
    }

    private Map<String, Object> buildTeamResponse(Team team) {
        Map<String, Object> teamMap = new HashMap<>();
        teamMap.put("id", team.getId());
        teamMap.put("name", team.getName());
        teamMap.put("description", team.getDescription());
        teamMap.put("avatarUrl", team.getAvatarUrl());
        teamMap.put("memberCount", team.getMemberCount());
        teamMap.put("rate", team.getRate());
        teamMap.put("isActive", team.getIsActive());
        teamMap.put("createdAt", team.getCreatedAt());
        teamMap.put("updatedAt", team.getUpdatedAt());
        
        if (team.getTeamLeader() != null) {
            Map<String, Object> leader = new HashMap<>();
            leader.put("id", team.getTeamLeader().getId());
            leader.put("name", team.getTeamLeader().getName());
            leader.put("email", team.getTeamLeader().getEmail());
            leader.put("avatarUrl", team.getTeamLeader().getAvatarUrl());
            teamMap.put("teamLeader", leader);
        }
        
        return teamMap;
    }
}


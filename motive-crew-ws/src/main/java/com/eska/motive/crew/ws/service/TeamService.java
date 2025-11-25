package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.entity.Team;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.repository.TeamRepository;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Team operations
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findByIsActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Team getTeamById(Long id) throws ResourceNotFoundException {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Team getTeamByName(String name) throws ResourceNotFoundException {
        return teamRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
    }

    @Transactional
    public void updateTeamMemberCount(Long teamId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        if (team != null) {
            long count = userRepository.countByTeamIdAndIsActiveTrue(teamId);
            team.setMemberCount((int) count);
            teamRepository.save(team);
        }
    }

    @Transactional
    public void updateAllTeamMemberCounts() {
        List<Team> teams = teamRepository.findAll();
        for (Team team : teams) {
            long count = userRepository.countByTeamIdAndIsActiveTrue(team.getId());
            team.setMemberCount((int) count);
        }
        teamRepository.saveAll(teams);
    }
}


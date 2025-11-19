package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserPreferences entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {

    Optional<UserPreferences> findByUser(User user);
}


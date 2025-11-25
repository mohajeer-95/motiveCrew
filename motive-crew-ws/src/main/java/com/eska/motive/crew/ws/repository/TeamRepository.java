package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Team entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

    boolean existsByName(String name);

    List<Team> findByIsActiveTrueOrderByNameAsc();

    @Query("SELECT t FROM Team t WHERE " +
           "(:search IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:isActive IS NULL OR t.isActive = :isActive)")
    List<Team> findByFilters(
            @Param("search") String search,
            @Param("isActive") Boolean isActive
    );
}


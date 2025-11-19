package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.MonthlyCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for MonthlyCollection entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface MonthlyCollectionRepository extends JpaRepository<MonthlyCollection, Long> {

    Optional<MonthlyCollection> findByYearAndMonth(Integer year, Integer month);

    boolean existsByYearAndMonth(Integer year, Integer month);
}


package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> findByFilters(
            @Param("search") String search,
            @Param("role") User.UserRole role,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    long countByRole(User.UserRole role);

    long countByIsActiveTrue();

    long countByTeamIdAndIsActiveTrue(Long teamId);
}


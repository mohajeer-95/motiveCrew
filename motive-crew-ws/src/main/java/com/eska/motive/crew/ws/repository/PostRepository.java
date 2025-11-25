package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Post entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Corporate feed (team_id is NULL)
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.team IS NULL ORDER BY p.createdAt DESC")
    Page<Post> findCorporatePosts(Pageable pageable);

    // Team feed (team_id is NOT NULL)
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.team.id = :teamId ORDER BY p.createdAt DESC")
    Page<Post> findTeamPosts(@Param("teamId") Long teamId, Pageable pageable);

    // All posts for a user's team
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND " +
           "(p.team.id = :teamId OR p.team IS NULL) ORDER BY p.createdAt DESC")
    Page<Post> findPostsByUserTeam(@Param("teamId") Long teamId, Pageable pageable);

    // Posts by user
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.user.id = :userId ORDER BY p.createdAt DESC")
    Page<Post> findPostsByUser(@Param("userId") Long userId, Pageable pageable);

    // Count likes for a post
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId")
    long countLikesByPostId(@Param("postId") Long postId);

    // Count comments for a post
    @Query("SELECT COUNT(pc) FROM PostComment pc WHERE pc.post.id = :postId AND pc.deletedAt IS NULL")
    long countCommentsByPostId(@Param("postId") Long postId);
}


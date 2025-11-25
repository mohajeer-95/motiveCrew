package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Post;
import com.eska.motive.crew.ws.entity.PostLike;
import com.eska.motive.crew.ws.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PostLike entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);

    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId")
    List<PostLike> findByPostId(@Param("postId") Long postId);

    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user.id = :userId")
    List<Long> findLikedPostIdsByUserId(@Param("userId") Long userId);

    long countByPost(Post post);
}


package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Post;
import com.eska.motive.crew.ws.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PostComment entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.deletedAt IS NULL ORDER BY pc.createdAt ASC")
    Page<PostComment> findByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.deletedAt IS NULL ORDER BY pc.createdAt ASC")
    List<PostComment> findAllByPostId(@Param("postId") Long postId);

    long countByPostAndDeletedAtIsNull(Post post);
}


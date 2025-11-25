package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreateCommentRequest;
import com.eska.motive.crew.ws.dto.request.CreatePostRequest;
import com.eska.motive.crew.ws.entity.Post;
import com.eska.motive.crew.ws.entity.PostComment;
import com.eska.motive.crew.ws.entity.PostLike;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.repository.PostCommentRepository;
import com.eska.motive.crew.ws.repository.PostLikeRepository;
import com.eska.motive.crew.ws.repository.PostRepository;
import com.eska.motive.crew.ws.repository.TeamRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for Post operations (Corporate/Team Feed)
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public Page<Post> getCorporateFeed(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return postRepository.findCorporatePosts(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> getTeamFeed(Long teamId, int page, int size) throws ResourceNotFoundException, ValidationException {
        if (teamId == null) {
            throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
        }
        teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
        
        Pageable pageable = PageRequest.of(page - 1, size);
        return postRepository.findTeamPosts(teamId, pageable);
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) throws ResourceNotFoundException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
        
        if (post.getDeletedAt() != null) {
            throw new ResourceNotFoundException(StatusCode.NOT_FOUND);
        }
        
        return post;
    }

    @Transactional
    public Post createPost(User user, CreatePostRequest request) throws ValidationException, ResourceNotFoundException {
        if (user == null) {
            throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
        }

        if (!StringUtils.hasText(request.getText()) && !StringUtils.hasText(request.getImageUrl())) {
            throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
        }

        Post.FeedType feedType = Post.FeedType.CORPORATE;
        if (StringUtils.hasText(request.getFeedType())) {
            try {
                feedType = Post.FeedType.valueOf(request.getFeedType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }
        }

        Post.PostBuilder postBuilder = Post.builder()
                .user(user)
                .text(StringUtils.hasText(request.getText()) ? request.getText().trim() : null)
                .imageUrl(StringUtils.hasText(request.getImageUrl()) ? request.getImageUrl().trim() : null)
                .feedType(feedType);

        // If team feed, set the team
        if (feedType == Post.FeedType.TEAM) {
            if (user.getTeam() == null) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }
            postBuilder.team(user.getTeam());
        }

        return postRepository.save(postBuilder.build());
    }

    @Transactional
    public void toggleLike(User user, Long postId) throws ResourceNotFoundException, ValidationException {
        Post post = getPostById(postId);

        PostLike existingLike = postLikeRepository.findByPostAndUser(post, user).orElse(null);
        
        if (existingLike != null) {
            // Unlike
            postLikeRepository.delete(existingLike);
        } else {
            // Like
            PostLike like = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(like);
        }
    }

    @Transactional
    public PostComment createComment(User user, Long postId, CreateCommentRequest request) 
            throws ResourceNotFoundException, ValidationException {
        Post post = getPostById(postId);

        PostComment comment = PostComment.builder()
                .post(post)
                .user(user)
                .text(request.getText().trim())
                .build();

        return postCommentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(User user, Long commentId) throws ResourceNotFoundException, ValidationException {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != User.UserRole.ADMIN) {
            throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
        }

        comment.setDeletedAt(LocalDateTime.now());
        postCommentRepository.save(comment);
    }

    @Transactional
    public void deletePost(User user, Long postId) throws ResourceNotFoundException, ValidationException {
        Post post = getPostById(postId);

        if (!post.getUser().getId().equals(user.getId()) && user.getRole() != User.UserRole.ADMIN) {
            throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
        }

        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<PostComment> getPostComments(Long postId, int page, int size) throws ResourceNotFoundException {
        getPostById(postId); // Verify post exists
        Pageable pageable = PageRequest.of(page - 1, size);
        return postCommentRepository.findByPostId(postId, pageable);
    }

    @Transactional(readOnly = true)
    public List<PostComment> getAllPostComments(Long postId) throws ResourceNotFoundException {
        getPostById(postId); // Verify post exists
        return postCommentRepository.findAllByPostId(postId);
    }

    @Transactional(readOnly = true)
    public Set<Long> getLikedPostIds(User user) {
        return postLikeRepository.findLikedPostIdsByUserId(user.getId())
                .stream()
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public long getLikesCount(Long postId) {
        return postLikeRepository.countByPost(postRepository.findById(postId).orElse(null));
    }

    @Transactional(readOnly = true)
    public long getCommentsCount(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        return post != null ? postCommentRepository.countByPostAndDeletedAtIsNull(post) : 0;
    }
}


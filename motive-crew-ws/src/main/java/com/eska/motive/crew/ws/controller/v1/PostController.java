package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreateCommentRequest;
import com.eska.motive.crew.ws.dto.request.CreatePostRequest;
import com.eska.motive.crew.ws.entity.Post;
import com.eska.motive.crew.ws.entity.PostComment;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AuthService;
import com.eska.motive.crew.ws.service.PostService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/v1/posts")
@Log4j2
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private AuthService authService;

    @GetMapping("/corporate")
    public ResponseEntity<Map<String, Object>> getCorporateFeed(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) throws ResourceNotFoundException {
        User currentUser = getCurrentUser(token);
        
        Page<Post> postsPage = postService.getCorporateFeed(page, size);
        Set<Long> likedPostIds = postService.getLikedPostIds(currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Corporate feed retrieved successfully");
        response.put("error", false);
        response.put("data", buildFeedResponse(postsPage, likedPostIds, currentUser));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/team")
    public ResponseEntity<Map<String, Object>> getTeamFeed(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) 
            throws ResourceNotFoundException, ValidationException {
        User currentUser = getCurrentUser(token);
        
        if (currentUser.getTeam() == null) {
            throw new ValidationException("User must belong to a team to view team feed");
        }
        
        Page<Post> postsPage = postService.getTeamFeed(currentUser.getTeam().getId(), page, size);
        Set<Long> likedPostIds = postService.getLikedPostIds(currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Team feed retrieved successfully");
        response.put("error", false);
        response.put("data", buildFeedResponse(postsPage, likedPostIds, currentUser));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> getPostById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long postId) throws ResourceNotFoundException {
        User currentUser = getCurrentUser(token);
        
        Post post = postService.getPostById(postId);
        Set<Long> likedPostIds = postService.getLikedPostIds(currentUser);
        List<PostComment> comments = postService.getAllPostComments(postId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Post retrieved successfully");
        response.put("error", false);
        response.put("data", buildPostDetailResponse(post, likedPostIds, comments, currentUser));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreatePostRequest request)
            throws ResourceNotFoundException, ValidationException {
        User currentUser = getCurrentUser(token);
        
        Post post = postService.createPost(currentUser, request);
        Set<Long> likedPostIds = postService.getLikedPostIds(currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Post created successfully");
        response.put("error", false);
        response.put("data", buildPostResponse(post, likedPostIds, currentUser));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @RequestHeader("Authorization") String token,
            @PathVariable Long postId)
            throws ResourceNotFoundException, ValidationException {
        User currentUser = getCurrentUser(token);
        
        postService.toggleLike(currentUser, postId);
        Post post = postService.getPostById(postId);
        Set<Long> likedPostIds = postService.getLikedPostIds(currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Like toggled successfully");
        response.put("error", false);
        response.put("data", buildPostResponse(post, likedPostIds, currentUser));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Map<String, Object>> createComment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request)
            throws ResourceNotFoundException, ValidationException {
        User currentUser = getCurrentUser(token);
        
        PostComment comment = postService.createComment(currentUser, postId, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Comment created successfully");
        response.put("error", false);
        response.put("data", buildCommentResponse(comment));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getPostComments(
            @RequestHeader("Authorization") String token,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size)
            throws ResourceNotFoundException {
        getCurrentUser(token); // Verify authentication
        
        Page<PostComment> commentsPage = postService.getPostComments(postId, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Comments retrieved successfully");
        response.put("error", false);
        response.put("data", buildCommentsResponse(commentsPage));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(
            @RequestHeader("Authorization") String token,
            @PathVariable Long postId)
            throws ResourceNotFoundException, ValidationException {
        User currentUser = getCurrentUser(token);
        
        postService.deletePost(currentUser, postId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Post deleted successfully");
        response.put("error", false);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long commentId)
            throws ResourceNotFoundException, ValidationException {
        User currentUser = getCurrentUser(token);
        
        postService.deleteComment(currentUser, commentId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Comment deleted successfully");
        response.put("error", false);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }

    private Map<String, Object> buildFeedResponse(Page<Post> postsPage, Set<Long> likedPostIds, User currentUser) {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> posts = new ArrayList<>();
        
        for (Post post : postsPage.getContent()) {
            posts.add(buildPostResponse(post, likedPostIds, currentUser));
        }
        
        data.put("items", posts);
        data.put("pagination", buildPaginationResponse(postsPage));
        
        return data;
    }

    private Map<String, Object> buildPostResponse(Post post, Set<Long> likedPostIds, User currentUser) {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("id", post.getId());
        postMap.put("text", post.getText());
        postMap.put("imageUrl", post.getImageUrl());
        postMap.put("feedType", post.getFeedType().name().toLowerCase());
        postMap.put("createdAt", post.getCreatedAt());
        
        // User info
        if (post.getUser() != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", post.getUser().getId());
            userInfo.put("name", post.getUser().getName());
            userInfo.put("avatarUrl", post.getUser().getAvatarUrl());
            if (post.getUser().getTeam() != null) {
                userInfo.put("teamName", post.getUser().getTeam().getName());
            }
            userInfo.put("position", post.getUser().getPosition());
            postMap.put("userName", post.getUser().getName());
            postMap.put("userId", post.getUser().getId());
            postMap.put("userAvatar", post.getUser().getAvatarUrl());
            postMap.put("user", userInfo);
        }
        
        // Team info
        if (post.getTeam() != null) {
            postMap.put("teamName", post.getTeam().getName());
        }
        
        // Likes and comments count
        long likesCount = postService.getLikesCount(post.getId());
        long commentsCount = postService.getCommentsCount(post.getId());
        postMap.put("likesCount", likesCount);
        postMap.put("commentsCount", commentsCount);
        postMap.put("isLiked", likedPostIds.contains(post.getId()));
        
        return postMap;
    }

    private Map<String, Object> buildPostDetailResponse(Post post, Set<Long> likedPostIds, 
                                                         List<PostComment> comments, User currentUser) {
        Map<String, Object> postMap = buildPostResponse(post, likedPostIds, currentUser);
        
        // Add comments
        List<Map<String, Object>> commentsList = new ArrayList<>();
        for (PostComment comment : comments) {
            commentsList.add(buildCommentResponse(comment));
        }
        postMap.put("comments", commentsList);
        
        return postMap;
    }

    private Map<String, Object> buildCommentResponse(PostComment comment) {
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("id", comment.getId());
        commentMap.put("text", comment.getText());
        commentMap.put("createdAt", comment.getCreatedAt());
        
        if (comment.getUser() != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", comment.getUser().getId());
            userInfo.put("name", comment.getUser().getName());
            userInfo.put("avatarUrl", comment.getUser().getAvatarUrl());
            commentMap.put("userName", comment.getUser().getName());
            commentMap.put("userId", comment.getUser().getId());
            commentMap.put("userAvatar", comment.getUser().getAvatarUrl());
            commentMap.put("user", userInfo);
        }
        
        return commentMap;
    }

    private Map<String, Object> buildCommentsResponse(Page<PostComment> commentsPage) {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> comments = new ArrayList<>();
        
        for (PostComment comment : commentsPage.getContent()) {
            comments.add(buildCommentResponse(comment));
        }
        
        data.put("items", comments);
        data.put("pagination", buildPaginationResponse(commentsPage));
        
        return data;
    }

    private Map<String, Object> buildPaginationResponse(Page<?> page) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page.getNumber() + 1);
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("totalItems", page.getTotalElements());
        pagination.put("pageSize", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        return pagination;
    }
}


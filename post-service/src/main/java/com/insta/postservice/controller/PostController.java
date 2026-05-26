package com.insta.postservice.controller;
import com.insta.postservice.dto.*;
import com.insta.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/posts") @RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    @PostMapping
    public ResponseEntity<PostResponse> create(
            @RequestHeader("X-User-Id") Long userId, @Valid @RequestBody PostRequest req) {
        return ResponseEntity.ok(postService.createPost(userId, req));
    }
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> update(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long postId, @RequestBody PostRequest req) {
        return ResponseEntity.ok(postService.updatePost(userId, postId, req));
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-User-Id") Long userId, @PathVariable Long postId) {
        postService.deletePost(userId, postId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(postService.searchPosts(q));
    }
}

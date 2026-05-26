package com.insta.likeservice.controller;
import com.insta.likeservice.dto.LikeResponse;
import com.insta.likeservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/likes") @RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    @PostMapping("/{postId}")
    public ResponseEntity<LikeResponse> like(
            @PathVariable Long postId, @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(likeService.likePost(postId, userId));
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<LikeResponse> unlike(
            @PathVariable Long postId, @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(likeService.unlikePost(postId, userId));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<LikeResponse> getLikes(
            @PathVariable Long postId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(likeService.getLikes(postId, userId));
    }
}

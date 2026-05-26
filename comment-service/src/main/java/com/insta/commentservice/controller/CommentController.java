package com.insta.commentservice.controller;
import com.insta.commentservice.dto.*;
import com.insta.commentservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/comments") @RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId, @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CommentRequest req) {
        return ResponseEntity.ok(commentService.addComment(postId, userId, req));
    }
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId, @RequestHeader("X-User-Id") Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}

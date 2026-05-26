package com.insta.commentservice.service;
import com.insta.commentservice.dto.*;
import com.insta.commentservice.model.Comment;
import com.insta.commentservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service @RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    public CommentResponse addComment(Long postId, Long userId, CommentRequest req) {
        Comment c = Comment.builder().postId(postId).userId(userId).content(req.getContent()).build();
        return mapToDto(commentRepository.save(c));
    }
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }
    public void deleteComment(Long commentId, Long userId) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!c.getUserId().equals(userId)) throw new IllegalArgumentException("Unauthorized");
        commentRepository.delete(c);
    }
    private CommentResponse mapToDto(Comment c) {
        CommentResponse dto = new CommentResponse();
        dto.setId(c.getId()); dto.setPostId(c.getPostId()); dto.setUserId(c.getUserId());
        dto.setContent(c.getContent()); dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}

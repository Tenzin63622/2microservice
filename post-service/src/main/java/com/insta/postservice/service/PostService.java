package com.insta.postservice.service;
import com.insta.postservice.dto.*;
import com.insta.postservice.exception.ResourceNotFoundException;
import com.insta.postservice.model.Post;
import com.insta.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service @RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponse createPost(Long userId, PostRequest req) {
        Post post = Post.builder().userId(userId).imageUrl(req.getImageUrl())
                .caption(req.getCaption()).hashtags(req.getHashtags()).build();
        return mapToDto(postRepository.save(post));
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByUser(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public PostResponse getPost(Long postId) {
        return mapToDto(postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found")));
    }

    public PostResponse updatePost(Long userId, Long postId, PostRequest req) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        if (!post.getUserId().equals(userId)) throw new IllegalArgumentException("Unauthorized");
        if (req.getCaption() != null) post.setCaption(req.getCaption());
        if (req.getHashtags() != null) post.setHashtags(req.getHashtags());
        return mapToDto(postRepository.save(post));
    }

    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        if (!post.getUserId().equals(userId)) throw new IllegalArgumentException("Unauthorized");
        postRepository.delete(post);
    }

    public List<PostResponse> searchPosts(String query) {
        List<Post> results = postRepository.searchByCaption(query);
        results.addAll(postRepository.searchByHashtag(query));
        return results.stream().distinct().map(this::mapToDto).collect(Collectors.toList());
    }

    private PostResponse mapToDto(Post p) {
        PostResponse dto = new PostResponse();
        dto.setId(p.getId()); dto.setUserId(p.getUserId()); dto.setImageUrl(p.getImageUrl());
        dto.setCaption(p.getCaption()); dto.setHashtags(p.getHashtags());
        dto.setCreatedAt(p.getCreatedAt()); dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }
}

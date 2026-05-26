package com.insta.likeservice.service;
import com.insta.likeservice.dto.LikeResponse;
import com.insta.likeservice.model.Like;
import com.insta.likeservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    public LikeResponse likePost(Long postId, Long userId) {
        if (likeRepository.existsByPostIdAndUserId(postId, userId))
            throw new IllegalArgumentException("Already liked");
        likeRepository.save(Like.builder().postId(postId).userId(userId).build());
        return buildResponse(postId, userId);
    }
    @Transactional
    public LikeResponse unlikePost(Long postId, Long userId) {
        if (!likeRepository.existsByPostIdAndUserId(postId, userId))
            throw new IllegalArgumentException("Not liked yet");
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        return buildResponse(postId, userId);
    }
    public LikeResponse getLikes(Long postId, Long userId) {
        return buildResponse(postId, userId);
    }
    private LikeResponse buildResponse(Long postId, Long userId) {
        LikeResponse r = new LikeResponse();
        r.setPostId(postId);
        r.setLikeCount(likeRepository.countByPostId(postId));
        r.setLikedByCurrentUser(userId != null && likeRepository.existsByPostIdAndUserId(postId, userId));
        return r;
    }
}

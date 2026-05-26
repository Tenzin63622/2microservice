package com.insta.followservice.service;
import com.insta.followservice.dto.FollowResponse;
import com.insta.followservice.model.Follow;
import com.insta.followservice.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service @RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    public FollowResponse follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) throw new IllegalArgumentException("Cannot follow yourself");
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId))
            throw new IllegalArgumentException("Already following");
        followRepository.save(Follow.builder().followerId(followerId).followingId(followingId).build());
        return buildResponse(followingId, followerId);
    }
    @Transactional
    public FollowResponse unfollow(Long followerId, Long followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId))
            throw new IllegalArgumentException("Not following");
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        return buildResponse(followingId, followerId);
    }
    public FollowResponse getFollowStats(Long targetUserId, Long currentUserId) {
        return buildResponse(targetUserId, currentUserId);
    }
    public List<Long> getFollowerIds(Long userId) {
        return followRepository.findByFollowingId(userId).stream()
                .map(Follow::getFollowerId).collect(Collectors.toList());
    }
    public List<Long> getFollowingIds(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(Follow::getFollowingId).collect(Collectors.toList());
    }
    private FollowResponse buildResponse(Long targetUserId, Long currentUserId) {
        FollowResponse r = new FollowResponse();
        r.setTargetUserId(targetUserId);
        r.setFollowersCount(followRepository.countByFollowingId(targetUserId));
        r.setFollowingCount(followRepository.countByFollowerId(targetUserId));
        r.setFollowing(currentUserId != null && followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId));
        return r;
    }
}

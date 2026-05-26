package com.insta.followservice.dto;
import lombok.Data;
@Data
public class FollowResponse {
    private Long targetUserId;
    private long followersCount;
    private long followingCount;
    private boolean isFollowing;
}

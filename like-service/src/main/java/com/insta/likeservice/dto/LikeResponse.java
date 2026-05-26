package com.insta.likeservice.dto;
import lombok.Data;
@Data
public class LikeResponse {
    private Long postId;
    private Long likeCount;
    private boolean likedByCurrentUser;
}

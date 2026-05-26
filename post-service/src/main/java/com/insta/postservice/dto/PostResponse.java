package com.insta.postservice.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class PostResponse {
    private Long id;
    private Long userId;
    private String imageUrl;
    private String caption;
    private List<String> hashtags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

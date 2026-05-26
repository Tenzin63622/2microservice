package com.insta.postservice.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;
@Data
public class PostRequest {
    @NotBlank(message = "Image URL is required") private String imageUrl;
    private String caption;
    private List<String> hashtags;
}

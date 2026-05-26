package com.insta.userservice.dto;
import lombok.*;
@Data @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private Long userId;
}

package com.insta.userservice.dto;
import lombok.Data;
@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String bio;
    private String profilePictureUrl;
    private String website;
}

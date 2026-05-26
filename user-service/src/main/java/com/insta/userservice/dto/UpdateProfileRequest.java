package com.insta.userservice.dto;
import lombok.Data;
@Data
public class UpdateProfileRequest {
    private String fullName;
    private String bio;
    private String profilePictureUrl;
    private String website;
}

package com.insta.userservice.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required") @Size(min = 3, max = 30) private String username;
    @NotBlank(message = "Email is required") @Email(message = "Email must be valid") private String email;
    @NotBlank(message = "Password is required") @Size(min = 6) private String password;
    private String fullName;
}

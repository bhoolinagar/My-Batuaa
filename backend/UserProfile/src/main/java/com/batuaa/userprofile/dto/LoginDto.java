package com.batuaa.userprofile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String emailId;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    // getters & setters

    public String getEmailId() { return emailId; }

    public void setEmailId(String emailId) { this.emailId = emailId;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message ="email is required")
    private String email;
    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message="password is required")
    @Size(min = 6,max=8,message = "size of password should be between 6 to 8 characters")
    private String password;

}

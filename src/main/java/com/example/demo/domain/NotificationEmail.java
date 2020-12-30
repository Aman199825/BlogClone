package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEmail {
    @NotBlank
    private String subject;
    @NotBlank
    private String recipient;
    private String body;
}

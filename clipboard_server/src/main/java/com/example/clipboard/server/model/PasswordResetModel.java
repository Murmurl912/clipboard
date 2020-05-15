package com.example.clipboard.server.model;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public class PasswordResetModel {
    @NotBlank
    public String username;
    @NotBlank
    public String password;
    @NotBlank
    public String code;
    @NotBlank
    public String id;
}

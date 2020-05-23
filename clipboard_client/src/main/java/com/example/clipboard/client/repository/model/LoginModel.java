package com.example.clipboard.client.repository.model;

import javax.validation.constraints.NotBlank;

public class LoginModel {
    @NotBlank
    public String username;
    @NotBlank
    public String password;
}

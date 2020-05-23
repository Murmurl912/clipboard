package com.example.clipboard.server.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Valid
public class SignInModel {
    @NotBlank
    public String username;
    public String email;
    @NotBlank
    public String password;
}

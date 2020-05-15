package com.example.clipboard.server.model;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Validated
public class SignUpModel {
    @NotBlank
    @Size(min = 3, max = 32)
    public String username;
    @NotBlank
    public String password;
    public String avatar;
    @NotBlank
    public String email;

}

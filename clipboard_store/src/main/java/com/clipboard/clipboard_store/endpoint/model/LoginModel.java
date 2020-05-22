package com.clipboard.clipboard_store.endpoint.model;

import javax.validation.constraints.NotBlank;

public class LoginModel {
    @NotBlank
    public String username;
    @NotBlank
    public String password;
}

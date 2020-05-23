package com.example.clipboard.server.model;

import java.util.Map;

public interface AuthenticationModel {

    public static final String username = "username";
    public static final String password = "password";
    public static final String timestamp = "timestamp";
    public static final String random = "random";
    public static final String hash = "hash";

    public AuthenticationType getType();

    public Map<String, Object> getData();

    public enum AuthenticationType {
        AUTHENTICATION_TYPE_PASSWORD,
        AUTHENTICATION_TYPE_CHALLENGE,
    }
}

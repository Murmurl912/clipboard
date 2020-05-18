package com.example.clipboard.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

@SpringBootApplication
public class ClipboardServerApplication {


    private static final Logger logger = LoggerFactory.getLogger(ClipboardServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ClipboardServerApplication.class, args);
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            ReactiveAuthenticationManager manager,
                                                            ServerSecurityContextRepository repository) {
        http
                .authorizeExchange()
                .pathMatchers(HttpMethod.PUT, "/account").permitAll() // sign up
                .pathMatchers(HttpMethod.POST, "/account").permitAll() // sign in
                .pathMatchers(HttpMethod.GET, "/account/*/activation").permitAll()  // account activation
                .pathMatchers(HttpMethod.POST, "/account/*/activation").permitAll() // account activation
                .pathMatchers(HttpMethod.GET, "/account/*/password").permitAll() // password reset
                .pathMatchers(HttpMethod.POST, "/account/*/password").permitAll() // password reset
                .anyExchange().permitAll() // todo disable security
                .and()
                .csrf()
                .disable()
                .authenticationManager(manager)
                .securityContextRepository(repository);

        return http.build();
    }
}

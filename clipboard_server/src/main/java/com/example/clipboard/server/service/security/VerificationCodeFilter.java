package com.example.clipboard.server.service.security;

import com.example.clipboard.server.entity.temp.VerificationCode;
import com.example.clipboard.server.service.reactive.VerificationCodeReactiveService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@ControllerAdvice
public class VerificationCodeFilter implements WebFilter {

    @Autowired
    private VerificationCodeReactiveService service;

    public VerificationCodeFilter() {

    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        if(request.getMethod() != HttpMethod.POST) {
            return webFilterChain.filter(serverWebExchange);
        }

        RequestPath path = request.getPath();
        String pathString = path.value();
        if(pathString.startsWith("/account/") && (pathString.endsWith("/activation") || pathString.endsWith("/email"))) {
            HttpHeaders httpHeaders = request.getHeaders();
            if(!httpHeaders.containsKey("verification_code")) {
                return Mono.empty();
            }

            String code = httpHeaders.getFirst("verification_code");

            if(StringUtils.isEmpty(code)) {
                return Mono.empty();
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                VerificationCode c = mapper.readValue(new String(Base64Utils.decodeFromString(code), StandardCharsets.UTF_8), VerificationCode.class);
                VerificationCode.VerificationCodeType type = null;

                if(pathString.endsWith("/activation")) {
                    type = VerificationCode.VerificationCodeType.VERIFICATION_CODE_TYPE_ACTIVATION;
                } else if(pathString.endsWith("/password")) {
                    type = VerificationCode.VerificationCodeType.VERIFICATION_CODE_TYPE_PASSWORD;
                } else if(pathString.endsWith("/email")) {
                    type = VerificationCode.VerificationCodeType.VERIFICATION_CODE_TYPE_EMAIL;
                } else {
                    type = VerificationCode.VerificationCodeType.VERIFICATION_CODE_TYPE_LOGIN;
                }
                return service.verify(c.id, c.code, type)
                        .flatMap(bool -> {
                            if(bool) {
                                return webFilterChain.filter(serverWebExchange);
                            } else {
                                return Mono.empty();
                            }
                        });

            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }


        } else {
            return webFilterChain.filter(serverWebExchange);
        }
    }
}

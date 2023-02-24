package com.example.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class DemoHandler {
    public Mono<ServerResponse> onGet(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .doOnError(Throwable::printStackTrace)
                .flatMap(securityContext -> {
                    var principal = (Jwt) securityContext.getAuthentication().getPrincipal();
                    log.info(principal.getTokenValue());
                    return ServerResponse.ok().bodyValue(principal.getTokenValue());
                });
    }
}

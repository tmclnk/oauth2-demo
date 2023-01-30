package com.example.rs.impersonation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@Slf4j
public class ImpersonationController {

    @Autowired
    private ImpersonationService impersonationService;

    @PutMapping("/impersonation/{username}")
    public Mono<? extends Map> put(@PathVariable("username") String username) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .doOnError(Throwable::printStackTrace)
                .doOnSuccess(principal -> impersonationService.prepareImpersonation(principal, username))
                .doOnSuccess(principal -> log.info("Prepared {} to impersonate {}", principal, username))
                .flatMap(principal -> Mono.just(Map.of(principal, username)));
    }

    @GetMapping("/impersonation")
    public Mono<? extends Map> get() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .doOnError(Throwable::printStackTrace)
                .flatMap(principal -> {
                    var username = impersonationService.getImpersonation(principal);
                    if (username != null) {
                        return Mono.just(Map.of(principal, username));
                    } else {
                        return Mono.just(Map.of());
                    }
                });
    }

}

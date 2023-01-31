package com.example.rs.impersonation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ImpersonationHandler {

    @Autowired
    private ImpersonationService impersonationService;

    public Mono<ServerResponse> put(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .doOnError(Throwable::printStackTrace)
                .flatMap(securityContext-> {
                    var principal = securityContext.getAuthentication().getName();
                    var agilityUsername = request.pathVariable("agilityUsername");
                    impersonationService.prepareImpersonation(principal, agilityUsername);
                    var imp = impersonationService.getImpersonation(principal);
                    return ServerResponse.ok().bodyValue(imp);
                });
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .doOnError(Throwable::printStackTrace)
                .flatMap(principal -> {
                    var impersonation= impersonationService.getImpersonation(principal);
                    if (impersonation != null) {
                        return ServerResponse.ok().bodyValue(impersonation);
                    } else {
                        return ServerResponse.noContent().build();
                    }
                });
    }

}

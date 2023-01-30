package com.example.rs.impersonation;

import com.example.model.Impersonation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ImpersonationController {

    @Autowired
    private ImpersonationService impersonationService;

    @PutMapping("/impersonation/{username}")
    public Mono<Impersonation> put(@PathVariable("username") String username) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .doOnError(Throwable::printStackTrace)
                .doOnSuccess(principal -> impersonationService.prepareImpersonation(principal, username))
                .doOnSuccess(principal -> log.info("Prepared {} to impersonate {}", principal, username))
                .flatMap(principal -> {
                    return Mono.just(impersonationService.getImpersonation(principal));
                });
    }

    @GetMapping("/impersonation")
    public Mono<Impersonation> get() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .doOnError(Throwable::printStackTrace)
                .flatMap(principal -> {
                    var impersonation= impersonationService.getImpersonation(principal);
                    if (impersonation != null) {
                        return Mono.just(impersonation);
                    } else {
                        return Mono.empty();
                    }
                });
    }

}

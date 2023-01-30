package com.example.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Just dump auth data as JSON.
 */
@RestController
@Slf4j
public class OAuthController {

    @GetMapping(value = "/")
    public Mono<Void> hello(@AuthenticationPrincipal OidcUser principal, ServerHttpResponse response) throws URISyntaxException {
    response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        response.getHeaders().setLocation(new URI("/impersonation"));
        return Mono.empty();
    }


    @GetMapping(value = "/token", produces = "text/plain")
    public Mono<String> getRawToken(@AuthenticationPrincipal OidcUser principal) {
        return Mono.just(principal.getIdToken().getTokenValue());
    }
}

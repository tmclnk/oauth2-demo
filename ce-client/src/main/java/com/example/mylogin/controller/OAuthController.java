package com.example.mylogin.controller;

import lombok.extern.slf4j.Slf4j;
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

/**
 * Just dump auth data as JSON.
 */
@RestController
@Slf4j
public class OAuthController {
    @GetMapping("/oidc")
    public Mono<OidcUser> getOidcUserPrincipal(@AuthenticationPrincipal OidcUser principal) {
        return Mono.just(principal);
    }

    @GetMapping("/token")
    public Mono<OAuth2AuthenticationToken> getToken(OAuth2AuthenticationToken token) {
        return Mono.just(token);
    }

    @GetMapping("/oauth2user")
    public Mono<OAuth2User> index(@AuthenticationPrincipal OAuth2User principal) {
        return Mono.just(principal);
    }

    @GetMapping("/")
    public void index(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create("/person"));
    }

}

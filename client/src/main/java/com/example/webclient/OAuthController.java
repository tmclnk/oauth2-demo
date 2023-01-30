package com.example.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Just dump auth data as JSON.
 */
@RestController
@Slf4j
public class OAuthController {

    @GetMapping(value = "/")
    public Mono<String> hello(@AuthenticationPrincipal OidcUser principal) {
        var token = principal.getIdToken();
        var sub = token.getSubject();
        var impersonate = token.getClaim("impersonate");
        return Mono.just(String.format("Hello %s, I see you are impersonating %s", sub, impersonate));
    }


    @GetMapping(value = "/id", produces = "application/text")
    public Mono<String> getRawToken(@AuthenticationPrincipal OidcUser principal) {
        return Mono.just(principal.getIdToken().getTokenValue());
    }
    @GetMapping(value = "/id", produces = "application/json")
    public Mono<Map> getJsonToken(@AuthenticationPrincipal OidcUser principal) {
        return Mono.just(principal.getAttributes());
    }

    @GetMapping("/token")
    public Mono<OAuth2AuthenticationToken> getToken(OAuth2AuthenticationToken token) {
    token.getPrincipal().getAttributes();
        return Mono.just(token);
    }

    @GetMapping("/oauth2user")
    public Mono<OAuth2User> index(@AuthenticationPrincipal OAuth2User principal) {
        return Mono.just(principal);
    }
}

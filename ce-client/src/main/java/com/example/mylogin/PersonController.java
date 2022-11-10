package com.example.mylogin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@RestController
@Slf4j
public class PersonController {

    private final WebClient oauthWebClient;

    @Autowired
    public PersonController(WebClient webClient) {
        this.oauthWebClient = webClient;
    }

    @GetMapping("/person")
    public Mono<HashMap> getPerson(@RegisteredOAuth2AuthorizedClient("cloudentity") OAuth2AuthorizedClient authorizedClient) {
        log.trace("Making person call with {}...", authorizedClient.getAccessToken().getTokenValue());
        var result = oauthWebClient
                .get()
                .uri("https://spring.users.runpaste.com/users/123")
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(HashMap.class);
        return result;
    }
}

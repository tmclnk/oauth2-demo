package com.example.mylogin.controller;

import com.example.mylogin.UserServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class PersonController {

    private final WebClient oauthWebClient;

    private final UserServiceProperties userServiceProperties;

    public PersonController(WebClient oauthWebClient, UserServiceProperties userServiceProperties) {
        this.oauthWebClient = oauthWebClient;
        this.userServiceProperties = userServiceProperties;
    }

    /**
     * Calls an OAuth2-protected Resource Server ({@link UserServiceProperties#getBaseUri()})
     * and slightly munges the result.
     */
    @GetMapping("/person/{id}")
    public Mono<? extends Map> getPerson(@RegisteredOAuth2AuthorizedClient("cloudentity") OAuth2AuthorizedClient authorizedClient, @PathVariable("id") String id) {
        // It's probably best to not put this sort of thing directly in a Controller.
        var uri = UriComponentsBuilder.fromHttpUrl(userServiceProperties.getBaseUri())
                .pathSegment("users", "{user-id}")
                .buildAndExpand(id)
                .toUri();
        return oauthWebClient
                .get()
                .uri(uri)
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(HashMap.class)
                .map(userData -> {
                    var m = new HashMap<String, Object>();
                    m.put("hello", "world");
                    m.put("userData", userData);
                    return m;
                });
    }
}

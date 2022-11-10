package com.example.mylogin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class PersonController {

    private final WebClient oauthWebClient;

    @Autowired
    public PersonController(WebClient webClient) {
        this.oauthWebClient = webClient;
    }

    @GetMapping("/person")
    public Map getPerson(@RegisteredOAuth2AuthorizedClient("cloudentity") OAuth2AuthorizedClient authorizedClient) {
        log.trace("Making person call with {}...", authorizedClient.getAccessToken().getTokenValue());
        var result = oauthWebClient
                .get()
                .uri("https://spring.users.runpaste.com/users/123")
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(HashMap.class)
                .block();

        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("hello", "world");
        wrapper.put("resourceServerResponse", result);
        return wrapper;
    }
}

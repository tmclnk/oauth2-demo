package com.example.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
@Slf4j
@Component
public class DemoClientHandler {

    private final WebClient webClient;
    private final ApplicationProperties properties;

    @Autowired
    ReactiveOAuth2AuthorizedClientService clientService;

    public DemoClientHandler(WebClient webClient, ApplicationProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    @GetMapping

    public Mono<Map> onGet(@AuthenticationPrincipal OidcUser principal, @RegisteredOAuth2AuthorizedClient("myidp") OAuth2AuthorizedClient client) {
    long start = System.currentTimeMillis();
        return webClient.get().uri(properties.getResourceUrl())
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.info("Status Code {}", clientResponse.rawStatusCode());
                    return clientResponse.bodyToMono(Exception.class).map(Exception::new);
                })
                .bodyToMono(String.class)
                .flatMap(s -> {
                    var token = principal.getIdToken();
                    Map m = Map.of("sub", token.getSubject(),
                            "resourceServerResponse", s,
                            "milliseconds", System.currentTimeMillis() - start);
                    return Mono.just(m);
                });
    }

}

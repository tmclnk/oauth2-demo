package com.example.webclient.impersonation;

import com.example.model.Impersonation;
import com.example.webclient.ApplicationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;
import static org.springframework.web.util.UriComponentsBuilder.*;

@Controller
@Slf4j
@RequestMapping("/impersonation")
public class ImpersonationController {

    private final WebClient webClient;
    private final ApplicationProperties properties;

    public ImpersonationController(WebClient webClient, ApplicationProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    @GetMapping

    public Mono<String> onGet(Model model, @AuthenticationPrincipal OidcUser principal, @RegisteredOAuth2AuthorizedClient("myidp") OAuth2AuthorizedClient client) {
        var token = principal.getIdToken();
        log.info("Id Token subject that we're not using: {}", token.getSubject());

        // We hardcoded the 'sub' claim in the access token to 'bstan'.
        // Don't let this continue. But for now, don't directly
        // use attributes from the ID Token. Instead, rely on
        // what the remote side returns to us.
        return webClient.get().uri(properties.getImpersonationUrl())
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.info("Status Code {}", clientResponse.rawStatusCode());
                    return clientResponse.bodyToMono(Exception.class).map(Exception::new);
                })
                .bodyToMono(Impersonation.class)
                .defaultIfEmpty(new Impersonation())
                .doOnSuccess(i -> {
                    model.addAttribute("impersonation", i);
                    log.info("Saved impersonation {} -> {}", i.getSubject(), i.getAgilityUsername());
                }).flatMap(i -> Mono.just("impersonation_form.html"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<? extends String> onPost(Model model, Impersonation impersonationModel, @RegisteredOAuth2AuthorizedClient("myidp") OAuth2AuthorizedClient client) {
        var uri = fromHttpUrl(properties.getImpersonationUrl())
                .pathSegment("{username}")
                .buildAndExpand(impersonationModel.getAgilityUsername())
                .toUri();

        return webClient.put()
                .uri(uri)
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.info("Status Code {}", clientResponse.rawStatusCode());
                    return clientResponse.bodyToMono(Exception.class).map(Exception::new);
                })
                .bodyToMono(Impersonation.class)
                .doOnSuccess(i -> {
                    model.addAttribute("impersonation", i);
                    log.info("Saved impersonation {}", i);
                }).flatMap(i -> Mono.just("impersonation_form.html"));
    }
}

package com.example.webclient.impersonation;

import com.example.model.Impersonation;
import com.example.webclient.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

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

    public Mono<String> onGet(Model model, @AuthenticationPrincipal OidcUser principal) {
        var token = principal.getIdToken();
//        model.addAttribute("subject", token.getSubject());

        var impersonation = new Impersonation(token.getSubject(), token.getClaimAsString("impersonate"));
        model.addAttribute("impersonation", impersonation);
        return Mono.just("impersonation_form.html");
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<? extends String> onPost(Model model, Impersonation impersonationModel, @RegisteredOAuth2AuthorizedClient("myidp") OAuth2AuthorizedClient client) {
        var uri = fromHttpUrl(properties.getImpersonationUrl())
                .pathSegment("{username}")
                .buildAndExpand(impersonationModel.getTarget())
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
                }).then(Mono.just("impersonation_form.html"));


    }
}

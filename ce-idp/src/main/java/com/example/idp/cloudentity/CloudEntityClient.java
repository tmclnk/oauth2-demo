package com.example.idp.cloudentity;

import com.example.idp.web.LoginCommand;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class CloudEntityClient {

    @Autowired
    @Setter
    private CloudEntityProperties cloudEntityProperties;

    @Autowired
    @Setter
    private WebClient webClient;

    @Setter
    private Function<LoginCommand, Map<String, Object>> userAttributeService = loginCommand -> {
        var map = new HashMap<String, Object>();
        map.put("my_attribute", "HELLO WORLD!!!!!!!!!!!");
        return map;
    };

    /**
     * POST to the CloudEntity "/accept" url.
     * Uses a Bearer token obtained using client_credentials grant.
     * There's no refresh token here, we're fetching a new Bearer token every time.
     *
     * @return a redirect url (presumably to a consent screen)
     */
    public Mono<URI> accept(String subject, LoginCommand command) {
        var accept = new AcceptRequest(subject, command.getLoginState());

        // decorate with custom attributes
        accept.getAuthenticationContext().putAll(userAttributeService.apply(command));

        var acceptURI = cloudEntityProperties.acceptURI(command.getLoginId());
        return webClient
                .post()
                .uri(acceptURI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(accept))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(AcceptResponse.class)
                .map(AcceptResponse::getRedirectTo)
                .map(URI::create);
    }
}

package com.example.idp.cloudentity;

import com.example.idp.web.LoginCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
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
    /**
     * The oauth2 client registration id. Don't change this unless you are ready to change the
     * corresponding configuration properties.
     */
    private static final String CLIENT_REGISTRATION_ID = "cloudentity";
    private final CloudEntityProperties cloudEntityProperties;
    private final WebClient webClient;

    public CloudEntityClient(CloudEntityProperties cloudEntityProperties, WebClient webClient) {
        this.cloudEntityProperties = cloudEntityProperties;
        this.webClient = webClient;
    }

    /**
     * POST to the CloudEntity "/accept" url.
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
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(CLIENT_REGISTRATION_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(accept))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(AcceptResponse.class)
                .map(AcceptResponse::getRedirectTo)
                .map(URI::create);
    }

    private Function<LoginCommand, Map<String, Object>> userAttributeService = loginCommand -> {
        var map = new HashMap<String, Object>();
        map.put("my_attribute", "HELLO WORLD!!!!!!!!!!!");

        for (int i = 0; i < 10; i++) {
            map.put(String.format("my_attribute%d", i), String.format("i am my attribute %d", i));
        }
        return map;
    };
}

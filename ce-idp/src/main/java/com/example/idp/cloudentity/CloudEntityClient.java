package com.example.idp.cloudentity;

import com.example.idp.web.LoginCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

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
    public String accept(String subject, LoginCommand command) {
        var accept = new AcceptRequest(subject, command.getLoginState());

        // decorate with custom attributes
        accept.getAuthenticationContext().putAll(userAttributeService.apply(command));

        // create our own netty HttpClient so we have access to tracing the payload
        var httpClient = HttpClient.create().wiretap(true);
        log.trace(toJSON(accept));

        var acceptURI = cloudEntityProperties.acceptURI(command.getLoginId());
        log.trace("Accept {}", acceptURI);
        var result = webClient
                .post()
                .uri(acceptURI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(accept))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(AcceptResponse.class)
                .block();
        assert result != null && result.getRedirectTo() != null : "missing 'redirect_to' in response";
        return result.getRedirectTo();
    }

    /**
     * Fetches a bearer token from token endpoint using client-id and client-secret.
     *
     * @throws RuntimeException if there's an error response from the token service, or if the accessToken
     *                          is null or blank.
     */
//    private Mono<TokenResponse> getAccessToken() {
//        var url = String.format("%s/oauth2/token", authServer);
//        var secret = Base64.getEncoder().encodeToString(String.format("%s:%s", clientId, clientSecret).getBytes());
//        return WebClient.create().method(HttpMethod.POST)
//                .uri(url)
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .header("Authorization", String.format("Basic %s", secret))
//                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .onStatus(HttpStatus::isError, resp -> resp.bodyToMono(String.class).map(Exception::new))
//                .bodyToMono(TokenResponse.class);
//    }
    @SuppressWarnings("java:S112")
    private String toJSON(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

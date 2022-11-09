package com.example.idp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static io.netty.handler.logging.LogLevel.DEBUG;
import static reactor.netty.transport.logging.AdvancedByteBufFormat.TEXTUAL;


@Configuration
@Slf4j
public class OAuth2Configuration {

    /**
     * Configuration of OAuth2 Client.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.oauth2Client();
        return http.build();
    }

    @Bean
    public WebClient cloudEntityWebClient(ReactiveClientRegistrationRepository repository) {
        // see https://github.com/spring-projects/spring-security/issues/11735
        var service = new InMemoryReactiveOAuth2AuthorizedClientService(repository);
        var authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(repository, service);
        var oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauthFilter.setDefaultClientRegistrationId("cloudentity");

        var httpClient = HttpClient
                .create()
                .wiretap("reactor.netty.http.client.HttpClient", DEBUG, TEXTUAL);
        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(oauthFilter)
                .build();
    }
}

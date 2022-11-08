package com.example.idp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
@Slf4j
public class OAuth2Configuration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .oauth2Client();

        return http.build();
    }

    @Bean
    public WebClient webClient(ReactiveClientRegistrationRepository repository) {
        // see https://github.com/spring-projects/spring-security/issues/11735
//        var registration = repository.findByRegistrationId("cloudentity").block();
        var service = new InMemoryReactiveOAuth2AuthorizedClientService(repository);
        var authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(repository, service);
        var oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauthFilter.setDefaultClientRegistrationId("cloudentity");
        return WebClient.builder().filter(oauthFilter).build();

//        InMemoryReactiveOAuth2AuthorizedClientService(
    }

//    @Bean
//    WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
//        var oauth2Client = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
//        return WebClient.builder()
//                .filter(oauth2Client)
//                .build();
//    }
//
//    @Bean
//    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
//            ReactiveClientRegistrationRepository clientRegistrationRepository,
//            ReactiveOAuth2AuthorizedClientService authorizedClientService) {
//        var clientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
//                .clientCredentials()
//                .refreshToken()
//                .build();
//
//        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
//        authorizedClientManager.setAuthorizedClientProvider(clientProvider);
//        return authorizedClientManager;
//    }
}

package com.example.mylogin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.HashSet;
import java.util.Set;

@Configuration

public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        Set<String> myScopes = new HashSet<>();
        myScopes.add("email");
        myScopes.add("oidc");

        OidcUserService userService = new OidcUserService();
        userService.setAccessibleScopes(myScopes);

        http.csrf().disable()
                .authorizeHttpRequests(a -> a.anyRequest().authenticated())
                .oauth2Login(customizer -> {
                    // see https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html
                    customizer.userInfoEndpoint().oidcUserService(userService);
                    customizer.successHandler(new RefererRedirectionAuthenticationSuccessHandler());
                }).logout();
        return http.build();
    }

    /**
     * This WebClient is to be injected into components which need to make calls
     * as an OAuth2 client. This WebClient is the same as the examples
     * from the Spring Boot documentation, except it uses a Netty HTTP client
     * to allow us to wiretap the payloads.
     */
    @Bean
    public WebClient webClient(ClientRegistrationRepository clients, OAuth2AuthorizedClientRepository clientRepository) {
        var httpClient = HttpClient.create().wiretap(true);
        var oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, clientRepository);
        return WebClient
                .builder()
                .filter(oauth2)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository) {
        var authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .clientCredentials()
                        .password()
                        .build();

        var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    static class RefererRedirectionAuthenticationSuccessHandler
            extends SimpleUrlAuthenticationSuccessHandler
            implements AuthenticationSuccessHandler {
        public RefererRedirectionAuthenticationSuccessHandler() {
            super();
            setUseReferer(true);
        }

    }
}
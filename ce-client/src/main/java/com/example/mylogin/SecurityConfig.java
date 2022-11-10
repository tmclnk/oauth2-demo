package com.example.mylogin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        Set<String> myScopes = new HashSet<>();
        myScopes.add("email");
        myScopes.add("oidc");

        OidcUserService userService = new OidcUserService();
        userService.setAccessibleScopes(myScopes);

        http.csrf().disable()
                .authorizeExchange(e -> e.anyExchange().authenticated())
                .oauth2Login(customizer -> {
                    // see https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html
//                    customizer.userInfoEndpoint().oidcUserService(userService);
//                    customizer.successHandler(new RefererRedirectionAuthenticationSuccessHandler());
                })
                .oauth2Client()
                .and().logout();
        return http.build();
    }

    /**
     * This WebClient is to be injected into components which need to make calls
     * as an OAuth2 client. This WebClient is the same as the examples
     * from the Spring Boot documentation, except it uses a Netty HTTP client
     * to allow us to wiretap the payloads.
     *
     * @see <a href="https://docs.spring.io/spring-security/reference/5.7.4/reactive/oauth2/client/authorized-clients.html#oauth2Client-webclient-webflux">Authorized Clients</a>
     */
    @Bean
    public WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        var httpClient = HttpClient.create().wiretap(true);
        var fn = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        return WebClient
                .builder()
                .filter(fn)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    /**
     * @see <a href="https://docs.spring.io/spring-security/reference/5.7.4/reactive/oauth2/client/index.html">Reactive OAuth2 Client</a>
     */
    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {

        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
                ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .clientCredentials()
                        .password()
                        .build();

        DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager;
        authorizedClientManager = new DefaultReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
//    @Bean
//    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository) {
//        var authorizedClientProvider =
//                OAuth2AuthorizedClientProviderBuilder.builder()
//                        .authorizationCode()
//                        .refreshToken()
//                        .clientCredentials()
//                        .password()
//                        .build();
//
//        var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
//        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//
//        return authorizedClientManager;
//    }

//    static class RefererRedirectionAuthenticationSuccessHandler
//            extends SimpleUrlAuthenticationSuccessHandler
//            implements AuthenticationSuccessHandler {
//        public RefererRedirectionAuthenticationSuccessHandler() {
//            super();
//            setUseReferer(true);
//        }
//
//    }
}
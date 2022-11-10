package com.example.idp;

import com.example.idp.cloudentity.CloudEntityProperties;
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
import static reactor.netty.transport.logging.AdvancedByteBufFormat.SIMPLE;

@Configuration
@Slf4j
public class OAuth2Configuration {

    /**
     * Wire OAuth handling into http processing.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.oauth2Client();
        return http.build();
    }

    /**
     * Creates an OAuth2-enabled {@link WebClient}.
     *
     * @see <a href="https://docs.spring.io/spring-security/reference/5.7.4/reactive/oauth2/client/authorized-clients.html">Authorized Clients</a>
     * @see <a href="https://github.com/spring-projects/spring-security/issues/11735">Spring Security #11735</a>
     */
    @Bean
    public WebClient cloudEntityWebClient(ReactiveClientRegistrationRepository repository, CloudEntityProperties cloudEntityProperties) {
        var service = new InMemoryReactiveOAuth2AuthorizedClientService(repository);
        var authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(repository, service);
        var oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        // configure client so we can dump payloads to the logs
        var httpClient = HttpClient
                .create()
                .wiretap("reactor.netty.http.client.HttpClient", DEBUG, SIMPLE);
        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(oauthFilter)
                .build();
    }
}

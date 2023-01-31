package com.example.rs;

import com.example.rs.impersonation.ImpersonationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RoutingConfiguration {
    private static final RequestPredicate ACCEPT_JSON = accept(MediaType.APPLICATION_JSON);

    @Bean
    public RouterFunction<ServerResponse> monoRouterFunction(ImpersonationHandler impHandler) {
        return route()
                .GET("/impersonation", ACCEPT_JSON, impHandler::get)
                .PUT("/impersonation/{agilityUsername}", ACCEPT_JSON, impHandler::put)
                .build();
    }
}

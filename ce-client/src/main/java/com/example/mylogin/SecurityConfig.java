package com.example.mylogin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
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

    static class RefererRedirectionAuthenticationSuccessHandler
            extends SimpleUrlAuthenticationSuccessHandler
            implements AuthenticationSuccessHandler {

        public RefererRedirectionAuthenticationSuccessHandler() {
            super();
            setUseReferer(true);
        }

    }
}
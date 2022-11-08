package com.example.mylogin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Just dump auth data as JSON.
 */
@RestController
public class OAuthController {
    @GetMapping("/oidc")
    public OidcUser getOidcUserPrincipal(@AuthenticationPrincipal OidcUser principal) {
        return principal;
    }

    @GetMapping("/token")
    public OAuth2AuthenticationToken getToken(OAuth2AuthenticationToken token) {
        return token;
    }

    @GetMapping("/")
    public RedirectView index(@AuthenticationPrincipal OAuth2User principal) {
        return new RedirectView("/person");
//        return principal;
    }

}

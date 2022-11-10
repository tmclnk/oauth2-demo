package com.example.webclient;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "user-service")
@Getter
@Setter
@Validated
public class UserServiceProperties {
    /**
     * Base uri for the Users resource service.
     */
    @NotBlank
    private String baseUri;
}

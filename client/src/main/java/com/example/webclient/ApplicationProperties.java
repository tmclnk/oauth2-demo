package com.example.webclient;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "ip5k")
@Getter
@Setter
@Validated
public class ApplicationProperties {

    @NotBlank
    private String impersonationUrl;

}

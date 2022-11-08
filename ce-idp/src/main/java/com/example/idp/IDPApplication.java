package com.example.idp;

import com.example.idp.cloudentity.CloudEntityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@EnableWebFluxSecurity
@EnableConfigurationProperties(CloudEntityProperties.class)
public class IDPApplication {
    public static void main(String[] args) {
        SpringApplication.run(IDPApplication.class, args);
    }
}

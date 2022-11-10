package com.example.idp.cloudentity;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "cloudentity")
public class CloudEntityProperties {
    @Setter
    @NotBlank
    private String tenantId;

    public URI acceptURI(String loginId) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("{tenant-id}.us.authz.cloudentity.io")
                .pathSegment("api", "system", "{tenant-id}", "logins", "{login-id}", "accept")
                .buildAndExpand(Map.of("tenant-id", this.tenantId, "login-id", loginId))
                .toUri();
    }
}
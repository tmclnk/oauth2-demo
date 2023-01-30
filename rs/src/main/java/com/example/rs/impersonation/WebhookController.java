package com.example.rs.impersonation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@Slf4j
public class WebhookController {

    @Autowired
    private ImpersonationService impersonationService;

    @PostMapping("/webhook")
    public Mono<WebhookPayload> webhook(@RequestBody String r) throws IOException {
        ObjectMapper om = new ObjectMapper();
        var node = om.readTree(r.getBytes(StandardCharsets.UTF_8));
        var subject = node.get("data").get("identity").get("claims").get("sub").textValue();
        var impersonationTargetId = impersonationService.getImpersonation(subject);
        log.info("{}", subject);
        if(impersonationTargetId != null) {
            return Mono.just(new WebhookPayload(new WebhookPayload.Command("com.okta.identity.patch", new WebhookPayload.Value("add", "/claims/impersonate", impersonationTargetId))));
        } else {
            return Mono.just(new WebhookPayload());
        }
    }
}

package com.example.rs.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

    @GetMapping("/users/{id}")
    public User get(@PathVariable Long id, Authentication auth) {
        auth.getAuthorities().stream().forEach(a -> log.info("{}", a.getAuthority()));
        var person = new User();
        person.setId(id);
        return person;
    }

    @PreAuthorize("hasAuthority('SCOPE_profile')")
    @GetMapping("/secure/users/{id}")
    public User secureGet(@PathVariable Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var person = new User();
        person.setId(id);
        person.setFirstName("322");
        return person;
    }

    @GetMapping("/jwt")
    public Authentication jwt() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth;
    }

}

package com.example.rs.profile.person;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    @GetMapping("/person/{id}")
    public Person get(@PathVariable Long id) {
        var person = new Person();
        person.setId(id);
        return person;
    }

    @PreAuthorize("hasAuthority('SCOPE_profile')")
    @GetMapping("/secure/person/{id}")
    public Person secureGet(@PathVariable Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var person = new Person();
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

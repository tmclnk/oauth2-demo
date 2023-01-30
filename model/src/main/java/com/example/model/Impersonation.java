package com.example.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Impersonation {
    private String subject;
    private String target;
    public Impersonation(String subject, String target) {
        this.subject = subject;
        this.target = target;
    }

}

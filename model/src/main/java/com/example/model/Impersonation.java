package com.example.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Impersonation {
    private String subject;

    private String agilityUsername;
    public Impersonation(String subject, String agilityUsername) {
        this.subject = subject;
        this.agilityUsername = agilityUsername;
    }

    /** use #getAgilityUsername */
    @Deprecated
    public String getTarget() {
        return agilityUsername;
    }

    /** use #setAgilityUsername */
    @Deprecated
    public void setTarget(String target) {
        this.agilityUsername = target;
    }
}

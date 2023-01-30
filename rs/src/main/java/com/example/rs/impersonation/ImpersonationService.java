package com.example.rs.impersonation;

import com.example.model.Impersonation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ImpersonationService {
    private final Map<String, Impersonation> map = new ConcurrentHashMap<>();

    void prepareImpersonation(String subject, String username) {
        map.put(subject, new Impersonation(subject, username));
    }

    public Impersonation getImpersonation(String subject) {
        return map.get(subject);
    }
}

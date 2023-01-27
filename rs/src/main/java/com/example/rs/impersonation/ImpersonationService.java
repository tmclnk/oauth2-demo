package com.example.rs.impersonation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
class ImpersonationService {
    private final Map<String, String> map = new ConcurrentHashMap<>();

    void prepareImpersonation(String principal, String username) {
        map.put(principal, username);
    }

    String getImpersonation(String principal) {
        return map.get(principal);
    }

    void clearImpersonation(String principal) {
        map.remove(principal);
    }
}

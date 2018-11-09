package com.experiment.passwordprocessor.service.helper;

import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.UUID;

@Component
public class TokenGenerationHelper {

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public String resolveUrl(String endpoint, String token) {
        try {
            return endpoint + URLEncoder.encode(token, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("error occurred while generating url", e);
        }
    }
}

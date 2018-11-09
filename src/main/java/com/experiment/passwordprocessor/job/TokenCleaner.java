package com.experiment.passwordprocessor.job;

import com.experiment.passwordprocessor.service.TokenProcessorService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TokenCleaner {

    private final TokenProcessorService tokenProcessorService;


    @Scheduled(cron = "${token.cleaning.expression}")
    protected final void removeExpiredTokens() {
        tokenProcessorService.deleteExpiredTokens();
    }

}

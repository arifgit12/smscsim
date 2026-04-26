package com.smpp.smscsim.config;

import com.smpp.smscsim.spring.auto.SmppSessionManager;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class SmscHealthIndicator implements HealthIndicator {

    private final SmppSessionManager sessionManager;

    public SmscHealthIndicator(SmppSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail("activeSessions", sessionManager.getSessionCount())
                .build();
    }
}

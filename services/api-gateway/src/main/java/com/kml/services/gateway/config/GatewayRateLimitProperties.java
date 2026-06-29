package com.kml.services.gateway.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kml.gateway.rate-limit")
public class GatewayRateLimitProperties {

    private int capacity = 600;
    private Duration window = Duration.ofMinutes(1);

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Gateway rate-limit capacity must be at least 1");
        }
        this.capacity = capacity;
    }

    public Duration getWindow() {
        return window;
    }

    public void setWindow(Duration window) {
        if (window == null || window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("Gateway rate-limit window must be positive");
        }
        this.window = window;
    }
}

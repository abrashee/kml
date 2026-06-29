package com.kml.services.gateway.config;

import com.kml.services.gateway.security.GatewayRateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRateLimitRegistrationConfig {

    @Bean
    GatewayRateLimitFilter gatewayRateLimitFilter(GatewayRateLimitProperties properties) {
        return new GatewayRateLimitFilter(properties);
    }

    @Bean
    FilterRegistrationBean<GatewayRateLimitFilter> disableGatewayRateLimitServletRegistration(
        GatewayRateLimitFilter gatewayRateLimitFilter) {
        FilterRegistrationBean<GatewayRateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(gatewayRateLimitFilter);
        registration.setEnabled(false);
        return registration;
    }
}

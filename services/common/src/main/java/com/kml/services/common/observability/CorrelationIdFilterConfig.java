package com.kml.services.common.observability;

import io.micrometer.tracing.Tracer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CorrelationIdFilterConfig {

    @Bean
    FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter(Tracer tracer) {
        FilterRegistrationBean<CorrelationIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorrelationIdFilter(tracer));
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }
}

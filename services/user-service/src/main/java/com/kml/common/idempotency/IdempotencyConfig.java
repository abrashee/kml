package com.kml.common.idempotency;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdempotencyConfig {

  @Bean
  public FilterRegistrationBean<IdempotencyFilter> idempotencyFilterRegistration() {
    FilterRegistrationBean<IdempotencyFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new IdempotencyFilter());
    registration.addUrlPatterns("/api/v1/orders/*", "/api/v1/shipments/*");
    registration.setName("idempotencyFilter");
    registration.setOrder(1);
    return registration;
  }
}

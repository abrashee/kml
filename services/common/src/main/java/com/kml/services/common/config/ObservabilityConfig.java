package com.kml.services.common.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

@Configuration
public class ObservabilityConfig {

  @Bean
  public ObservationPredicate skipLowValueServerObservations() {
    return (name, context) -> {
      if (context instanceof ServerRequestObservationContext serverContext) {
        String path = serverContext.getCarrier().getRequestURI();
        return !path.startsWith("/actuator")
            && !path.startsWith("/swagger-ui")
            && !path.startsWith("/api-docs")
            && !path.startsWith("/v3/api-docs")
            && !path.equals("/favicon.ico");
      }
      return true;
    };
  }
}

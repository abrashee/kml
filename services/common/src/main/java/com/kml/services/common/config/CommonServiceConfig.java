package com.kml.services.common.config;

import com.kml.services.common.ServiceUrls;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ServiceUrls.class)
public class CommonServiceConfig {
}

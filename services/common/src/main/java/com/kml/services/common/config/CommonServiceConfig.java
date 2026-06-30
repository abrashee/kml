package com.kml.services.common.config;

import com.kml.services.common.ServiceUrls;
import com.kml.services.common.security.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ServiceUrls.class, JwtProperties.class})
public class CommonServiceConfig {
}

package com.kml.services.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.kml.services",
    "com.kml.auth",
    "com.kml.audit",
    "com.kml.common",
    "com.kml.config",
    "com.kml.security",
    "com.kml.user"
})
@EntityScan(basePackages = {
    "com.kml.audit",
    "com.kml.user"
})
@EnableJpaRepositories(basePackages = {
    "com.kml.audit",
    "com.kml.user"
})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

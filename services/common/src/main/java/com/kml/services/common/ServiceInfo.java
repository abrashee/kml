package com.kml.services.common;

import java.time.Instant;
import java.util.Map;

public record ServiceInfo(
    String service,
    String boundary,
    String status,
    Instant timestamp,
    Map<String, String> dependencies) {

    public static ServiceInfo ready(String service, String boundary, Map<String, String> dependencies) {
        return new ServiceInfo(service, boundary, "ready", Instant.now(), dependencies);
    }
}

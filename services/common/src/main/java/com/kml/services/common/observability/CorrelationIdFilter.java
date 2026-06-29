package com.kml.services.common.observability;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Correlation-ID";

    private final Tracer tracer;

    public CorrelationIdFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String correlationId = request.getHeader(HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        response.setHeader(HEADER, correlationId);
        MDC.put("correlationId", correlationId);

        Span span = tracer.currentSpan();
        if (span != null) {
            MDC.put("traceId", span.context().traceId());
            MDC.put("spanId", span.context().spanId());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
}

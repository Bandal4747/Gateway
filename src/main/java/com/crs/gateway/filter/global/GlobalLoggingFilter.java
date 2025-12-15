package com.crs.gateway.filter.global;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalLoggingFilter.class);

    private static final String CORRELATION_ID = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        ServerHttpRequest request = exchange.getRequest();

        // 1️ Get or generate Correlation ID
        String headerCorrelationId = request.getHeaders().getFirst(CORRELATION_ID);
        final String correlationId = 
                (headerCorrelationId == null || headerCorrelationId.isBlank())
                        ? UUID.randomUUID().toString()
                        : headerCorrelationId;

        // 2️ Mutate request
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CORRELATION_ID, correlationId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        // 3️ Add Correlation ID to response
        mutatedExchange.getResponse()
                .getHeaders()
                .add(CORRELATION_ID, correlationId);

        // 4️ Log request
        log.info(
                "INCOMING REQUEST | correlationId={} | method={} | path={}",
                correlationId,
                mutatedRequest.getMethod(),
                mutatedRequest.getURI().getPath()
        );

        // 5️⃣ Continue filter chain
        return chain.filter(mutatedExchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info(
                            "OUTGOING RESPONSE | correlationId={} | status={} | time={}ms",
                            correlationId,
                            mutatedExchange.getResponse().getStatusCode(),
                            duration
                    );
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error(
                            "REQUEST FAILED | correlationId={} | status={} | time={}ms | error={}",
                            correlationId,
                            mutatedExchange.getResponse().getStatusCode(),
                            duration,
                            error.getMessage(),
                            error
                    );
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

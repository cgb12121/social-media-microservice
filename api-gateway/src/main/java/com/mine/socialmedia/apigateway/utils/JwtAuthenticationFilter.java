package com.mine.socialmedia.apigateway.utils;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new RuntimeException("Authorization header is missing or invalid"));
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtUtil.isTokenExpired(token)) {
                String userId = jwtUtil.extractUserName(token);
                List<String> roles = jwtUtil.extractClaims(token, claims -> claims.get("authority", List.class));

                ServerHttpRequest request = exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Roles", String.join(",", roles))
                        .build();

                return chain.filter(exchange.mutate().request(request).build());
            } else {
                return Mono.error(new RuntimeException("JWT Token is expired"));
            }
        } catch (Exception e) {
            return Mono.error(new RuntimeException("JWT validation failed: " + e.getMessage()));
        }
    }

}

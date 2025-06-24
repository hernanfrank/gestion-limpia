package com.burbujas.gestionlimpia.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {
    private final Bucket bucket;

    public RateLimiterInterceptor() {
        Bandwidth limit = Bandwidth.simple(20, Duration.ofSeconds(1));
        this.bucket = Bucket.builder()
            .addLimit(limit)
            .build();
    }

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        if (!bucket.tryConsume(1)) {
            response.setStatus(429); // Too Many Requests
            return false;
        }
        return true;
    }
}
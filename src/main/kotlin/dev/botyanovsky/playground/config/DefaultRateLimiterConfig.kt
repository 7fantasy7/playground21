package dev.botyanovsky.playground.config

import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Duration

@Configuration
class DefaultRateLimiterConfig {

    @Bean
    @Primary
    fun defaultApiRateLimiter(): RateLimiterRegistry {
        val config: RateLimiterConfig = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(20))
            .limitForPeriod(50)
            .timeoutDuration(Duration.ofMillis(0))
            .build()

        val rateLimiterRegistry: RateLimiterRegistry = RateLimiterRegistry.of(config)

        return rateLimiterRegistry
    }

}
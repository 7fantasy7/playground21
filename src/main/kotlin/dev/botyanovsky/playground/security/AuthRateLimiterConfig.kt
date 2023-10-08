package dev.botyanovsky.playground.security

import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class AuthRateLimiterConfig {

    @Bean
    fun loginRateLimiter(): RateLimiterRegistry {
        val config: RateLimiterConfig = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMinutes(10))
            .limitForPeriod(5)
            .timeoutDuration(Duration.ofMillis(0))
            .build()

        return RateLimiterRegistry.of(config)
    }

    @Bean
    fun refreshRateLimiter(): RateLimiterRegistry {
        val config: RateLimiterConfig = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMinutes(30))
            .limitForPeriod(3)
            .timeoutDuration(Duration.ofMillis(0))
            .build()

        return RateLimiterRegistry.of(config)
    }

}
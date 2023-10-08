package dev.botyanovsky.playground.garbage;

import feign.FeignException
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Primary
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(
    name = "worldtime",
    url = "http://worldtimeapi.org",
    fallback = SomeFeignClientFallback::class
)
@Primary
interface SomeFeignClient {

    @Retryable(maxAttempts = 2, noRetryFor = [FeignException.TooManyRequests::class])
    @GetMapping("/api/timezone/Europe/Govno")
    fun getTime(): Any

}

@Component
class SomeFeignClientFallback : SomeFeignClient {
    override fun getTime(): Any {
        return mutableMapOf<String, Any>()
    }
}
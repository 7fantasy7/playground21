package dev.botyanovsky.playground.garbage

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class SomeCachedExternalResource {

    data class SomeContext(val value: String)

    private val CONTEXT: ScopedValue<SomeContext> = ScopedValue.newInstance()

    @Cacheable("default")
    fun cached(): String {
        ScopedValue.where(CONTEXT, SomeContext(value = "hello")).run { go() }
        Thread.sleep(Duration.ofSeconds(3))
        return "CACHED"
    }

    fun go() {
        println(CONTEXT.get())
    }
}
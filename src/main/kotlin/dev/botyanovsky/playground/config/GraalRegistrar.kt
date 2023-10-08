package dev.botyanovsky.playground.config

import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

class GraalRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.resources()
            .registerPattern("rsa.private.key")
            .registerPattern("rsa.public.key")

    }
}
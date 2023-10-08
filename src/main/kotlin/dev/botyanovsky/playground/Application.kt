package dev.botyanovsky.playground

import dev.botyanovsky.playground.config.GraalRegistrar
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableCaching
@EnableRetry
@ImportRuntimeHints(GraalRegistrar::class)
class Application

// todo customize springdoc? https://springdoc.org/index.html#migrating-from-springfox

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}



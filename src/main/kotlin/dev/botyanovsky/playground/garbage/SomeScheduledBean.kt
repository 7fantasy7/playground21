package dev.botyanovsky.playground.garbage

import dev.botyanovsky.playground.utils.Logger
import org.springframework.stereotype.Component

private val logger = Logger.logger {}

@Component
class SomeScheduledBean {

    //    @Scheduled(initialDelay = 1 * 1000, fixedDelay = 5 * 1000)
    fun scheduled() {
        logger.info("scheduled")
    }

}
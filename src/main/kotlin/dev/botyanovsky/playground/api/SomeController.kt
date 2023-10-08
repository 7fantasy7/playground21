package dev.botyanovsky.playground.api

import dev.botyanovsky.playground.domain.SomeEntity
import dev.botyanovsky.playground.garbage.SomeCachedExternalResource
import dev.botyanovsky.playground.garbage.SomeFeignClient
import dev.botyanovsky.playground.repository.SomeRepository
import dev.botyanovsky.playground.utils.Logger
//import io.swagger.v3.oas.annotations.Operation
//import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService

private val logger = Logger.logger {}

//@Tag(name = "Some")
//@RestController
class SomeController(
    val someRepository: SomeRepository,
    val transactionTemplate: TransactionTemplate,
    val someFeignClient: SomeFeignClient,
    val virtualExecutor: ExecutorService,
    val someCachedExternalResource: SomeCachedExternalResource
) {

//    @Operation(summary = "Something", description = "tralala")
//    @GetMapping("/hello")
    fun hello(authentication: Authentication): Map<String, Any> {
//        val timeNow: Future<*> = virtualExecutor.submit(Callable {
//            measureTimeMillis({ time -> logger.info("Get time took $time") }) {
//                someFeignClient.getTime()
//            }
//        })

        logger.info("Added before building graal image")

        val cached = virtualExecutor.submit(Callable {
            measureTimeMillis({ time -> logger.info("Cached resource took $time") }) {
                someCachedExternalResource.cached()
            }
        })

        val someEntity = SomeEntity()

        measureTimeMillis({ time -> logger.info("Entity save took $time") }) {
            transactionTemplate.run {
                someEntity.name = "abc"
                someRepository.save(someEntity)
            }
        }


//        logger.debug(Thread.currentThread().isVirtual.toString())

        return mutableMapOf<String, Any>(
            "str" to "Hello, world! ${someEntity.id}",
//            "time" to timeNow.get(),
            "cached" to cached.get()
        )
    }

    private inline fun <T> measureTimeMillis(
        loggingFunction: (Long) -> Unit,
        function: () -> T
    ): T {

        val startTime = System.currentTimeMillis()
        val result: T = function.invoke()
        loggingFunction.invoke(System.currentTimeMillis() - startTime)

        return result
    }
}
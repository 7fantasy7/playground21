package dev.botyanovsky.playground.api.util

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


// https://github.com/Yoh0xFF/java-spring-security-example/blob/68cb5f8af40bfe18bbaaafa0b59c46d46316d1d4/src/main/java/io/example/configuration/GlobalExceptionHandler.java

@ControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {

    private val ise: String = "Internal Server Error"

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException, request: WebRequest
    ): ResponseEntity<*> {
        val errors: MutableList<String> = ArrayList()
        for (violation: ConstraintViolation<*> in ex.constraintViolations) {
            errors.add(
                violation.rootBeanClass.name + " " +
                        violation.propertyPath + ": " + violation.message
            )
        }
        val apiError = ApiError(
            (request as ServletWebRequest).request.requestURI.toString(),
            HttpStatus.BAD_REQUEST, ex.localizedMessage, errors
        )

        logger.error(apiError, ex)

        return ResponseEntity(apiError, HttpHeaders(), apiError.status)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors: MutableList<String> = ArrayList()
        for (error in ex.bindingResult.fieldErrors) {
            errors.add(error.field + ": " + error.defaultMessage)
        }
        for (error in ex.bindingResult.globalErrors) {
            errors.add(error.objectName + ": " + error.defaultMessage)
        }

        val apiError = ApiError(
            (request as ServletWebRequest).request.requestURI.toString(),
            HttpStatus.BAD_REQUEST, ise, errors
        )

        logger.error(apiError, ex)

        return ResponseEntity(apiError, HttpHeaders(), apiError.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception, request: WebRequest?): ResponseEntity<Any?> {
        ex.printStackTrace()

        val apiError = ApiError(
            (request as ServletWebRequest).request.requestURI.toString(),
            HttpStatus.INTERNAL_SERVER_ERROR, ex.localizedMessage, ise
        )

        logger.error(apiError, ex)

        return ResponseEntity(apiError, HttpHeaders(), apiError.status)
    }

}
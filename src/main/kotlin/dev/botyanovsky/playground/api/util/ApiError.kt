package dev.botyanovsky.playground.api.util

import org.springframework.http.HttpStatus

class ApiError(
    val requestUrl: String,
    val status: HttpStatus,
    val message: String,
    val errors: List<String>
) {
    constructor(requestUrl: String, status: HttpStatus, message: String, error: String)
            : this(requestUrl, status, message, listOf(error))
}

package org.waterfogsw.httpinterface.client

import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.DeleteExchange
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import org.springframework.web.service.annotation.PutExchange
import org.waterfogsw.httpinterface.domain.User

@HttpExchange
interface UserClient {
    @GetExchange("/users/{id}")
    fun getUser(@PathVariable id: Long): User

    @PostExchange("/users")
    fun createUser(@RequestBody user: User): User

    @Retryable(
        include = [RuntimeException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000)
    )
    @GetExchange("/users/retry/{id}")
    fun getUserWithRetry(
        @PathVariable id: Long,
        @RequestParam("failCount", required = false) failCount: Int?
    ): User

    @GetExchange("/users/error/{id}")
    fun getUserWithError(
        @PathVariable id: Long,
        @RequestParam("errorType") errorType: String
    ): User

    @DeleteExchange("/users/{id}")
    fun deleteUser(@PathVariable id: Long)

    @PutExchange("/users/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody user: User
    ): User

    @GetExchange("/users/timeout/{id}")
    fun getUserWithTimeout(
        @PathVariable id: Long,
        @RequestParam("delayMs") delayMs: Long
    ): User
}

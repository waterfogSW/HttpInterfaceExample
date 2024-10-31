package org.waterfogsw.httpinterface.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.waterfogsw.httpinterface.domain.User
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

@RestController
@RequestMapping("/users")
class MockUserController {

    private val userStore = ConcurrentHashMap<Long, User>()
    private val retryCountMap = ConcurrentHashMap<Long, Int>()

    // 기본 CRUD 작업
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): User {
        return userStore[id] ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody user: User): User {
        if (userStore.containsKey(user.id)) {
            throw ResponseStatusException(HttpStatus.CONFLICT)
        }
        userStore[user.id] = user
        return user
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: Long) {
        if (!userStore.containsKey(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        userStore.remove(id)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User): User {
        if (!userStore.containsKey(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        userStore[id] = user.copy(id = id)
        return userStore[id]!!
    }

    // 재시도 로직 테스트
    @GetMapping("/retry/{id}")
    fun getUserWithRetry(
        @PathVariable id: Long,
        @RequestParam("failCount", required = false) failCount: Int?
    ): User {
        val currentCount = retryCountMap.compute(id) { _, count -> (count ?: 0) + 1 }!!
        val maxFailCount = failCount ?: 2

        if (currentCount <= maxFailCount) {
            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE)
        }

        retryCountMap.remove(id)
        return userStore.getOrDefault(id, createDefaultUser(id))
    }

    // 에러 케이스 테스트
    @GetMapping("/error/{id}")
    fun getUserWithError(
        @PathVariable id: Long,
        @RequestParam("errorType") errorType: String
    ): User {
        when (errorType.uppercase()) {
            "NOT_FOUND" -> throw ResponseStatusException(HttpStatus.NOT_FOUND)
            "UNAUTHORIZED" -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            "BAD_REQUEST" -> throw ResponseStatusException(HttpStatus.BAD_REQUEST)
            "RANDOM_ERROR" -> if (Random.nextBoolean()) throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
            "CONFLICT" -> throw ResponseStatusException(HttpStatus.CONFLICT)
        }

        return userStore.getOrDefault(id, createDefaultUser(id))
    }

    // 타임아웃 테스트
    @GetMapping("/timeout/{id}")
    fun getUserWithTimeout(
        @PathVariable id: Long,
        @RequestParam("delayMs", defaultValue = "5000") delayMs: Long
    ): User {
        Thread.sleep(delayMs)
        return userStore.getOrDefault(id, createDefaultUser(id))
    }

    private fun createDefaultUser(id: Long) = User(
        id = id,
        name = "User $id",
        age = Random.nextInt(20, 50)
    )

    // 테스트 데이터 초기화
    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun reset() {
        userStore.clear()
        retryCountMap.clear()
    }

    // 테스트 데이터 생성
    @PostMapping("/sample-data")
    @ResponseStatus(HttpStatus.CREATED)
    fun createSampleData() {
        (1L..5L).forEach { id ->
            userStore[id] = createDefaultUser(id)
        }
    }
}
